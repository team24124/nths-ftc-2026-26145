package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;


import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.List;

@TeleOp(name = "teleop", group = "!")
@Config
public class  newTeleOp extends OpMode {
    private Robot robot;
    private List<LynxModule> hubs;

    // ftc dashboard config values
    public static double intakeSpeed = 1;
    public static double flywheelKp = 0.01;
    public static double flywheelKv = 0.0005;
    public static Double[] flywheelV = {1500.0, 2000.0, 1500.0};
    public static double kickerStartPos = 0.25;
    public static double kickerEndPos = 0;

    @Override
    public void init() {
        hubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        robot = new Robot(hardwareMap, telemetry, gamepad1, gamepad2);
        robot.kicker.manualSetPositionOverride(kickerStartPos);
        robot.actions.init();
    }

    @Override
    public void loop() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }

        // update values each loop for ftc dashboard config ability
        robot.flywheel.setPV(flywheelKp, flywheelKv);
        robot.flywheel.setSpeeds(flywheelV);
        robot.intake.setSpeed(intakeSpeed);
        robot.kicker.updatePosTargets(kickerStartPos, kickerEndPos);

        // toggle 1 controller mode
//        if (robot.gamepad.driver.startWasPressed()) {
//            if (robot.gamepad.operator == gamepad1) {robot.gamepad.swapOperator(gamepad2);} else {robot.gamepad.swapOperator(gamepad1);}
//        }

        // drivetrain
        robot.drivetrain.drive(
                robot.gamepad.driver.left_stick_x * 1.1,
                -robot.gamepad.driver.left_stick_y,
                robot.gamepad.driver.right_stick_x
        );

        if (robot.gamepad.driver.leftStickButtonWasPressed()) {
            robot.drivetrain.toggleSpeeds();
        }

        if (robot.gamepad.operator.rightStickButtonWasPressed()) {
            robot.flywheel.toggleSpeeds();
        }

        // intake
        robot.intake.setReverse(robot.gamepad.operator.left_bumper);
        robot.intake.run(robot.gamepad.operator.left_trigger > 0.5);

        // flywheel
        robot.flywheel.setReverse(robot.gamepad.operator.right_bumper);
        robot.flywheel.run(robot.gamepad.operator.right_trigger > 0.5);

        // kicker
        if (robot.gamepad.operator.aWasPressed()) {
            robot.actions.schedule(robot.kicker.kick());
        }

        // periodical calls
        robot.telem.update();
        robot.actions.run();
    }

    @Override
    public void stop() {
        robot.telem.unsubscribeAll();
        robot.actions.stop();
    }
}
