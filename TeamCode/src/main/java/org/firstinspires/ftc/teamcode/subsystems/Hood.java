package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotMap;

/**
 * Hood subsystem for adjusting the shooting angle.
 * Uses a servo to tilt the hood up/down for different shot trajectories.
 */
public class Hood {

    private final Servo hoodServo;
    private final Telemetry telemetry;

    private double targetPosition = RobotMap.HOOD_STOW;

    public Hood(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        hoodServo = hardwareMap.get(Servo.class, RobotMap.HOOD_SERVO);
        hoodServo.setPosition(targetPosition);
    }

    /** Set the hood to a specific position. */
    public void setPosition(double position) {
        targetPosition = clamp(position, 0.0, 1.0);
        hoodServo.setPosition(targetPosition);
    }

    /** Increment the hood position upward (steeper angle). */
    public void incrementUp() {
        setPosition(targetPosition + RobotMap.HOOD_STEP);
    }

    /** Increment the hood position downward (flatter angle). */
    public void incrementDown() {
        setPosition(targetPosition - RobotMap.HOOD_STEP);
    }

    /** Set hood to low shot preset. */
    public void setLow() {
        setPosition(RobotMap.HOOD_LOW);
    }

    /** Set hood to mid shot preset. */
    public void setMid() {
        setPosition(RobotMap.HOOD_MID);
    }

    /** Set hood to high shot preset. */
    public void setHigh() {
        setPosition(RobotMap.HOOD_HIGH);
    }

    /** Stow the hood (flat position). */
    public void stow() {
        setPosition(RobotMap.HOOD_STOW);
    }

    public double getPosition() {
        return targetPosition;
    }

    public void telemetry() {
        telemetry.addData("[Hood] Position", String.format("%.2f", targetPosition));
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
