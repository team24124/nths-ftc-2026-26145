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

// PLEASE PLEASE READ THE README FILE BEFORE TOUCHING THE CODE

//personal note: upload code

@TeleOp(name = "MTeleOp", group = "!")
@Config
public class MTeleOp extends LinearOpMode {
    public static double maxTurretPower = 1250;
    public static long servoFeedTime = 300;
    public static long waitBack = 100;
    public static double kp = 0;
    public static double kv = 0.00042;
    @Override
    public void runOpMode() throws InterruptedException {

        Gamepad prevgp1 = new Gamepad();
        Gamepad curgp1 = new Gamepad();

        Gamepad prevgp2 = new Gamepad();
        Gamepad curgp2 = new Gamepad();

        // Declare our motors and empty variables for they're power

        // drivebase
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("leftBack");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rightBack");

        double frontLeftPower = 0;
        double frontRightPower = 0;
        double backLeftPower = 0;
        double backRightPower = 0;

        boolean precition = false;

        // turret
        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class,"turretAccelerator");
        //flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);

        CRServo leftFeeder = hardwareMap.get(CRServo.class, "leftFeeder");
        CRServo rightFeeder = hardwareMap.get(CRServo.class, "rightFeeder");

//        double ballCount = 1;
//        boolean ballShooting = false;

        double turretAccelPower = 0;
        PIDF pv = new PIDF();

        boolean primed = false;
        boolean rb = false;

        VoltageSensor voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");

        // make all motors work in the same direction; counteract reverse motors
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        flywheel.setZeroPowerBehavior(BRAKE);

        ElapsedTime timer = new ElapsedTime();
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            pv.setPV(kp, kv);

            prevgp1.copy(curgp1);
            curgp1.copy(gamepad1);
            prevgp2.copy(curgp2);
            curgp2.copy(gamepad2);
            // toggle precition movement mode
            if (gamepad1.y && !prevgp1.y) {
                precition = !precition;
            }

            // mecanum drivebase logic

            if (precition) {
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
                } else if (gamepad1.left_bumper) {
                    frontLeftPower = -0.1;
                    frontRightPower = 0.1;
                    backLeftPower = -0.1;
                    backRightPower = 0.1;
                } else if (gamepad1.right_bumper) {
                    frontLeftPower = 0.1;
                    frontRightPower = -0.1;
                    backLeftPower = 0.1;
                    backRightPower = -0.1;
                } else {
                    frontLeftPower = 0;
                    frontRightPower = 0;
                    backLeftPower = 0;
                    backRightPower = 0;
                }
            } else {
                double y = -gamepad1.left_stick_y; // left stick y axis
                double x = gamepad1.left_stick_x * 1.1; // left stick x axis, multi to counteract imperfect strafing
                double rx = gamepad1.right_stick_x; // right stick x axis

                // CHECK GM 0 FOR MATH, search mecanum teleop
                // IMPORTANT: REMOVE THE /2 BASED ON DRIVER PREFERENCE.
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
                frontLeftPower = ((y + x + rx) / denominator);
                backLeftPower = ((y - x + rx) / denominator);
                frontRightPower = ((y - x - rx) / denominator);
                backRightPower = ((y + x - rx) / denominator);
            }

            // turret logic

            if (gamepad2.right_bumper) {
                rb = true;
                turretAccelPower = pv.calculate(flywheel.getVelocity(), maxTurretPower, voltageSensor.getVoltage());
                if (flywheel.getVelocity() < (maxTurretPower + 20) && flywheel.getVelocity() > (maxTurretPower - 20)) {
                    primed = true;
                }
            } else {
                rb = false;
                turretAccelPower = 0;
                primed = false;
            }


            if ((gamepad2.a && !prevgp2.a) || gamepad2.x) {
                if (primed) {
                    leftFeeder.setPower(0.5);
                    rightFeeder.setPower(-0.5);
                    timer.reset();
                    while (timer.milliseconds() < servoFeedTime && opModeIsActive()) {idle();}
                    leftFeeder.setPower(0);
                    rightFeeder.setPower(0);
                }
            } else if (gamepad2.left_bumper) {
                leftFeeder.setPower(-0.5);
                rightFeeder.setPower(0.5);
                flywheel.setPower(0.5);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                leftFeeder.setPower(0);
                rightFeeder.setPower(0);
                flywheel.setPower(0);
            } //else {
//                leftFeeder.setPower(-0.1);
//                rightFeeder.setPower(0.1);
//            }



            frontLeftMotor.setPower(frontLeftPower);  // kinda straightforward here. just set motors to the previously calculated power, and send that to telemetry as well
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            flywheel.setPower(turretAccelPower);

            telemetry.addData("FL power", frontLeftPower);
            telemetry.addData("BL power", backLeftPower);
            telemetry.addData("FR power", frontRightPower);
            telemetry.addData("BR power", backRightPower);
            telemetry.addData("Turret Power", turretAccelPower);
            telemetry.addData("turret vel", flywheel.getVelocity());
            telemetry.addData("Turret Ready", primed);
            telemetry.addData("rb", rb);
            telemetry.update();
        }
    }
}