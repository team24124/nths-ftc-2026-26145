package org.firstinspires.ftc.teamcode.hardware.subsystems;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.util.ArraySelect;
import org.firstinspires.ftc.teamcode.util.interfaces.TelemetryObservable;

public class RobotCentricDrive extends Drivetrain implements TelemetryObservable {
    public RobotCentricDrive(HardwareMap hw, Pose2d start) {
        super(hw, start);
    }

    /**
     * Drive Train implementation from this <a href="https://www.youtube.com/watch?v=gnSW2QpkGXQ">video</a>
     * @param x Amount of x (Ex. left/right on the left joystick)
     * @param y Amount of y (Ex. up/down on the left joystick)
     * @param rx Amount of turn (Ex. left/right on the right joystick)
     */
    @Override
    public void drive(double x, double y, double rx) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double leftPower = ((y + x + rx) / denominator);
        double leftBackPower = ((y - x + rx) / denominator);
        double rightPower = ((y - x - rx) / denominator);
        double rightBackPower = ((y + x - rx) / denominator);

        ArraySelect<Double> speeds = getSpeeds();
        super.setDrivePowers(
                leftPower * speeds.getSelected(),
                rightPower * speeds.getSelected(),
                leftBackPower * speeds.getSelected(),
                rightBackPower * speeds.getSelected()
        );
    }

    @Override
    public void manualSetPowerOverride(double left, double right, double leftBack, double rightBack) {
        super.setDrivePowers(left, right, leftBack, rightBack);
    }

    @Override
    public void periodic(){
        getDrive().updatePoseEstimate();
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("Speed", getSpeeds().getSelected());
    }
}