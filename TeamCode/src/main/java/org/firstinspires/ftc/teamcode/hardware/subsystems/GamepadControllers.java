package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.interfaces.SubsystemBase;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

public class GamepadControllers implements TelemetryObservable, SubsystemBase {
    public Gamepad driver, operator;

    public GamepadControllers(Gamepad uno, Gamepad dos) {
        driver = uno;
        operator = dos;
    }

    public void swapDriver(Gamepad g) {
        driver = g;
    }

    public void swapOperator(Gamepad g) {
        operator = g;
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("1 controller mode", driver == operator);
    }

    @Override
    public String getName() {
        return "";
    }
}
