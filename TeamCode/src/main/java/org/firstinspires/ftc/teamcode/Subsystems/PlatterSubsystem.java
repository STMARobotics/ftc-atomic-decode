package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.ArtifactColor;

public class PlatterSubsystem extends SubsystemBase {

    public final CRServo platterServo;
    public final Servo launcherServo;
    public final CRServo launchableLeft;  // artifact grabber rollers
    public final CRServo launchableRight;

    private final NormalizedColorSensor colorSensorBottom;
    private final NormalizedColorSensor colorSensorWall;
    private final TouchSensor magnetSwitch;

    private ArtifactColor artifactColor;

    public PlatterSubsystem(HardwareMap hardwareMap) {
        platterServo = hardwareMap.get(CRServo.class, "platterServo");
        launcherServo = hardwareMap.get(Servo.class, "launcherServo");
        launchableLeft = hardwareMap.get(CRServo.class, "launchableLeft");
        launchableRight = hardwareMap.get(CRServo.class, "launchableRight");

        launchableRight.setDirection(CRServo.Direction.REVERSE);

        colorSensorBottom = hardwareMap.get(NormalizedColorSensor.class, "cSensorBottom");
        colorSensorWall = hardwareMap.get(NormalizedColorSensor.class, "cSensorWall");

        magnetSwitch = hardwareMap.get(TouchSensor.class, "magnetSwitch");
    }

    /**
     * Spins the platter at a given power
     * This is a backup if we're not using platterSpin()
     * @param power the power to spin the platter at
     */
    public void spinPlatter(double power) {
        platterServo.setPower(power);
    }

    /**
     * Idles the platter, this is always called unless we're
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

    public ArtifactColor checkColor() {
        NormalizedRGBA cB = colorSensorBottom.getNormalizedColors();
        NormalizedRGBA cW = colorSensorBottom.getNormalizedColors();
        if (cB.green < 0.01 && cB.blue < 0.01 && cW.green < 0.01 && cW.blue < 0.01) {
            return ArtifactColor.NONE;
        }

        return cB.green >= 0.012 && cW.green >= 0.012 ? ArtifactColor.GREEN : ArtifactColor.PURPLE;
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
        return magnetSwitch.isPressed();
    }

    /**
     * Activates the spring loaded launcher with a servo
     */
    public void launcherActivate() {
        launcherServo.setPosition(0.6); // placeholder, tune on robot
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

    /**
     * Sends telemetry data about the platter subsystem.
     * @param telemetry the telemetry object to send data to
     */
    public void telemetrize(Telemetry telemetry) {
        ArtifactColor detected = checkColor();

        if (colorSensorWall != null) {
            NormalizedRGBA cW = colorSensorWall.getNormalizedColors();
            telemetry.addData("Raw R Wall", "%.3f", cW.red);
            telemetry.addData("Raw G Wall", "%.3f", cW.green);
            telemetry.addData("Raw B Wall", "%.3f", cW.blue);
            telemetry.addData("Color found Wall", checkColor());
        }
        telemetry.addData(" ", null);
        if (colorSensorBottom != null) {
            NormalizedRGBA cB = colorSensorBottom.getNormalizedColors();
            telemetry.addData("Raw R Bottom", "%.3f", cB.red);
            telemetry.addData("Raw G Bottom", "%.3f", cB.green);
            telemetry.addData("Raw B Bottom", "%.3f", cB.blue);
            telemetry.addData("Color found Wall", checkColor());
        }
    }
}
