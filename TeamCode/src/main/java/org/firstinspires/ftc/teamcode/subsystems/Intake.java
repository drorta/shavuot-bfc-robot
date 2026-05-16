package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotMap;

/**
 * Intake subsystem for collecting artifacts (balls).
 * Controls the intake rollers only — the intake structure is fixed.
 */
public class Intake {

    private final DcMotor intakeMotor;
    private final Telemetry telemetry;

    public enum State {
        INTAKING,
        OUTTAKING,
        STOPPED
    }

    private State currentState = State.STOPPED;

    public Intake(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        intakeMotor = hardwareMap.get(DcMotor.class, RobotMap.INTAKE_MOTOR);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: Reverse if rollers spin the wrong way
        // intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /** Run the intake rollers to collect balls. */
    public void intake() {
        intakeMotor.setPower(RobotMap.INTAKE_POWER);
        currentState = State.INTAKING;
    }

    /** Reverse the rollers to eject balls. */
    public void outtake() {
        intakeMotor.setPower(RobotMap.OUTTAKE_POWER);
        currentState = State.OUTTAKING;
    }

    /** Stop the intake rollers. */
    public void stop() {
        intakeMotor.setPower(0);
        currentState = State.STOPPED;
    }

    /** Toggle intake on/off. If running, stops. If stopped, intakes. */
    public void toggle() {
        if (currentState == State.INTAKING) {
            stop();
        } else {
            intake();
        }
    }

    public State getState() {
        return currentState;
    }

    public void telemetry() {
        telemetry.addData("[Intake] State", currentState);
    }
}
