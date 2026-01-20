package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.ArraySelect;
import org.firstinspires.ftc.teamcode.util.PIDF;
import org.firstinspires.ftc.teamcode.util.interfaces.SubsystemBase;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

import java.util.Objects;

public class Flywheel implements SubsystemBase, TelemetryObservable {
    private final DcMotorEx flywheelR, flywheelL;
    private final PIDF pid = new PIDF();
    private final VoltageSensor vs;
    private boolean primed = false;
    private ArraySelect<Double> speeds = new ArraySelect<>(new Double[]{1500.0, 2000.0, 1500.0});
    private String targetLocation = "wall";
    private boolean reverse = false;
    public Flywheel(HardwareMap hw) {
        this.flywheelR = hw.get(DcMotorEx.class, "flywheelR");
        this.flywheelL = hw.get(DcMotorEx.class, "flywheelL");
        this.flywheelR.setDirection(DcMotorSimple.Direction.REVERSE);
        this.flywheelL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.flywheelR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.vs = hw.get(VoltageSensor.class, "Control Hub");
    }

    public void run(boolean ss) {
        if (ss) {
            double power = pid.calculate(this.flywheelL.getVelocity(), this.speeds.getSelected(), this.vs.getVoltage());

            if (reverse) {power *= -1;}

            this.flywheelR.setPower(power);
            this.flywheelL.setPower(power);
        } else {
            this.flywheelR.setPower(0);
            this.flywheelL.setPower(0);
        }

        this.primed = this.flywheelL.getVelocity() < this.speeds.getSelected() + 20 && this.flywheelL.getVelocity() > this.speeds.getSelected() - 20;
    }

    public void setSpeeds(Double[] v) {
        ArraySelect<Double> newSpeeds = new ArraySelect<>(v);
        if (!Objects.equals(this.speeds, newSpeeds)) {this.speeds = newSpeeds; this.targetLocation = "wall";}
    }

    public void toggleSpeeds() {
        this.speeds.setSelected(Math.abs(this.speeds.getSelectedIndex() - 1));
        if (this.speeds.getSelectedIndex() == 0) {this.targetLocation = "wall";}
        else if (this.speeds.getSelectedIndex() == 1) {this.targetLocation = "far";}
        else if (this.speeds.getSelectedIndex() == 2) {this.targetLocation = "middle";}
    }

    public void setPV(double p, double v) {
        this.pid.setPV(p,v);
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void manualSetPowerOverride(double p) {
        this.flywheelR.setPower(p);
        this.flywheelL.setPower(p);
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("Reverse?", this.reverse);
        telemetry.addData("Flywheel Velocity", this.flywheelL.getVelocity());
        telemetry.addData("Target Velocity", this.speeds.getSelected());
        telemetry.addData("Shooting Location", this.targetLocation);
        telemetry.addData("Ready?", this.primed);
    }

    @Override
    public String getName() {
        return "Flywheel";
    }
}
