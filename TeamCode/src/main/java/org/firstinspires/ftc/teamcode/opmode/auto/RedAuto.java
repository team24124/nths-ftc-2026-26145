package org.firstinspires.ftc.teamcode.opmode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="RedAuto", group = "!", preselectTeleOp = "MainTeleOp")
@Config
public class RedAuto extends LinearOpMode {
    public static long turretSpinUpTime = 1000;
    public static double turretPower = 0.63;
    public static long firstBallsServoTime = 300;
    public static long lastBallServoTime = 600;
    public static long reversingTime = 2000;
    public static double leftPower = 0.1;
    public static double rightPower = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx frontLeftMotor = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx backLeftMotor = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx frontRightMotor = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx backRightMotor = hardwareMap.get(DcMotorEx.class, "rightBack");

        DcMotorEx turretAccel = hardwareMap.get(DcMotorEx.class, "turretAccelerator");

        CRServo leftFeeder = hardwareMap.get(CRServo.class, "leftFeeder");
        CRServo rightFeeder = hardwareMap.get(CRServo.class, "rightFeeder");


        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        turretAccel.setZeroPowerBehavior(BRAKE);

        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime timer = new ElapsedTime();

        turretAccel.setPower(-turretPower);
        timer.reset();
        while (timer.milliseconds() < turretSpinUpTime) {idle();}

        leftFeeder.setPower(0.5);
        rightFeeder.setPower(-0.5);
        timer.reset();

        while (timer.milliseconds() < firstBallsServoTime) {idle();}
        leftFeeder.setPower(0);
        rightFeeder.setPower(0);
        telemetry.addData("twas the first block", "yes");
        telemetry.update();
        timer.reset();
        while (timer.milliseconds() < turretSpinUpTime) {idle();}



        leftFeeder.setPower(0.5);
        rightFeeder.setPower(-0.5);
        timer.reset();
        while (timer.milliseconds() < firstBallsServoTime) {idle();}
        leftFeeder.setPower(0);
        rightFeeder.setPower(0);
        telemetry.addData("twas the second block", "yes");
        telemetry.update();
        timer.reset();
        while (timer.milliseconds() < turretSpinUpTime) {idle();}

        leftFeeder.setPower(0.5);
        rightFeeder.setPower(-0.5);
        timer.reset();
        while (timer.milliseconds() < firstBallsServoTime) {idle();}
        leftFeeder.setPower(0);
        rightFeeder.setPower(0);
        telemetry.addData("twas the second block", "yes");
        telemetry.update();
        timer.reset();
        while (timer.milliseconds() < turretSpinUpTime) {idle();}

        leftFeeder.setPower(0.5);
        rightFeeder.setPower(-0.5);
        timer.reset();
        while (timer.milliseconds() < lastBallServoTime) {idle();}
        leftFeeder.setPower(0);
        rightFeeder.setPower(0);
        timer.reset();
        while (timer.milliseconds() < 500) {idle();}
        turretAccel.setPower(0);

        telemetry.addData("twas the third block", "yes");
        telemetry.update();

        frontLeftMotor.setPower(leftPower);
        backLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
        backRightMotor.setPower(rightPower);
        timer.reset();
        while (timer.milliseconds() < reversingTime) {idle();}
        leftFeeder.setPower(0);
        rightFeeder.setPower(0);
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);
    }
}
