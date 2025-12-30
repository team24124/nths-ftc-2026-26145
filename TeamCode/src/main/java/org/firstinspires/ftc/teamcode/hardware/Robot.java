package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.hardware.subsystems.Intake;
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

    public Robot(HardwareMap hw, Telemetry telemetry) {
        drivetrain = new RobotCentricDrive(hw, PoseStorage.currentPose);
        intake = new Intake(hw);
        flywheel = new Flywheel(hw);

        actions = ActionsScheduler.INSTANCE;
        telem = new TelemetryControl(telemetry);
        telem
                .subscribe(actions)
                .subscribe(drivetrain)
                .subscribe(intake)
                .subscribe(flywheel);
    }
}