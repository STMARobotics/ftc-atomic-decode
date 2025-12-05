package org.firstinspires.ftc.teamcode.Subsystems;

import com.seattlesolvers.solverslib.util.InterpLUT;

/**
 * Subsystem wrapper that exposes lookup-table based shooter settings.
 */
public class LookupTable {

    InterpLUT lutShooter = new InterpLUT();
    InterpLUT lutHood = new InterpLUT();


    public LookupTable() {
    }

    public LookupTable addPointShooter(double distanceMeters, double shooterRPM) {
        lutShooter.add(distanceMeters, shooterRPM);
        return this;
    }

    public LookupTable addPointHood(double distanceMeters, double hoodAngleDegrees) {
        lutHood.add(distanceMeters, hoodAngleDegrees);
        return this;
    }

    public LookupTable createTables() {
        lutShooter.createLUT();
        lutHood.createLUT();
        return this;
    }

    /**
     * Get shooter RPM for a specific distance in meters.
     *
     * @param distanceMeters distance to target in meters
     * @return shooter wheel speed in RPM
     */
    public double getShooterRPM(double distanceMeters) {
        return lutShooter.get(distanceMeters);
    }

    /**
     * Get hood angle (degrees) for a specific distance in meters.
     *
     * @param distanceMeters distance to target in meters
     * @return hood pitch in degrees
     */
    public double getHoodAngle(double distanceMeters) {
        return lutHood.get(distanceMeters);
    }
}
