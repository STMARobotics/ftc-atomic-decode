package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import java.util.Objects;


public class PlatterSubsystem extends SubsystemBase {

    public final CRServo platterServo;
    public final Servo launcherServo;
    public final CRServo launchableLeft; // artifact grabber rollers pls change this
    public final CRServo launchableRight;

    public PlatterSubsystem(HardwareMap hardwareMap) {
        platterServo = hardwareMap.get(CRServo.class, "hoodServo");
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
     * Spins the platter, probably used for shooting or wrong color
     */
    public void platterSpin() {
        platterServo.setPower(0.8);
    }

    /**
     * Stops the platter from spinning
     */
    public void stopPlatter() {
        platterServo.setPower(0);
    }

    /**
     * Checks the color detected by the color sensor
     * @return returns the color detected as a string
     */
    public String checkColor() {
        // TODO: implement color sensor logic
        return "green"; // placeholder
    }

    /**
     * Checks if the detected color matches the target color
     * @param targetColor the color we want to match
     * @return true if the colors match, false otherwise
     */
    public boolean isCorrectColor(String targetColor) {
        return Objects.equals(checkColor(), targetColor);
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
