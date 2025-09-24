package org.firstinspires.ftc.teamcode.Subsystems;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

/**
 * Subsystem for detecting AprilTags with the webcam
 */
public class AprilTagSubsystem extends SubsystemBase {

    private final AprilTagProcessor aprilTagProcessor;
    private final VisionPortal visionPortal;

    public AprilTagSubsystem(HardwareMap hardwareMap) {
        aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(
                WebcamName.class, "Webcam 1"), aprilTagProcessor);
    }

    /**
     * Stops streaming. Useful to save CPU
     */
    public void stopStreaming() {
        visionPortal.stopStreaming();
    }

    /**
     * Resumes streaming if it was previously stopped
     */
    public void resumeStreaming() {
        visionPortal.resumeStreaming();
    }

    /**
     * Gets the current AprilTag detections from the camera
     * @return list of detections
     */
    public List<AprilTagDetection> getDetections() {
        return aprilTagProcessor.getDetections();
    }

    /**
     * Adds AprilTag data to telemetry
     * @param telemetry telemetry
     * @param currentDetections detections to add
     */
    @SuppressLint("DefaultLocale")
    public static void telemetryDetections(Telemetry telemetry, List<AprilTagDetection> currentDetections) {
        telemetry.addData("# AprilTags Detected", currentDetections.size());
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }
    }

}
