package org.firstinspires.ftc.teamcode.opmode.auto.basic;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Auton mode for the robot.
 * Annotated with @Autonomous to appear in the driver station OpMode list.
 * Annotated with @Config to allow variable tuning via FTC Dashboard.
 */
@Autonomous(name="Front", group = "basic", preselectTeleOp = "MTeleOp")
@Config
public class front extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        //// -- Declare motors --
        DcMotorEx frontLeftMotor = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx backLeftMotor = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx frontRightMotor = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx backRightMotor = hardwareMap.get(DcMotorEx.class, "rightBack");

        // set direction to ensure positive value usage
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // set motor zero power behavior to BRAKE (motors stop quickly when power is 0)
        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);

        waitForStart();

        // display instructions for driver
        telemetry.addData("Instructions:", "Stop auto when robot has exited launch line");
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();  // timer for scheduled events

        // run movement
        frontLeftMotor.setPower(0.5);
        backLeftMotor.setPower(0.5);
        frontRightMotor.setPower(0.5);
        backRightMotor.setPower(0.5);

        // wait for movement to complete (ideally driver would manually stop it)
        timer.reset();
        while (timer.milliseconds() < 10000 && opModeIsActive()) {idle();}

        // stop movement
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);
    }
}
