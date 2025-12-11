package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.LIMELIGHT_POLL_HZ;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {

    private final Limelight3A limelight;

    public LimelightSubsystem(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(LIMELIGHT_POLL_HZ);
        limelight.start();
    }

    /**
     * Switches the limelight pipeline
     * @param pipeline the pipeline index to switch to
     */
    public void pipelineSwitcher(int pipeline) {
        limelight.pipelineSwitch(pipeline); // 0 = blue, 1 = red
    }

    /**
     * Returns the horizontal offset from crosshair to target
     * Uses LLResult.getTx() per Limelight FTC API. Returns NaN if no valid result.
     * @return the horizontal offset in degrees (NaN if unavailable)
     */
    public double getTargetOffset() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return Double.NaN;
        }
        return result.getTx();
    }

    /**
     * Returns the distance to the target in meters.
     * Uses LLResult.getBotposeAvgDist() per Limelight FTC API. Returns NaN if no valid result.
     * @return the distance to the target in meters (NaN if unavailable)
     * (zero chance this works)
     */
    public double getDistance() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return Double.NaN;
        }
        return result.getTy();
    }

    /**
     * Returns whether the limelight has a valid target
     * @return true if the target is valid, false otherwise
     */
    public boolean hasValidTarget() {
        LLResult result = limelight.getLatestResult();
        return result != null && result.isValid();
    }
}
