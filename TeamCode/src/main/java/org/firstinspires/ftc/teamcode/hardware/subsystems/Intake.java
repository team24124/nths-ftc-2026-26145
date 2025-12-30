package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.interfaces.SubsystemBase;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

public class Intake implements SubsystemBase, TelemetryObservable {
    private double direction = 1;
    private final DcMotorEx motor;
    private boolean run = false;
    public Intake(HardwareMap hw) {
        this.motor = hw.get(DcMotorEx.class, "intake");
        this.motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        this.motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public Action toggleIntake(double intakeSpeed) {
        return (TelemetryPacket telemetry) -> {
            run = !run;
            if (run) {
                this.motor.setPower(direction*intakeSpeed);
            } else {
                this.motor.setPower(0);
            }

            return false;
        };
    }

    public Action reverseIntake() {
        return (TelemetryPacket telemetry) -> {
            direction *= -1;
            return false;
        };
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
        telemetry.addData("Intake Running", run);
    }
}
