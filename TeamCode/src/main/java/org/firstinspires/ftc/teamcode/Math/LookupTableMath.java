package org.firstinspires.ftc.teamcode.Math;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Lightweight lookup table math utility with safe linear interpolation.
 */
public class LookupTableMath {

    private final NavigableMap<Double, Double> distanceVelocityMap = new TreeMap<>();
    private final NavigableMap<Double, Double> distancePitchMap = new TreeMap<>();

    public LookupTableMath() {}

    /**
     * Constructor that takes a list of settings that will be loaded to the table.
     *
     * @param settingsList list of settings (distance meters, velocity rps, pitch degrees)
     */
    public LookupTableMath(List<ShootingSettings> settingsList) {
        if (settingsList != null) {
            for (ShootingSettings settings : settingsList) {
                if (settings != null) {
                    addEntry(settings.getDistance(), settings.getVelocity(), settings.getPitch());
                }
            }
        }
    }

    /** Add or replace a table entry. */
    public LookupTableMath addEntry(double distance, double velocity, double pitch) {
        // TreeMap does not allow null keys; values may be null but we avoid inserting nulls here.
        distanceVelocityMap.put(distance, velocity);
        distancePitchMap.put(distance, pitch);
        return this;
    }

    /** Calculate interpolated settings for a given distance. */
    public ShootingSettings calculate(double distance) {
        double vel = interpolate(distanceVelocityMap, distance);
        double pitch = interpolate(distancePitchMap, distance);
        return new ShootingSettings().distance(distance).velocity(vel).pitch(pitch);
    }

    private double interpolate(NavigableMap<Double, Double> map, double x) {
        if (map == null || map.isEmpty() || Double.isNaN(x)) {
            return 0.0;
        }

        // exact match (safe)
        if (map.containsKey(x)) {
            Double v = map.get(x);
            return v != null ? v : 0.0;
        }

        Map.Entry<Double, Double> floor = map.floorEntry(x);
        Map.Entry<Double, Double> ceil = map.ceilingEntry(x);

        // out of range: clamp to endpoints (use entry values directly to avoid extra lookups)
        if (floor == null && ceil == null) {
            return 0.0;
        }
        if (floor == null) {
            Double v = nonNullValue(ceil.getValue(), map.firstEntry());
            return v;
        }
        if (ceil == null) {
            Double v = nonNullValue(floor.getValue(), map.lastEntry());
            return v;
        }

        // both present
        Double y0 = floor.getValue();
        Double y1 = ceil.getValue();

        // if either value is null, prefer the other or clamp
        if (y0 == null && y1 == null) {
            return 0.0;
        }
        if (y0 == null) {
            return y1;
        }
        if (y1 == null) {
            return y0;
        }

        double x0 = floor.getKey();
        double x1 = ceil.getKey();

        // guard against degenerate interval
        if (Double.compare(x0, x1) == 0) {
            return y0;
        }

        double t = (x - x0) / (x1 - x0);
        return y0 + (y1 - y0) * t;
    }

    // helper to obtain a non-null value from an entry fallback
    private Double nonNullValue(Double value, Map.Entry<Double, Double> fallbackEntry) {
        if (value != null) {
            return value;
        }
        if (fallbackEntry != null && fallbackEntry.getValue() != null) {
            return fallbackEntry.getValue();
        }
        return 0.0;
    }

    /** Simple container for shooter settings. */
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

        public double getDistance() { return distance; }
        public double getVelocity() { return velocity; }
        public double getPitch() { return pitch; }
    }
}
