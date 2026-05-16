package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotMap;
import org.firstinspires.ftc.teamcode.subsystems.Carousel;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Feeder;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Hood;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

/**
 * TeleOp OpMode for the DECODE FTC robot.
 * Single-gamepad control scheme — one driver operates everything.
 *
 * ═══════════════════════════════════════════════════════════════
 *                SINGLE GAMEPAD CONTROL SCHEME
 * ═══════════════════════════════════════════════════════════════
 *
 * ── DRIVING ─────────────────────────────────────────────────
 *   Left Stick X/Y     → Strafe / Forward-Backward (field-centric)
 *   Right Stick X      → Rotation
 *   Left Bumper (hold) → Slow mode (35% speed)
 *   Options (Start)    → Reset IMU heading
 *
 * ── INTAKE ──────────────────────────────────────────────────
 *   A (Cross)          → Toggle Intake On/Off
 *   B (Circle) (hold)  → Outtake (reverse rollers)
 *
 * ── SHOOTING ────────────────────────────────────────────────
 *   Right Bumper       → Toggle Flywheel On/Off
 *   Y (Triangle)       → Shoot (kick ball — flywheel must be on)
 *   Right Trigger      → Full auto-shoot (spin up + kick + advance)
 *
 * ── HOOD ────────────────────────────────────────────────────
 *   D-Pad Up           → Hood angle up (manual)
 *   D-Pad Down         → Hood angle down (manual)
 *   Left Trigger       → Hood preset: Low (hold > 0.5)
 *   X (Square)         → Hood preset: High
 *
 * ── CAROUSEL ────────────────────────────────────────────────
 *   D-Pad Right        → Carousel next slot
 *   D-Pad Left         → Carousel previous slot
 *
 * ═══════════════════════════════════════════════════════════════
 */
@TeleOp(name = "DECODE TeleOp", group = "Competition")
public class DecodeTeleOp extends LinearOpMode {

    // Subsystems
    private Drivetrain drivetrain;
    private Flywheel flywheel;
    private Intake intake;
    private Hood hood;
    private Carousel carousel;
    private Feeder feeder;

    // Timing
    private final ElapsedTime runtime = new ElapsedTime();

    // Shooting sequence state machine
    private enum ShootState {
        IDLE,
        SPINNING_UP,
        KICKING,
        ADVANCING_SLOT,
        COOLDOWN
    }

    private ShootState shootState = ShootState.IDLE;
    private final ElapsedTime shootTimer = new ElapsedTime();

    // Button edge-detection (previous frame state)
    private boolean prevA = false;
    private boolean prevY = false;
    private boolean prevX = false;
    private boolean prevRightBumper = false;
    private boolean prevDpadLeft = false;
    private boolean prevDpadRight = false;
    private boolean prevOptions = false;

    // Track how many balls have been shot (for telemetry)
    private int shotCount = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        // ── Initialize all subsystems ──
        drivetrain = new Drivetrain(hardwareMap, telemetry);
        flywheel = new Flywheel(hardwareMap, telemetry);
        intake = new Intake(hardwareMap, telemetry);
        hood = new Hood(hardwareMap, telemetry);
        carousel = new Carousel(hardwareMap, telemetry);
        feeder = new Feeder(hardwareMap, telemetry);

        telemetry.addLine("═══ DECODE Robot Initialized ═══");
        telemetry.addLine("All subsystems ready.");
        telemetry.addLine("Single gamepad control.");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // ── Main TeleOp Loop ──
        while (opModeIsActive()) {

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            //             DRIVING
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            // Slow mode while left bumper is held
            drivetrain.setSpeedMultiplier(gamepad1.left_bumper ? RobotMap.SLOW_MODE_MULTIPLIER : 1.0);

            // Field-centric driving
            drivetrain.driveFieldCentric(
                    gamepad1.left_stick_x,
                    gamepad1.left_stick_y,
                    gamepad1.right_stick_x
            );

            // Reset heading (press options/start)
            if (gamepad1.options && !prevOptions) {
                drivetrain.resetHeading();
            }
            prevOptions = gamepad1.options;

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            //             INTAKE
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            // A toggles intake on/off
            if (gamepad1.a && !prevA) {
                intake.toggle();
            }
            prevA = gamepad1.a;

            // B does outtake while held
            if (gamepad1.b) {
                intake.outtake();
            } else if (!gamepad1.a && intake.getState() == Intake.State.OUTTAKING) {
                // Stop outtake when B is released (don't interfere with A toggle)
                intake.stop();
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            //            SHOOTING
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            // Right bumper toggles flywheel on/off
            if (gamepad1.right_bumper && !prevRightBumper) {
                flywheel.toggle();
            }
            prevRightBumper = gamepad1.right_bumper;

            // Y button: single shot (kick the current ball, flywheel must be running)
            if (gamepad1.y && !prevY && shootState == ShootState.IDLE) {
                if (flywheel.isRunning()) {
                    startKick();
                }
            }
            prevY = gamepad1.y;

            // Right trigger: full auto-shoot sequence (spin up -> kick -> advance)
            if (gamepad1.right_trigger > 0.5 && shootState == ShootState.IDLE) {
                startShootSequence();
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            //              HOOD
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            // D-Pad Up/Down for manual adjustment
            if (gamepad1.dpad_up) {
                hood.incrementUp();
            }
            if (gamepad1.dpad_down) {
                hood.incrementDown();
            }

            // Left trigger > 0.5 → hood low preset
            if (gamepad1.left_trigger > 0.5) {
                hood.setLow();
            }

            // X → hood high preset
            if (gamepad1.x && !prevX) {
                hood.setHigh();
            }
            prevX = gamepad1.x;

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            //            CAROUSEL
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            // D-Pad Right → next slot
            if (gamepad1.dpad_right && !prevDpadRight) {
                carousel.nextSlot();
            }
            prevDpadRight = gamepad1.dpad_right;

            // D-Pad Left → previous slot
            if (gamepad1.dpad_left && !prevDpadLeft) {
                carousel.previousSlot();
            }
            prevDpadLeft = gamepad1.dpad_left;

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            //         UPDATE & TELEMETRY
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            // Update shoot state machine
            updateShootSequence();

            // Update subsystems
            feeder.update();

            // Telemetry
            telemetry.addLine("═══ DECODE TeleOp ═══");
            telemetry.addData("Runtime", String.format("%.1f s", runtime.seconds()));
            telemetry.addData("Shots Fired", shotCount);
            telemetry.addLine("─────────────────────");
            drivetrain.telemetry();
            flywheel.telemetry();
            intake.telemetry();
            hood.telemetry();
            carousel.telemetry();
            feeder.telemetry();

            if (shootState != ShootState.IDLE) {
                telemetry.addLine("─────────────────────");
                telemetry.addData("[Shoot] State", shootState);
            }

            telemetry.update();
        }

        // ── Cleanup ──
        drivetrain.stop();
        flywheel.stop();
        intake.stop();
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //              SHOOTING SEQUENCE STATE MACHINE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    /**
     * Start a full shoot sequence: spin up flywheel, wait for speed, kick, advance carousel.
     */
    private void startShootSequence() {
        flywheel.spinUp();
        shootState = ShootState.SPINNING_UP;
        shootTimer.reset();
    }

    /**
     * Start just the kick (flywheel already running).
     */
    private void startKick() {
        feeder.kick();
        shootState = ShootState.KICKING;
        shootTimer.reset();
    }

    /**
     * Update the shooting sequence state machine. Call every loop.
     */
    private void updateShootSequence() {
        switch (shootState) {
            case IDLE:
                // Nothing to do
                break;

            case SPINNING_UP:
                // Wait until flywheel is at speed
                if (flywheel.isAtSpeed()) {
                    startKick();
                }
                // Timeout after 2 seconds — something may be wrong
                if (shootTimer.seconds() > 2.0) {
                    shootState = ShootState.IDLE;
                }
                break;

            case KICKING:
                // Wait for the feeder to finish its kick
                if (feeder.isReady()) {
                    shotCount++;
                    shootState = ShootState.ADVANCING_SLOT;
                    shootTimer.reset();
                    carousel.nextSlot();
                }
                break;

            case ADVANCING_SLOT:
                // Wait for the carousel to settle in the next slot position
                if (shootTimer.milliseconds() >= RobotMap.SLOT_SETTLE_TIME_MS) {
                    shootState = ShootState.COOLDOWN;
                    shootTimer.reset();
                }
                break;

            case COOLDOWN:
                // Brief cooldown before allowing the next shot
                if (shootTimer.milliseconds() >= RobotMap.POST_KICK_COOLDOWN_MS) {
                    shootState = ShootState.IDLE;
                }
                break;
        }
    }
}
