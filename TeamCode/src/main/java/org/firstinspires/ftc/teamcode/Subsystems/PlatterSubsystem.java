package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.Constants.ArtifactColor;

public class PlatterSubsystem extends SubsystemBase {

    public final CRServo platterServo;
    public final Servo launcherServo;
    public final CRServo launchableLeft; // artifact grabber rollers pls change this
    public final CRServo launchableRight;

    // Stored target color
    private ArtifactColor artifactColor;

    public PlatterSubsystem(HardwareMap hardwareMap) {
        platterServo = hardwareMap.get(CRServo.class, "platterServo");
        launcherServo = hardwareMap.get(Servo.class, "launcherServo");
        launchableLeft = hardwareMap.get(CRServo.class, "launchableLeft");
        launchableRight = hardwareMap.get(CRServo.class, "launchableRight");
    }

    // color sensor, magnet switch
    // launcher servo, artifact grabbers

    /**
     * Spins the platter at a given power
     * This is a backup if were not using platterSpin()
     * @param power the power to spin the platter at
     */
    public void spinPlatter(double power) {
        platterServo.setPower(power);
    }

    /**
     * Idles the platter, this is always called unless were
     * needing to use the platter
     */
    public void idlePlatter() {
        platterServo.setPower(0.2);
    }

    /**
     * Stops the platter from spinning
     */
    public void stopPlatter() {
        platterServo.setPower(0);
    }

    /**
     * Checks the color detected by the color sensor
     * @return returns the color detected as an enum
     */
    public ArtifactColor checkColor() {
        // TODO: implement color sensor logic later; placeholder for now
        return ArtifactColor.GREEN; // placeholder
    }

    /**
     * Checks if the detected color matches the given target color.
     * ALL is treated as a wildcard target that always matches.
     */
    public boolean isCorrectColor(ArtifactColor target) {
        return target == ArtifactColor.ALL || checkColor() == target;
    }

    /**
     * Checks if the magnet switch is tripped
     * @return true if the magnet switch is tripped, false otherwise
     */
    public boolean isMagnetTripped() {
        // TODO: implement magnet switch logic
        return false; // placeholder
    }

    /**
     * Activates the spring loaded launcher with a servo
     */
    public void launcherActivate() {
        launcherServo.setPosition(90); // placeholder
    }

    /**
     * Deactivates the spring loaded launcher with a servo
     */
    public void launcherDeactivate() {
        launcherServo.setPosition(0); // placeholder
    }

    /**
     * Spins the rollers that grab the launched artifacts
     */
    public void launchableActivate() {
        launchableLeft.setPower(1.0);
        launchableRight.setPower(1.0);
    }

    /**
     * Stops the rollers that grab the launched artifacts
     */
    public void launchableStop() {
        launchableLeft.setPower(0);
        launchableRight.setPower(0);
    }

    /**
     * Reverses the rollers that grab the launched artifacts
     */
    public void launchableReverse() {
        launchableLeft.setPower(-1.0);
        launchableRight.setPower(-1.0);
    }
}
