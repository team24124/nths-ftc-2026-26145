package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;


import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.List;

@TeleOp(name = "trste", group = "!")
@Config
public class newTeleOp extends OpMode {
    private Robot robot;
    private Gamepad driver, operator;
    private List<LynxModule> hubs;

    public static double intakeSpeed = 0.5;
    public static double flywheelKp = 0.01;
    public static double flywheelKv = 0.0005;
    public static double flywheelV = 1000;

    @Override
    public void init() {
        hubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        driver = gamepad1;
        operator = gamepad2;

        robot = new Robot(hardwareMap, telemetry);
        robot.actions.init();
    }

    @Override
    public void loop() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }

        robot.flywheel.pid.setPV(flywheelKp, flywheelKv);
        robot.flywheel.v = flywheelV;

        double y = -driver.left_stick_y;
        double x = driver.left_stick_x * 1.1;
        double rx = driver.right_stick_x;

        robot.drivetrain.drive(x, y, rx);

        if (driver.leftStickButtonWasPressed()) {
            robot.drivetrain.toggleSpeeds();
        }

        if (driver.xWasPressed()) {
            robot.actions.schedule(robot.intake.toggleIntake(intakeSpeed));
        }

        robot.actions.schedule(robot.flywheel.run(driver.b));

        robot.telem.update();
        robot.actions.run();
    }

    @Override
    public void stop() {
        robot.telem.unsubscribeAll();
        robot.actions.stop();
    }
}
