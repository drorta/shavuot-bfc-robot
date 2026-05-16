package org.firstinspires.ftc.teamcode;

/**
 * Central location for all hardware map device names and tunable constants.
 * Change the string names here to match your robot's hardware configuration
 * on the Driver Station — every subsystem reads from this file.
 *
 * ═══════════════════════════════════════════════════════════════
 *                   HARDWARE MAP NAMES
 * ═══════════════════════════════════════════════════════════════
 */
public final class RobotMap {

    private RobotMap() {} // Prevent instantiation

    // ── Drive Motors (DcMotorEx) ──────────────────────────────
    public static final String LEFT_FRONT_MOTOR  = "leftFront";
    public static final String LEFT_BACK_MOTOR   = "leftBack";
    public static final String RIGHT_FRONT_MOTOR = "rightFront";
    public static final String RIGHT_BACK_MOTOR  = "rightBack";

    // ── Flywheel (DcMotorEx) ─────────────────────────────────
    public static final String FLYWHEEL_MOTOR = "flywheel";

    // ── Intake Rollers (DcMotor) ─────────────────────────────
    public static final String INTAKE_MOTOR = "intake";

    // ── Hood Servo ───────────────────────────────────────────
    public static final String HOOD_SERVO = "hood";

    // ── Carousel / Magazine Servo ────────────────────────────
    public static final String CAROUSEL_SERVO = "carousel";

    // ── Feeder / Kicker Servo ────────────────────────────────
    public static final String FEEDER_SERVO = "feeder";

    // ── IMU ──────────────────────────────────────────────────
    public static final String IMU = "imu";


    // ═══════════════════════════════════════════════════════════
    //                  TUNABLE CONSTANTS
    // ═══════════════════════════════════════════════════════════

    // ── Flywheel ─────────────────────────────────────────────
    /** Power to apply when shooting (0.0 – 1.0) */
    public static double FLYWHEEL_SHOOT_POWER = 1.0;

    /** Encoder velocity (ticks/sec) threshold to consider flywheel "at speed" */
    public static double FLYWHEEL_VELOCITY_THRESHOLD = 1500;

    // ── Intake ───────────────────────────────────────────────
    /** Power for intake rollers (positive = inward) */
    public static double INTAKE_POWER = 1.0;

    /** Power for outtake / reverse (negative) */
    public static double OUTTAKE_POWER = -0.7;

    // ── Hood Servo Positions ─────────────────────────────────
    /** Stowed / resting position */
    public static double HOOD_STOW = 0.0;

    /** Low angle (close-range shot) */
    public static double HOOD_LOW = 0.2;

    /** Mid angle (medium-range shot) */
    public static double HOOD_MID = 0.5;

    /** High angle (far-range shot) */
    public static double HOOD_HIGH = 0.75;

    /** Step size for manual D-pad adjustment */
    public static double HOOD_STEP = 0.02;

    // ── Carousel Servo Positions ─────────────────────────────
    /** Servo position that aligns Slot 1 with the feeder */
    public static double CAROUSEL_SLOT_1 = 0.15;

    /** Servo position that aligns Slot 2 with the feeder */
    public static double CAROUSEL_SLOT_2 = 0.50;

    /** Servo position that aligns Slot 3 with the feeder */
    public static double CAROUSEL_SLOT_3 = 0.85;

    // ── Feeder / Kicker Servo Positions ──────────────────────
    /** Retracted / resting position (ready for next ball) */
    public static double FEEDER_RETRACTED = 0.0;

    /** Extended / kick position (hits ball into flywheel) */
    public static double FEEDER_KICK = 0.6;

    /** Duration of the kick action before auto-retract (ms) */
    public static double FEEDER_KICK_DURATION_MS = 300;

    // ── Shooting Sequence Timing ─────────────────────────────
    /** Time to wait for carousel to settle after advancing a slot (ms) */
    public static double SLOT_SETTLE_TIME_MS = 250;

    /** Cooldown after a shot before allowing another (ms) */
    public static double POST_KICK_COOLDOWN_MS = 400;

    // ── Drive ────────────────────────────────────────────────
    /** Slow-mode speed multiplier (hold left bumper) */
    public static double SLOW_MODE_MULTIPLIER = 0.35;
}
