package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.hardware.subsystems.GamepadControllers;
import org.firstinspires.ftc.teamcode.hardware.subsystems.Intake;
import org.firstinspires.ftc.teamcode.hardware.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.hardware.subsystems.RobotCentricDrive;
import org.firstinspires.ftc.teamcode.util.ActionsScheduler;
import org.firstinspires.ftc.teamcode.util.PoseStorage;
import org.firstinspires.ftc.teamcode.util.TelemetryControl;

public class Robot {
    public Drivetrain drivetrain;
    public Intake intake;
    public Flywheel flywheel;
    public TelemetryControl telem;
    public ActionsScheduler actions;
    public Kicker kicker;
    public GamepadControllers gamepad;

    public Robot(HardwareMap hw, Telemetry telemetry, Gamepad g1, Gamepad g2) {
        drivetrain = new RobotCentricDrive(hw, PoseStorage.currentPose);
        intake = new Intake(hw);
        flywheel = new Flywheel(hw);
        kicker = new Kicker(hw);
        gamepad = new GamepadControllers(g1, g2);

        actions = ActionsScheduler.INSTANCE;
        telem = new TelemetryControl(telemetry);
        telem
                .subscribe(gamepad)
                //.subscribe(actions)
                .subscribe(drivetrain)
                .subscribe(intake)
                .subscribe(flywheel)
                .subscribe(kicker);
    }
}