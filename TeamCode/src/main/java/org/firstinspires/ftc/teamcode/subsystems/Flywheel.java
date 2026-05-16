package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.RobotMap;

/**
 * Flywheel subsystem for shooting artifacts (balls).
 * Uses a DcMotorEx to spin a flywheel at controlled velocities.
 */
public class Flywheel {

    private final DcMotorEx flywheelMotor;
    private final Telemetry telemetry;

    private boolean isRunning = false;

    public Flywheel(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        flywheelMotor = hardwareMap.get(DcMotorEx.class, RobotMap.FLYWHEEL_MOTOR);
        flywheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // TODO: Reverse if flywheel spins the wrong way
        // flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /** Spin up the flywheel to shooting speed. */
    public void spinUp() {
        flywheelMotor.setPower(RobotMap.FLYWHEEL_SHOOT_POWER);
        isRunning = true;
    }

    /** Spin the flywheel at a custom power. */
    public void setPower(double power) {
        flywheelMotor.setPower(power);
        isRunning = power != 0;
    }

    /** Stop the flywheel. */
    public void stop() {
        flywheelMotor.setPower(0);
        isRunning = false;
    }

    /** Toggle the flywheel on/off. */
    public void toggle() {
        if (isRunning) {
            stop();
        } else {
            spinUp();
        }
    }

    /** Check if the flywheel is at shooting velocity. */
    public boolean isAtSpeed() {
        return Math.abs(flywheelMotor.getVelocity()) >= RobotMap.FLYWHEEL_VELOCITY_THRESHOLD;
    }

    /** Get current flywheel velocity in ticks/sec. */
    public double getVelocity() {
        return flywheelMotor.getVelocity();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void telemetry() {
        telemetry.addData("[Flywheel] Running", isRunning);
        telemetry.addData("[Flywheel] Velocity", String.format("%.0f", getVelocity()));
        telemetry.addData("[Flywheel] At Speed", isAtSpeed());
        telemetry.addData("[Flywheel] Current (A)", String.format("%.2f", flywheelMotor.getCurrent(CurrentUnit.AMPS)));
    }
}
