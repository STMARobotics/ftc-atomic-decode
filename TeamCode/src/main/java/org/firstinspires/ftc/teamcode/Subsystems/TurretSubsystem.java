package org.firstinspires.ftc.teamcode.Subsystems;

import com.pedropathing.control.PIDFController;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.util.MathUtils;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private final AnalogInput pot;
    private final LimelightSubsystem limelightSubsystem;

    private double lastAppliedPower = 0.0;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");
        limelightSubsystem = new LimelightSubsystem(hardwareMap);

        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setTurretPower(double power) {
        lastAppliedPower = MathUtils.clamp(power, -TURRET_MAX_POWER, TURRET_MAX_POWER);
        turretMotor.setPower(lastAppliedPower);
    }

    public void stopTurret() {
        lastAppliedPower = 0.0;
        turretMotor.setPower(0);
    }

    public void goToHome() {
    }

    /**
     * Main auto-aim loop using limelight tx.
     * Implements:
     *  - Low-pass filtering of limelight offset
     *  - Hysteresis deadband (lock when |error| <= DEAD_BAND_DEG, unlock when > DEAD_BAND_DEG + DEAD_BAND_HYSTERESIS_DEG)
     *  - Manual PD + static friction feedforward (kS) outside deadband
     *  - Slew rate limiting per loop
     */
    public void autoLockTurret() {
        double error = limelightSubsystem.getTargetOffset();
        if (Double.isNaN(error)) {
            stopTurret();
        }
        if (!limelightSubsystem.hasValidTarget()) {
            stopTurret();
        }
        double output = pidController.calculate(error, 0.0);

        setTurretPower(output);
    }

    public boolean isLockedOn(double thresholdDeg) {
        double tx = limelightSubsystem.getTargetOffset();
        if (Double.isNaN(tx)) {
            return false;
        }

        return Math.abs(tx) <= thresholdDeg;
    }

    /**
     * Convenience overload that uses the turret deadband as a default lock threshold.
     * If you prefer a separate limelight-specific threshold, pass it to isLockedOn(double).
     *
     * @return true when locked using the default DEAD_BAND_DEG threshold
     */
    public boolean isLockedOn() {
        return isLockedOn(DEAD_BAND_DEG);
    }

    /**
     * Returns the turret position in degrees based on the potentiometer.
     */
    public double getTurretPosition() {
        double v = Math.max(POT_MIN_V, Math.min(pot.getVoltage(), POT_MAX_V));
        double fraction = (v - POT_MIN_V) / (POT_MAX_V - POT_MIN_V);
        return fraction * (SOFT_MAX_DEG - SOFT_MIN_DEG) + SOFT_MIN_DEG;
    }
}
