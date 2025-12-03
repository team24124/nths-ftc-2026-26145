package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.util.PIDF;

public class Robot {
    private HardwareMap hw;
    public Robot(HardwareMap h) {
        hw = h;
    }

    // drivebase
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;

    private double frontLeftPower = 0;
    private double frontRightPower = 0;
    private double backLeftPower = 0;
    private double backRightPower = 0;


    // turret
    private DcMotorEx flywheel;
    private CRServo lFeeder;
    private CRServo rFeeder;

    private double flywheelPower = 0;
    private boolean flywheelPrimed = false;

    private PIDF flywheelPID = new PIDF();
    private VoltageSensor voltageSensor;


    private ElapsedTime timer = new ElapsedTime();

    public void init() {
        frontLeftMotor = hw.get(DcMotor.class, "leftFront");
        frontRightMotor = hw.get(DcMotor.class, "rightFront");
        backLeftMotor = hw.get(DcMotor.class, "leftBack");
        backRightMotor = hw.get(DcMotor.class, "rightBack");

        flywheel = hw.get(DcMotorEx.class, "turretAccelerator");
        lFeeder = hw.get(CRServo.class, "leftFeeder");
        rFeeder = hw.get(CRServo.class, "rightFeeder");

        voltageSensor = hw.get(VoltageSensor.class, "Control Hub");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        flywheel.setZeroPowerBehavior(BRAKE);
    }

    public void updatePV(double kp, double kv) {
        flywheelPID.setPV(kp, kv);
    }

    public double[] drive(double x, double y, double r) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(r), 1);
        frontLeftPower = ((y + x + r) / denominator);
        backLeftPower = ((y - x + r) / denominator);
        frontRightPower = ((y - x - r) / denominator);
        backRightPower = ((y + x - r) / denominator);

        return new double[]{frontLeftPower, frontRightPower, backLeftPower, backRightPower};
    }
}
