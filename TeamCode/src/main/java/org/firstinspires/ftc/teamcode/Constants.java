package org.firstinspires.ftc.teamcode;

import java.util.List;
import org.firstinspires.ftc.teamcode.Math.LookupTableMath;

/**
 * Central constants container.
 * Nested classes mirror the static imports used throughout the codebase.
 */
public final class Constants {

    public static final class limelightConstants {
        public static final int LIMELIGHT_POLL_HZ = 30;
    }

    public static final class TurretConstants {

        public static final double DEAD_BAND_DEG = 1.0;
        public static final double SOFT_MIN_DEG = -133.0;
        public static final double SOFT_MAX_DEG = 133.0;
        public static final double TURRET_HOME_ANGLE_DEG = 0.0;
        public static final double MIN_TURRET_POWER = 0.05;
        public static final double POT_MIN_V = 0.2; // placeholder
        public static final double POT_MAX_V = 3.0; // placeholder
    }

    public static class ClimbConstants {
        public static final double CLIMB_POWER = 1.0;
        public static final double STALL_CURRENT_AMPS = 9.2;
        public static final double CLUTCH_ENGAGED_POS = 0.8; // placeholder
        public static final double CLUTCH_DISENGAGED_POS = 0; // placeholder
    }

    public static enum AllianceColor {
        RED,
        BLUE
    }

    public static enum ArtifactColor {
        PURPLE,
        GREEN,
        ALL
    }

    // Shooter interpolator values: distances are meters, velocities are rotations/sec, pitch is degrees
    public static final LookupTableMath SHOOTER_INTERPOLATOR = new LookupTableMath(
            List.of(
                    new LookupTableMath.ShootingSettings().distance(1.0400968).velocity(50).pitch(34.0)
            ));
}
