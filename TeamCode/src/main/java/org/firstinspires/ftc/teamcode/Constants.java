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
        public static final double MAX_TURRET_POWER = 0.5;
    }

    public static class TurretConstants {
        public static final double DEAD_BAND_DEG = 1.0;
        public static final double TURRET_MIN_ANGLE_DEG = -135.0;
        public static final double TURRET_MAX_ANGLE_DEG = 135.0;
        public static final double TURRET_HOME_ANGLE_DEG = 0.0;
        public static final double MIN_TURRET_POWER = 0.05;
    }
}
