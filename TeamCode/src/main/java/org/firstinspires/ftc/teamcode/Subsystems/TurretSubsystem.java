package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.*;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

public class TurretSubsystem {

    // Remove external PID; use simple PD (default D=0 -> P-only)
    private double kP = 0.5;
    private double kD = 0.0;
    private double prevError = 0.0;
    private boolean hasPrevError = false;

    private final DcMotorEx turretMotor;
    private final Limelight3A limelight;
    private final AnalogInput pot;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");

        limelight.setPollRateHz(LIMELIGHT_POLL_HZ);
        limelight.start();

        // Optional: motor safety defaults
        turretMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Sets the turret motor power
     * @param power the power to set the turret motor to (-1 to 1)
     */
    public void setTurretPower(double power) {
        turretMotor.setPower(Range.clip(power, -1.0, 1.0));
    }

    /**
     * Stops the turret motor
     */
    public void stopTurret() {
        turretMotor.setPower(0);
        hasPrevError = false; // reset derivative state
    }

    /**
     * Automatically aims the turret at the target using simple PD (D optional).
     */
    public void autolockTurret() {
        double error = getTargetOffset();

        if (Double.isNaN(error)) {
            stopTurret();
            return;
        }

        if (Math.abs(error) <= DEAD_BAND_DEG) {
            stopTurret();
            return;
        }

        double dTerm = 0.0;
        if (hasPrevError) {
            dTerm = (error - prevError);
        }
        prevError = error;
        hasPrevError = true;

        double output = kP * error + kD * dTerm;

        double sign = Math.signum(output == 0 ? error : output);
        output = sign * Math.max(Math.abs(output), MIN_TURRET_POWER);

        // Soft limits to protect hardware
        double position = getTurretPosition();
        if (!Double.isNaN(position)) {
            if (position <= SOFT_MIN_DEG && output < 0) {
                output = 0.0;
            } else if (position >= SOFT_MAX_DEG && output > 0) {
                output = 0.0;
            }
        }

        setTurretPower(output);
    }

    /**
     * Optional: adjust gains at runtime
     */
    public void setGains(double kP, double kD) {
        this.kP = kP;
        this.kD = kD;
    }

    /**
     * Switches the limelight pipeline
     * @param pipeline the pipeline index to switch to
     */
    public void pipelineSwitcher(int pipeline) {
        limelight.pipelineSwitch(pipeline); // 0 = blue, 1 = red
    }

    /**
     * Returns the horizontal offset from crosshair to target
     * Uses LLResult.getTx() per Limelight FTC API. Returns NaN if no valid result.
     * @return the horizontal offset in degrees (NaN if unavailable)
     */
    public double getTargetOffset() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return Double.NaN;
        }
        return result.getTx();
    }

    /**
     * Returns the turret position in degrees.
     */
    public double getTurretPosition() {
         double v = pot.getVoltage(); // 0..3.3V
         return Range.scale(v, POT_MIN_V, POT_MAX_V, SOFT_MIN_DEG, SOFT_MAX_DEG);
    }
}
