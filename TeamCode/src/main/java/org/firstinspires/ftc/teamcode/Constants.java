package org.firstinspires.ftc.teamcode;

public class Constants {

    // Define the nested static class for Intake constants
    public static class IntakeConstants {
        public static final double INTAKE_POWER = 1;
        public static final double OUTTAKE_POWER = -1;
        public static final double STALL_CURRENT_AMPS = 9.2;
    }

    public static class limelightConstants {
        public static final int LIMELIGHT_POLL_HZ = 100;
    }

    public static class TurretConstants {
        public static final double DEAD_BAND_DEG = 1.0;
        public static final double SOFT_MIN_DEG = -133.0;
        public static final double SOFT_MAX_DEG = 133.0;
        public static final double TURRET_HOME_ANGLE_DEG = 0.0;
        public static final double MIN_TURRET_POWER = 0.05;
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
}
