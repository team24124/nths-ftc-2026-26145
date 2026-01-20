package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.interfaces.SubsystemBase;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

public class Intake implements SubsystemBase, TelemetryObservable {
    private final DcMotorEx motor;
    private boolean running = false;
    private boolean reverse = false;
    private double speed = 1;
    public Intake(HardwareMap hw) {
        this.motor = hw.get(DcMotorEx.class, "intake");
        this.motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        this.motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void run(boolean run) {
        this.running = run;
        if (run) {
            if (this.reverse) {this.motor.setPower(this.speed*-1);}
            else {this.motor.setPower(this.speed);}
        } else {
            this.motor.setPower(0);
        }
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void manualSetPowerOverride(double p) {
        this.motor.setPower(p);
    }

    @Override
    public String getName() {
        return "Intake";
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("Reverse?", this.reverse);
        telemetry.addData("Intake Running", this.running);
    }
}
