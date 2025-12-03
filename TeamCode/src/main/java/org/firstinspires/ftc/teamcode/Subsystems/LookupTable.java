package org.firstinspires.ftc.teamcode.Subsystems;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import org.firstinspires.ftc.teamcode.Constants;

import org.firstinspires.ftc.teamcode.Math.LookupTableMath.ShootingSettings;

/**
 * Subsystem wrapper that exposes lookup-table based shooter settings.
 */
public class LookupTable extends SubsystemBase {

    public LookupTable() {
        // No initialization required
    }

    /**
     * Get shooter RPM for a specific distance in meters.
     *
     * @param distanceMeters distance to target in meters
     * @return shooter wheel speed in RPM
     */
    public double getShooterRPM(double distanceMeters) {
        ShootingSettings settings = Constants.SHOOTER_INTERPOLATOR.calculate(distanceMeters);
        // settings.getVelocity() returns rotations per second; convert to RPM
        return settings.getVelocity() * 60.0;
    }

    /**
     * Get hood angle (degrees) for a specific distance in meters.
     *
     * @param distanceMeters distance to target in meters
     * @return hood pitch in degrees
     */
    public double getHoodAngle(double distanceMeters) {
        ShootingSettings settings = Constants.SHOOTER_INTERPOLATOR.calculate(distanceMeters);
        return settings.getPitch();
    }
}
