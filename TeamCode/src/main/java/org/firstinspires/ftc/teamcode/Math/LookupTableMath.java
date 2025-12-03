package org.firstinspires.ftc.teamcode.Math;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Lightweight lookup table math utility.
 * This is a simplified replacement of the WPILib-based interpolator used in the original code.
 * It stores (distance -> velocity, pitch) pairs and linearly interpolates between the nearest
 * points. All units are plain doubles: distance in meters, velocity in rotations-per-second,
 * pitch in degrees.
 */
public class LookupTableMath {

    private final NavigableMap<Double, Double> distanceVelocityMap = new TreeMap<>();
    private final NavigableMap<Double, Double> distancePitchMap = new TreeMap<>();

    /**
     * Constructor that takes a list of settings that will be loaded to the table.
     *
     * @param settingsList list of settings (distance meters, velocity rps, pitch degrees)
     */
    public LookupTableMath(List<ShootingSettings> settingsList) {
        for (ShootingSettings settings : settingsList) {
            distanceVelocityMap.put(settings.distance, settings.velocity);
            distancePitchMap.put(settings.distance, settings.pitch);
        }
    }

    /**
     * Calculates the shooter velocity and pitch by interpolating based on the distance.
     *
     * @param distance distance to the target (meters)
     * @return shooter settings with distance (m), velocity (rps), pitch (degrees)
     */
    public ShootingSettings calculate(double distance) {
        double vel = interpolate(distanceVelocityMap, distance);
        double pitch = interpolate(distancePitchMap, distance);
        return new ShootingSettings().distance(distance).velocity(vel).pitch(pitch);
    }

    private double interpolate(NavigableMap<Double, Double> map, double x) {
        if (map.isEmpty()) {
            return 0.0;
        }
        Double firstKey = map.firstKey();
        Double lastKey = map.lastKey();
        if (x <= firstKey) {
            return map.get(firstKey);
        }
        if (x >= lastKey) {
            return map.get(lastKey);
        }
        Double floorKey = map.floorKey(x);
        Double ceilKey = map.ceilingKey(x);
        if (floorKey == null || ceilKey == null || floorKey.equals(ceilKey)) {
            return map.get(x);
        }
        double x0 = floorKey;
        double x1 = ceilKey;
        double y0 = map.get(x0);
        double y1 = map.get(x1);
        double t = (x - x0) / (x1 - x0);
        return y0 + (y1 - y0) * t;
    }

    /** Shooter settings container (plain doubles). */
    public static class ShootingSettings {
        private double distance = 0.0; // meters
        private double velocity = 0.0; // rotations per second
        private double pitch = 0.0; // degrees

        public ShootingSettings distance(double distance) {
            this.distance = distance;
            return this;
        }

        public ShootingSettings velocity(double velocity) {
            this.velocity = velocity;
            return this;
        }

        public ShootingSettings pitch(double pitch) {
            this.pitch = pitch;
            return this;
        }

        public double getVelocity() {
            return velocity;
        }

        public double getPitch() {
            return pitch;
        }
    }
}