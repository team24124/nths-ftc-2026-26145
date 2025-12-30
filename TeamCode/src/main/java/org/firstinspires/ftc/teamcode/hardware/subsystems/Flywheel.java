package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.PIDF;
import org.firstinspires.ftc.teamcode.util.interfaces.SubsystemBase;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

public class Flywheel implements SubsystemBase, TelemetryObservable {
    private final DcMotorEx flywheelR, flywheelL;
    public PIDF pid = new PIDF();
    private final VoltageSensor vs;
    public boolean primed = false;
    public double v = 1000;
    public Flywheel(HardwareMap hw) {
        this.flywheelR = hw.get(DcMotorEx.class, "flywheelR");
        this.flywheelL = hw.get(DcMotorEx.class, "flywheelL");
        this.flywheelR.setDirection(DcMotorSimple.Direction.REVERSE);
        this.vs = hw.get(VoltageSensor.class, "Control Hub");
    }

    public Action run(boolean ss) {
        return (TelemetryPacket telemetry) -> {
            if (ss) {
                double power = pid.calculate(this.flywheelL.getVelocity(), v, this.vs.getVoltage());

                this.flywheelR.setPower(power);
                this.flywheelL.setPower(power);
            } else {
                this.flywheelR.setPower(0);
                this.flywheelL.setPower(0);
            }

            primed = flywheelL.getVelocity() < v + 20 && flywheelL.getVelocity() > v - 20;

            return false;
        };
    }

    public void manualSetPowerOverride(double p) {
        this.flywheelR.setPower(p);
        this.flywheelL.setPower(p);
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("Flywheel V", this.flywheelL.getVelocity());
        telemetry.addData("Primed", primed);
    }

    @Override
    public String getName() {
        return "Flywheel";
    }
}
