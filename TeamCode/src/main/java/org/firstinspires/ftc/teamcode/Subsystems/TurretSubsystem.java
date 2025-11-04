package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;

public class TurretSubsystem {

    private double kP = 0.5;
    private double kD = 0.0;
    private double prevError = 0.0;
    private boolean hasPrevError = false;

    private final DcMotorEx turretMotor;
    private final AnalogInput pot;

    private LimelightSubsystem limelightSubsystem;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");

        limelightSubsystem = new LimelightSubsystem(hardwareMap);

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
        double error = limelightSubsystem.getTargetOffset();

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
     * Returns the turret position in degrees.
     */
    public double getTurretPosition() {
         double v = pot.getVoltage(); // 0..3.3V
         return Range.scale(v, POT_MIN_V, POT_MAX_V, SOFT_MIN_DEG, SOFT_MAX_DEG);
    }
}
