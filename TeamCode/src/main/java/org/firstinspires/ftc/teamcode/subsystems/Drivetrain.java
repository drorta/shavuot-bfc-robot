package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.MecanumDrive;

/**
 * Drivetrain subsystem that wraps the Road Runner MecanumDrive.
 * Provides field-centric and robot-centric mecanum driving.
 */
public class Drivetrain {

    private final MecanumDrive drive;
    private final Telemetry telemetry;

    // Speed multiplier for slow-mode
    private double speedMultiplier = 1.0;

    public Drivetrain(HardwareMap hardwareMap, Telemetry telemetry) {
        this.drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        this.telemetry = telemetry;
    }

    /**
     * Drive the robot using field-centric controls.
     *
     * @param gamepadLeftStickX  strafe input (-1 to 1)
     * @param gamepadLeftStickY  forward input (-1 to 1, negative is forward on gamepad)
     * @param gamepadRightStickX rotation input (-1 to 1)
     */
    public void driveFieldCentric(double gamepadLeftStickX, double gamepadLeftStickY, double gamepadRightStickX) {
        drive.updatePoseEstimate();

        // Get heading from localizer
        double heading = drive.localizer.getPose().heading.toDouble();

        // Rotate the gamepad input by the inverse of the robot heading for field-centric control
        double rotX = gamepadLeftStickX * Math.cos(-heading) - (-gamepadLeftStickY) * Math.sin(-heading);
        double rotY = gamepadLeftStickX * Math.sin(-heading) + (-gamepadLeftStickY) * Math.cos(-heading);

        drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(rotY * speedMultiplier, -rotX * speedMultiplier),
                -gamepadRightStickX * speedMultiplier
        ));
    }

    /**
     * Drive the robot using robot-centric controls.
     *
     * @param gamepadLeftStickX  strafe input (-1 to 1)
     * @param gamepadLeftStickY  forward input (-1 to 1, negative is forward on gamepad)
     * @param gamepadRightStickX rotation input (-1 to 1)
     */
    public void driveRobotCentric(double gamepadLeftStickX, double gamepadLeftStickY, double gamepadRightStickX) {
        drive.updatePoseEstimate();

        drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(-gamepadLeftStickY * speedMultiplier, -gamepadLeftStickX * speedMultiplier),
                -gamepadRightStickX * speedMultiplier
        ));
    }

    /**
     * Reset the IMU heading (re-zero the field-centric reference).
     */
    public void resetHeading() {
        drive.localizer.setPose(new Pose2d(
                drive.localizer.getPose().position,
                0
        ));
    }

    /**
     * Set the speed multiplier (e.g., 0.4 for slow mode, 1.0 for full speed).
     */
    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = multiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Stop all drive motors.
     */
    public void stop() {
        drive.setDrivePowers(new PoseVelocity2d(new Vector2d(0, 0), 0));
    }

    public MecanumDrive getDrive() {
        return drive;
    }

    public void telemetry() {
        Pose2d pose = drive.localizer.getPose();
        telemetry.addData("[Drive] X", String.format("%.2f", pose.position.x));
        telemetry.addData("[Drive] Y", String.format("%.2f", pose.position.y));
        telemetry.addData("[Drive] Heading (deg)", String.format("%.2f", Math.toDegrees(pose.heading.toDouble())));
        telemetry.addData("[Drive] Speed", String.format("%.0f%%", speedMultiplier * 100));
    }
}
