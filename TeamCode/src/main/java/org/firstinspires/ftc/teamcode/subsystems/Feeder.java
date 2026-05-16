package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotMap;

/**
 * Feeder (kicker) subsystem.
 * A servo located below the shooter and carousel that hits/kicks balls
 * upward from the carousel slot into the spinning flywheel.
 *
 * The ball must be in the slot aligned above this feeder before kicking.
 * After kicking, the servo retracts to allow the next ball to be indexed.
 */
public class Feeder {

    private final Servo feederServo;
    private final Telemetry telemetry;

    private final ElapsedTime kickTimer = new ElapsedTime();
    private boolean isKicking = false;

    public Feeder(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        feederServo = hardwareMap.get(Servo.class, RobotMap.FEEDER_SERVO);
        retract();
    }

    /**
     * Kick a ball upward into the flywheel.
     * Call update() every loop to handle auto-retraction.
     */
    public void kick() {
        feederServo.setPosition(RobotMap.FEEDER_KICK);
        kickTimer.reset();
        isKicking = true;
    }

    /** Retract the feeder to the resting position. */
    public void retract() {
        feederServo.setPosition(RobotMap.FEEDER_RETRACTED);
        isKicking = false;
    }

    /**
     * Call this every loop iteration.
     * Auto-retracts the feeder after the kick duration has elapsed.
     */
    public void update() {
        if (isKicking && kickTimer.milliseconds() >= RobotMap.FEEDER_KICK_DURATION_MS) {
            retract();
        }
    }

    /** Returns true if the feeder is currently in the middle of a kick. */
    public boolean isKicking() {
        return isKicking;
    }

    /** Returns true if the feeder is retracted and ready for another kick. */
    public boolean isReady() {
        return !isKicking;
    }

    public void telemetry() {
        telemetry.addData("[Feeder] Kicking", isKicking);
        telemetry.addData("[Feeder] Ready", isReady());
    }
}
