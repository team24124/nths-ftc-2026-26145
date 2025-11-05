package org.firstinspires.ftc.teamcode.opmode.teleop;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

// PLEASE PLEASE READ THE README FILE BEFORE TOUCHING THE CODE

@TeleOp(name = "MainTeleOp", group = "!")
public class MainTeleOp2 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

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

        // turret
        DcMotor turretAccel = hardwareMap.dcMotor.get("turretAccelerator");

        double turretAccelPower = 0;

        // make all motors work in the same direction; counteract reverse motors
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        turretAccel.setZeroPowerBehavior(BRAKE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // mecanum drivebase logic

            double y = -gamepad1.left_stick_y; // left stick y axis
            double x = gamepad1.left_stick_x * 1.1; // left stick x axis, multi to counteract imperfect strafing
            double rx = gamepad1.right_stick_x; // right stick x axis

            // CHECK GM 0 FOR MATH, search mecanum teleop
            // IMPORTANT: REMOVE THE /2 BASED ON DRIVER PREFERENCE.
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            frontLeftPower = ((y + x + rx) / denominator) /2;
            backLeftPower = ((y - x + rx) / denominator) /2;
            frontRightPower = ((y - x - rx) / denominator) /2;
            backRightPower = ((y + x - rx) / denominator) /2;


            // turret logic

            turretAccelPower = gamepad2.left_stick_x;  // literally just motor power

            // eventually add the gecko wheel logic here


            frontLeftMotor.setPower(frontLeftPower);  // kinda straightforward here. just set motors to the previously calculated power, and send that to telemetry as well
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            turretAccel.setPower(turretAccelPower);

            telemetry.addData("FL power", frontLeftPower);
            telemetry.addData("BL power", backLeftPower);
            telemetry.addData("FR power", frontRightPower);
            telemetry.addData("BR power", backRightPower);
            telemetry.addData("Turret Power", turretAccelPower);
        }
    }
}