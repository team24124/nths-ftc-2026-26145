package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.TelemetryControl;
import org.firstinspires.ftc.teamcode.util.interfaces.SubsystemBase;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

public class Kicker implements SubsystemBase, TelemetryObservable {
    private double startpos = 0.25;
    private double endpos = 0;
    private final Servo kicker;
    private boolean up = false;

    public Kicker(HardwareMap hw) {
        this.kicker = hw.get(Servo.class, "kicker");
    }

    public Action kick() {
        ElapsedTime timer = new ElapsedTime();
        return (TelemetryPacket telemetry) -> {
            this.kicker.setPosition(this.endpos);
            this.up = true;

            if (timer.seconds() < 0.5) {return true;}

            this.kicker.setPosition(this.startpos);

            this.up = false;
            return false;
        };
    }

    public void updatePosTargets(double start, double end) {
        this.startpos = start;
        this.endpos = end;
    }

    public void manualSetPositionOverride(double pos) {
        this.kicker.setPosition(pos);
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("Kicker up?", !this.up);
    }

    @Override
    public String getName() {return "Kicker";}
}
