package org.firstinspires.ftc.teamcode.opmode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.List;

@Config
@TeleOp(name="Tester", group = "tests")
public class Tester extends OpMode {
    private Robot robot;
    private List<LynxModule> hubs;

    public static double intakeSpeed = 0;
    public static double flywheelSpeed = 0;
    public static double lbSpeed = 0;
    public static double rbSpeed = 0;
    public static double lfSpeed = 0;
    public static double rfSpeed = 0;
    public static double servoPos = 0;

    public void init() {
        hubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        robot = new Robot(hardwareMap, telemetry, gamepad1, gamepad2);
    }

    public void loop() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }

        robot.flywheel.manualSetPowerOverride(flywheelSpeed);
        robot.intake.manualSetPowerOverride(intakeSpeed);
        robot.drivetrain.manualSetPowerOverride(lfSpeed, rfSpeed, lbSpeed, rbSpeed);
        robot.kicker.manualSetPositionOverride(servoPos);
    }

    public void stop() {
        robot.telem.unsubscribeAll();
        robot.flywheel.manualSetPowerOverride(0);
        robot.intake.manualSetPowerOverride(0);
        robot.drivetrain.manualSetPowerOverride(0, 0, 0, 0);
        robot.kicker.manualSetPositionOverride(0);
    }
}
