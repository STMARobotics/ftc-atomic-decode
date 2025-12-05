package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.Math.LookupTableMath;

/**
 * Central constants container.
 * Nested classes mirror the static imports used throughout the codebase.
 */
public final class Constants {

    public static final class limelightConstants {
        public static final int LIMELIGHT_POLL_HZ = 90;
        public static final int BLUE_PIPELINE = 0;
        public static final int RED_PIPELINE  = 1;
    }

    public static final class TurretConstants {

        public static final double DEAD_BAND_DEG = 3.0;

        public static final double SOFT_MIN_DEG = -135.0;
        public static final double SOFT_MAX_DEG = 135.0;
        public static final double TURRET_HOME_DEG = 0.0;

        public static final double TURRET_MAX_POWER = 0.65;

        public static final double TURRET_KP = 0.03;
        public static final double TURRET_KD = 0.001;
        public static final double FEEDFORWARD_KS = 0.05;

        public static final double POT_MIN_V = 0.94;
        public static final double POT_MAX_V = 1.739;
        public static final double POT_HOME_V = 1.319;
    }

    public static class ClimbConstants {
        public static final double CLIMB_POWER = 1.0;
        public static final double CLUTCH_ENGAGED_POS = 0.8; // placeholder
        public static final double CLUTCH_DISENGAGED_POS = 0; // placeholder
    }

    public static class IntakeConstants {
        public static final double INTAKE_POWER = 1.0;
        public static final double OUTTAKE_POWER = -1.0;
        public static final double STALL_CURRENT_AMPS = 9.2;
    }

    public static class DriveConstants {
        public static final double JOYSTICK_DEADZONE = 0.05;
        public static final double NORMAL_DRIVE_SCALE = 1.0;
        public static final double SLOW_DRIVE_SCALE   = 0.5;
    }

    public static class PlatterConstants {
        public static final double SEARCH_POWER = 0.2;
        public static final double NUDGE_POWER  = -0.15;
        public static final double SHOOT_POWER = 0.4;
        public static final double INDEX_POWER = 0.2;
    }

    public enum AllianceColor {
        RED,
        BLUE
    }

    public enum ArtifactColor {
        PURPLE,
        GREEN,
        ALL,
        NONE
    }

    public static final LookupTableMath INTERPOLATOR = new LookupTableMath()
            .addEntry(2.16, 3300, .61)
            .addEntry(-0.25, 3300, .64)
            .addEntry(-2.2, 3300, .625)
            .addEntry(-5.0, 3400, .64)
            .addEntry(-8.54, 3600, .62)
            .addEntry(-12.7, 4200, .70)
            .addEntry(-13.0, 4300, .73);
}