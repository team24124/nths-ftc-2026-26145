package org.firstinspires.ftc.teamcode.opmode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.util.PIDF;

@Autonomous(name="tuner - DONT run ingame", group = "!", preselectTeleOp = "MTeleOp")
@Config
public class autoTuner extends LinearOpMode {
    public static long turretSpinUpTime = 2000;
    public static double turretPower = 1250;
    public static long firstBallsServoTime = 300;
    public static long movementTime = 2000;
    public static double leftPower = 0.5;
    public static double rightPower = 0.1;

    public static double kp = 0.005;
    public static double kv = 0.0005;

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

        turretAccel.setDirection(DcMotorSimple.Direction.REVERSE);

        VoltageSensor vs = hardwareMap.get(VoltageSensor.class, "Control Hub");

        int shots = 0;

        double flyhweelPower = 0;

        boolean primed = false;

        double[] speeds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        ElapsedTime timer = new ElapsedTime();
        PIDF pid = new PIDF();

        waitForStart();

        if (isStopRequested()) return;

        timer.reset();
        while (opModeIsActive()) {
            pid.setPV(kp, kv);

            if (shots == 16) {
                break;
            }

            flyhweelPower = pid.calculate(turretAccel.getVelocity(), turretPower, vs.getVoltage());
            primed = turretAccel.getVelocity() < (turretPower + 20) && turretAccel.getVelocity() > (turretPower - 20);

            if (primed && timer.milliseconds() > turretSpinUpTime) {
                leftFeeder.setPower(0.5);
                rightFeeder.setPower(-0.5);
                timer.reset();
                while (timer.milliseconds() < firstBallsServoTime && opModeIsActive()) {idle();}
                leftFeeder.setPower(0);
                rightFeeder.setPower(0);

                speeds[shots] = turretAccel.getVelocity();
                shots++;
                timer.reset();
            }

            turretAccel.setPower(flyhweelPower);
            telemetry.addData("flyp", flyhweelPower);
            telemetry.addData("flyv", turretAccel.getVelocity());
            telemetry.update();
        }

        timer.reset();
        while (timer.milliseconds() < turretSpinUpTime && opModeIsActive()) {idle();}
        frontLeftMotor.setPower(leftPower);
        backLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
        backRightMotor.setPower(rightPower);
        timer.reset();
        while (timer.milliseconds() < movementTime && opModeIsActive()) {idle();}
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);

        telemetry.addData("1", speeds[0]);
        telemetry.addData("2", speeds[1]);
        telemetry.addData("3", speeds[2]);
        telemetry.addData("4", speeds[3]);
        telemetry.update();

        timer.reset();
        while (timer.milliseconds() < 10000 && opModeIsActive()) {idle();}
    }
}
