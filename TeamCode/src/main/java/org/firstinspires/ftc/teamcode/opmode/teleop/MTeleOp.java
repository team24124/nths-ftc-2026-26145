package org.firstinspires.ftc.teamcode.opmode.teleop;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.util.PIDF;

/**
 * TeleOp mode for the robot.
 * Annotated with @TeleOp to appear in the driver station OpMode list.
 * Annotated with @Config to allow variable tuning via FTC Dashboard.
 */
//@TeleOp(name = "MTeleOp", group = "!")
@Config
public class MTeleOp extends LinearOpMode {
    //// --- Tuning Constants (Editable in Dashboard) ---
    public static double maxTurretPower = 1250; // Target velocity (ticks/sec) for the flywheel
    public static long servoFeedTime = 300;     // Duration (ms) to run the feeder servos for one shot
    public static long waitBack = 100;          // (Unused) Potential wait time
    public static double kp = 0.01;             // PIDF Proportional Coefficient
    public static double kv = 0.0005;           // PIDF Velocity/Feedforward Coefficient

    public static double voltOffset = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        //// --- Gamepad State Initialization ---
        // Used to detect rising edges (button presses vs holds)

        Gamepad prevgp1 = new Gamepad();
        Gamepad curgp1 = new Gamepad();

        Gamepad prevgp2 = new Gamepad();
        Gamepad curgp2 = new Gamepad();

        //// --- Hardware Declaration & Mapping ---

        // Drivebase Motors
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("leftBack");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rightBack");

        // Variables to store calculated motor powers
        double frontLeftPower = 0;
        double frontRightPower = 0;
        double backLeftPower = 0;
        double backRightPower = 0;

        //// Turret / Flywheel Motor

        // Using DcMotorEx to access velocity features
        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class,"turretAccelerator");
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE); // Reverse flywheel as it spins backwards by default

        // Feeder Servos (Continuous Rotation)
        CRServo leftFeeder = hardwareMap.get(CRServo.class, "leftFeeder");
        CRServo rightFeeder = hardwareMap.get(CRServo.class, "rightFeeder");

        // Turret control variables
        double turretAccelPower = 0;  // calculated turret power
        double prevPower = 0;
        double prevV = 0;

        PIDF pv = new PIDF(); // Instance of custom PIDF controller

        boolean primed = false; // Indicates if flywheel is at target speed

        // Voltage Sensor (used for PIDF voltage compensation)
        VoltageSensor voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");

        //// --- Motor Configuration ---

        // Reverse right-side motors so positive power moves the robot forward
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set Zero Power Behavior to BRAKE (motors stop quickly when power is 0)
        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        flywheel.setZeroPowerBehavior(BRAKE);

        ElapsedTime timer = new ElapsedTime(); // Timer for scheduled events

        waitForStart(); // Wait for driver to press PLAY

        // --- Main OpMode Loop ---
        while (opModeIsActive()) {
            if (isStopRequested()) return; // Stop if driver presses STOP

            // Update PIDF constants from Config variables (allows real-time tuning)
            pv.setPV(kp, kv);

            // Update Gamepad history for toggle logic
            prevgp1.copy(curgp1);
            curgp1.copy(gamepad1);
            prevgp2.copy(curgp2);
            curgp2.copy(gamepad2);

            //// --- Drivebase Logic ---

            // Mecanum Drive Calculation

            double y = -gamepad1.left_stick_y; // Forward/Back (Negative because up is negative on stick)
            double x = gamepad1.left_stick_x * 1.1; // Strafe (Multiplied to fix imperfect strafing mechanics)
            double rx = gamepad1.right_stick_x; // Turn

            // Calculate Mecanum powers
            // Denominator ensures powers are scaled correctly if they exceed 1.0
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            frontLeftPower = ((y + x + rx) / denominator);
            backLeftPower = ((y - x + rx) / denominator);
            frontRightPower = ((y - x - rx) / denominator);
            backRightPower = ((y + x - rx) / denominator);

            if (gamepad1.dpad_down) {
                frontLeftPower = -0.1;
                frontRightPower = -0.1;
                backLeftPower = -0.1;
                backRightPower = -0.1;
            } else if (gamepad1.dpad_up) {
                frontLeftPower = 0.1;
                frontRightPower = 0.1;
                backLeftPower = 0.1;
                backRightPower = 0.1;
            } else if (gamepad1.dpad_left) {
                frontLeftPower = -0.1;
                frontRightPower = 0.1;
                backLeftPower = 0.1;
                backRightPower = -0.1;
            } else if (gamepad1.dpad_right) {
                frontLeftPower = 0.1;
                frontRightPower = -0.1;
                backLeftPower = -0.1;
                backRightPower = 0.1;
            } else if (gamepad1.right_bumper) {
                frontLeftPower = 0.1;
                frontRightPower = -0.1;
                backLeftPower = 0.1;
                backRightPower = -0.1;
            } else if (gamepad1.left_bumper) {
                frontLeftPower = -0.1;
                frontRightPower = 0.1;
                backLeftPower = -0.1;
                backRightPower = 0.1;
            }

            //// --- Turret / Shooter Logic ---

            // Rev up flywheel when Right Bumper is held on Gamepad 2
            if (gamepad2.right_bumper) {
                // Calculate power needed to maintain target velocity using PIDF
                turretAccelPower = pv.calculate(flywheel.getVelocity(), maxTurretPower, voltageSensor.getVoltage() + voltOffset);
                
                // Check if flywheel velocity is within tolerance (+/- 20 ticks) of target
                primed = flywheel.getVelocity() < maxTurretPower + 20 && flywheel.getVelocity() > maxTurretPower - 20;
            } else {
                // Shutdown flywheel when bumper released
                turretAccelPower = 0;
                primed = false;
            }

            // Shoot Logic
            // Triggered by pressing 'A' (toggle check), and only when flywheel is primed (up to speed)
            if (gamepad2.a && !prevgp2.a && primed) {
                // Run feeders (servos) to push ball into flywheel
                leftFeeder.setPower(0.5);
                rightFeeder.setPower(-0.5);

                // Block code execution for servoFeedTime milliseconds to ensure ball feeds
                timer.reset();
                prevPower = turretAccelPower;
                prevV = flywheel.getVelocity();
                while (timer.milliseconds() < servoFeedTime && opModeIsActive()) {idle();}

                // Stop feeders
                leftFeeder.setPower(0);
                rightFeeder.setPower(0);
            }

            //// --- Apply Power to Motors ---
            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            flywheel.setPower(turretAccelPower);

            //// --- Telemetry Reporting ---
            telemetry.addData("FL power", frontLeftPower);
            telemetry.addData("BL power", backLeftPower);
            telemetry.addData("FR power", frontRightPower);
            telemetry.addData("BR power", backRightPower);
            telemetry.addData("-------------------", "-------------------");
            telemetry.addData("Flywheel Power", turretAccelPower);
            telemetry.addData("Flywheel Velocity", flywheel.getVelocity());
            telemetry.addData("Turret Ready?", primed);
            telemetry.addData("prev power", prevPower);
            telemetry.addData("prev v", prevV);
            telemetry.update();
        }
    }
}
