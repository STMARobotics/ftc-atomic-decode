package org.firstinspires.ftc.teamcode;

public class Constants {

    // Define the nested static class for Intake constants
    public static class IntakeConstants {
        public static final double INTAKE_POWER = 0.8;
        public static final double OUTTAKE_POWER = -0.6;
        public static final double STALL_CURRENT_AMPS = 9.2;
    }

    // Define the nested static class for Turret constants
    public static class TurretConstants {
        public static final double TURRET_POWER = 0.8;
    }

    // Vision-related constants for Limelight and geometry
    public static class VisionConstants {
        // Mounting geometry (meters and degrees). Measure on your robot and update.
        public static final double CAMERA_HEIGHT_M = 0.30; // height of camera lens from floor
        public static final double CAMERA_PITCH_DEG = 15.0; // camera tilt up (+) relative to horizontal
        public static final double CAMERA_YAW_OFFSET_DEG = 0.0; // yaw misalignment relative to turret/robot forward

        // Target geometry (meters). Set for the tag you’re aiming at (center of tag).
        public static final double TARGET_HEIGHT_M = 0.20; // example tag center height from floor
    }

    // Shooter geometry and kinematics (for hood calculation)
    public static class ShooterConstants {
        public static final double SHOOTER_HEIGHT_M = 0.28; // height of ball exit/flywheel center from floor
        public static final double LAUNCH_SPEED_MPS = 12.0; // measured projectile exit speed
    }
}
