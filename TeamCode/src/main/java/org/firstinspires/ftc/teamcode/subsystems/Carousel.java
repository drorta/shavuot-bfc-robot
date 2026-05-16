package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotMap;

/**
 * Carousel (rotating magazine) subsystem.
 * A rotating platform with 3 slots for holding balls (artifacts).
 * Controlled by a servo that indexes between slot positions, aligning
 * the correct slot with the feeder below the shooter.
 */
public class Carousel {

    private final Servo carouselServo;
    private final Telemetry telemetry;

    private int currentSlot = 1; // 1-indexed (1, 2, or 3)
    private final int TOTAL_SLOTS = 3;

    public Carousel(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        carouselServo = hardwareMap.get(Servo.class, RobotMap.CAROUSEL_SERVO);
        goToSlot(1);
    }

    /** Rotate to a specific slot (1, 2, or 3). */
    public void goToSlot(int slot) {
        if (slot < 1 || slot > TOTAL_SLOTS) return;

        currentSlot = slot;
        carouselServo.setPosition(getPositionForSlot(slot));
    }

    /** Advance to the next slot (wraps around 3 -> 1). */
    public void nextSlot() {
        int next = (currentSlot % TOTAL_SLOTS) + 1;
        goToSlot(next);
    }

    /** Go to the previous slot (wraps around 1 -> 3). */
    public void previousSlot() {
        int prev = currentSlot - 1;
        if (prev < 1) prev = TOTAL_SLOTS;
        goToSlot(prev);
    }

    /** Get the servo position for a given slot number. */
    private double getPositionForSlot(int slot) {
        switch (slot) {
            case 1: return RobotMap.CAROUSEL_SLOT_1;
            case 2: return RobotMap.CAROUSEL_SLOT_2;
            case 3: return RobotMap.CAROUSEL_SLOT_3;
            default: return RobotMap.CAROUSEL_SLOT_1;
        }
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void telemetry() {
        telemetry.addData("[Carousel] Current Slot", currentSlot);
    }
}
