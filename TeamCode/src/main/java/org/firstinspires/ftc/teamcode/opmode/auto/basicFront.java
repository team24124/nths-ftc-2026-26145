package org.firstinspires.ftc.teamcode.opmode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="basicFront", group = "!", preselectTeleOp = "MTeleOp")
@Config
public class basicFront extends LinearOpMode {
    public static long turretSpinUpTime = 1000;
    public static double turretPower = -1250;
    public static long firstBallsServoTime = 300;
    public static long lastBallServoTime = 600;
    public static long reversingTime = 1000;
    public static double leftPower = -0.5;

    public static double rightPower = -0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx frontLeftMotor = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx backLeftMotor = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx frontRightMotor = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx backRightMotor = hardwareMap.get(DcMotorEx.class, "rightBack");

        DcMotorEx turretAccel = hardwareMap.get(DcMotorEx.class, "turretAccelerator");
        turretAccel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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

        telemetry.addData("Instructions:", "Stop auto when robot has exited launch line");

        ElapsedTime timer = new ElapsedTime();

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
