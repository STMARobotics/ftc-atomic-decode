package org.firstinspires.ftc.teamcode;

import java.util.List;
import org.firstinspires.ftc.teamcode.Math.LookupTableMath;

/**
 * Central constants container.
 * Nested classes mirror the static imports used throughout the codebase.
 */
public final class Constants {

    public static final class limelightConstants {
        // Nominal limelight update rate; used for dt fallback
        public static final int LIMELIGHT_POLL_HZ = 90;
    }

    public static final class TurretConstants {

        // Control behavior
        public static final double DEAD_BAND_DEG = 2.0;

        // Mechanical range (deg) and home
        public static final double SOFT_MIN_DEG = -135.0;
        public static final double SOFT_MAX_DEG = 135.0;
        public static final double TURRET_HOME_ANGLE_DEG = 0.0;

        // Motor command shaping
        public static final double TURRET_MAX_POWER = 0.5;
        public static final double MAX_POWER_DELTA_PER_LOOP = 0.1;

        // Limelight smoothing (0..1], higher = more weight on new sample
        public static final double LIMELIGHT_ALPHA = 0.25;

        // PID base gains (scaled at runtime based on error magnitude)
        public static final double TURRET_KP = 0.028;
        public static final double TURRET_KD = 0.004;

        // Simple gain-scheduling thresholds/scales
        public static final double ERR_MED_DEG = 5.0;
        public static final double ERR_HIGH_DEG = 15.0;
        public static final double KP_SCALE_LOW = 0.35;
        public static final double KP_SCALE_MED = 0.60;
        public static final double KP_SCALE_HIGH = 1.00;

        // Static friction compensation (feedforward)
        public static final double FEEDFORWARD_KS = 0.05;

        // Potentiometer calibration (empirical)
        public static final double POT_MIN_V = 0.94;
        public static final double POT_MAX_V = 1.739;
        public static final double POT_HOME_V = 1.319;
    }

    public static class ClimbConstants {
        public static final double CLIMB_POWER = 1.0;
        public static final double STALL_CURRENT_AMPS = 9.2;
        public static final double CLUTCH_ENGAGED_POS = 0.8; // placeholder
        public static final double CLUTCH_DISENGAGED_POS = 0; // placeholder
    }

    public static class IntakeConstants {
        public static final double INTAKE_POWER = 1.0;
        public static final double OUTTAKE_POWER = -1.0;
        public static final double STALL_CURRENT_AMPS = 9.2;
    }

    public static class DriveCommandConstants {
        public static final double JOYSTICK_DEADZONE = 0.05;
    }

    public static enum AllianceColor {
        RED,
        BLUE
    }

    public static enum ArtifactColor {
        PURPLE,
        GREEN,
        ALL,
        NONE
    }

    // Shooter interpolator values: distances are meters, velocities are rotations/sec, pitch is degrees
    public static final LookupTableMath SHOOTER_INTERPOLATOR = new LookupTableMath(
            List.of(
                    new LookupTableMath.ShootingSettings().distance(1.0400968).velocity(50).pitch(34.0)
            ));
}