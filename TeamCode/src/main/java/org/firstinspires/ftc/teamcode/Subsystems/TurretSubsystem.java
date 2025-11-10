package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.util.MathUtils;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.LIMELIGHT_POLL_HZ;

/**
 * Simple and robust turret PD(F) control around a 0-degree setpoint using limelight tx.
 * - Low-pass filters limelight offset to reduce noise.
 * - Uses PID with real dt for consistent derivative behavior.
 * - Adds a small static feedforward (kS) to overcome stiction.
 * - Applies per-loop slew rate limiting to prevent jerks.
 * - Enforces soft mechanical limits using a potentiometer.
 */
public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private final AnalogInput pot;
    private final LimelightSubsystem limelightSubsystem;

    // Gains (start values come from constants; can be adjusted at runtime via setters if needed)
    private double kP = TURRET_KP;
    private double kD = TURRET_KD;

    private final PIDController pid = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    private double filteredOffsetDeg = 0.0;
    private boolean filterInitialized = false;

    private double lastAppliedPower = 0.0;

    // Timing for proper dt
    private long prevTimeNs = 0L;
    private double lastDtSec = 0.0;

    // Telemetry helpers
    private double lastPidOut = 0.0;
    private double lastFF = 0.0;
    private double lastCommandCapped = 0.0;
    private double lastPScale = 1.0;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");
        limelightSubsystem = new LimelightSubsystem(hardwareMap);

        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pid.setSetPoint(0.0);
        pid.setTolerance(DEAD_BAND_DEG);
    }

    public void setTurretPower(double power) {
        lastAppliedPower = MathUtils.clamp(power, -TURRET_MAX_POWER, TURRET_MAX_POWER);
        turretMotor.setPower(lastAppliedPower);
    }

    public void stopTurret() {
        lastAppliedPower = 0.0;
        turretMotor.setPower(0);
        pid.reset();
        prevTimeNs = 0L;
        filterInitialized = false;
    }

    /**
     * Main auto-aim loop. Call periodically from your OpMode.
     */
    public void autoLockTurret() {
        if (!limelightSubsystem.hasValidTarget()) {
            stopTurret();
            return;
        }

        final double tx = limelightSubsystem.getTargetOffset();

        // Initialize the filter to avoid a large first-step transient
        if (!filterInitialized) {
            filteredOffsetDeg = tx;
            filterInitialized = true;
        } else {
            filteredOffsetDeg = LIMELIGHT_ALPHA * tx + (1.0 - LIMELIGHT_ALPHA) * filteredOffsetDeg;
        }

        final double errorDeg = filteredOffsetDeg;

        // Deadband: hold position (also resets PID internal state)
        if (Math.abs(errorDeg) <= DEAD_BAND_DEG) {
            stopTurret();
            return;
        }

        // Gain-scheduling on P for smoother behavior across error magnitudes
        double absErr = Math.abs(errorDeg);
        double pScale = absErr > ERR_HIGH_DEG ? KP_SCALE_HIGH :
                absErr > ERR_MED_DEG  ? KP_SCALE_MED  : KP_SCALE_LOW;
        lastPScale = pScale;
        pid.setP(kP * pScale);
        pid.setD(kD);

        // Compute dt in seconds with fallback to limelight poll rate
        long now = System.nanoTime();
        double dtSec;
        if (prevTimeNs == 0L) {
            dtSec = 1.0 / LIMELIGHT_POLL_HZ;
        } else {
            dtSec = (now - prevTimeNs) * 1e-9;
            if (dtSec <= 0.0 || dtSec > 1.0) {
                dtSec = 1.0 / LIMELIGHT_POLL_HZ;
            }
        }
        prevTimeNs = now;
        lastDtSec = dtSec;

        // PID output around setpoint = 0 using current "errorDeg" (library expects value; setpoint already 0)
        double pidOut = pid.calculate(errorDeg, dtSec);

        // Minimal static feedforward to overcome stiction.
        // If you migrate to PIDFController, wire FEEDFORWARD_KS there and remove this explicit addition.
        double ff = Math.signum(pidOut != 0.0 ? pidOut : errorDeg) * FEEDFORWARD_KS;

        double commanded = pidOut + ff;

        // Clamp to allowed motor range
        double capped = MathUtils.clamp(commanded, -TURRET_MAX_POWER, TURRET_MAX_POWER);

        // Soft mechanical limits from potentiometer feedback
        double posDeg = getTurretPosition();
        if (!Double.isNaN(posDeg)) {
            if (posDeg <= SOFT_MIN_DEG && capped < 0) capped = 0.0;
            if (posDeg >= SOFT_MAX_DEG && capped > 0) capped = 0.0;
        }

        // Slew-rate limit per loop to avoid sudden steps
        double delta = capped - lastAppliedPower;
        if (delta > MAX_POWER_DELTA_PER_LOOP) delta = MAX_POWER_DELTA_PER_LOOP;
        if (delta < -MAX_POWER_DELTA_PER_LOOP) delta = -MAX_POWER_DELTA_PER_LOOP;

        setTurretPower(lastAppliedPower + delta);

        // Save for telemetry
        lastPidOut = pidOut;
        lastFF = ff;
        lastCommandCapped = capped;
    }

    /**
     * Returns the turret position in degrees based on the potentiometer.
     */
    public double getTurretPosition() {
        double v = Math.max(POT_MIN_V, Math.min(pot.getVoltage(), POT_MAX_V));
        double fraction = (v - POT_MIN_V) / (POT_MAX_V - POT_MIN_V);
        return fraction * (SOFT_MAX_DEG - SOFT_MIN_DEG) + SOFT_MIN_DEG;
    }

    public void telemetrize(Telemetry telemetry) {
        telemetry.addData("pot V", pot.getVoltage());
        telemetry.addData("turret pos (deg)", getTurretPosition());
        telemetry.addData("tx raw", limelightSubsystem.getTargetOffset());
        telemetry.addData("tx filtered", filteredOffsetDeg);
        telemetry.addData("has target", limelightSubsystem.hasValidTarget());
        telemetry.addData("power cmd (applied)", lastAppliedPower);
        telemetry.addData("PID out", lastPidOut);
        telemetry.addData("FF (kS)", lastFF);
        telemetry.addData("cmd capped", lastCommandCapped);
        telemetry.addData("dt (ms)", lastDtSec * 1000.0);
        telemetry.addData("P scale", lastPScale);
        telemetry.addData("P", pid.getP());
        telemetry.addData("D", pid.getD());
    }
}