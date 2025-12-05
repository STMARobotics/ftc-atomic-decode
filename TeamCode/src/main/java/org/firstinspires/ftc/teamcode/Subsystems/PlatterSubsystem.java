package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.INDEX_POWER;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Constants.ArtifactColor;

public class PlatterSubsystem extends SubsystemBase {

    public final CRServo platterServo;
    public final Servo launcherServo;
    public final CRServo launchableLeft; // artifact grabber rollers
    public final CRServo launchableRight;

    private final NormalizedColorSensor colorSensorLeft;
    private final DistanceSensor distanceSensorLeft;
    private final TouchSensor magnetSwitch;

    private ArtifactColor artifactColor;

    public PlatterSubsystem(HardwareMap hardwareMap) {
        platterServo = hardwareMap.get(CRServo.class, "platterServo");
        launcherServo = hardwareMap.get(Servo.class, "launcherServo");
        launchableLeft = hardwareMap.get(CRServo.class, "launchableLeft");
        launchableRight = hardwareMap.get(CRServo.class, "launchableRight");

        platterServo.setDirection(DcMotorSimple.Direction.REVERSE);

        colorSensorLeft = hardwareMap.get(NormalizedColorSensor.class, "cSensorLeft");
        NormalizedColorSensor colorSensorRight = hardwareMap.get(NormalizedColorSensor.class, "cSensorRight");
        distanceSensorLeft = hardwareMap.get(DistanceSensor.class, "cSensorLeft");
        DistanceSensor distanceSensorRight = hardwareMap.get(DistanceSensor.class, "cSensorRight");

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
        platterServo.setPower(0.12);
    }

    public void nextMagnet() {
        if (!isMagnetTripped()) {
            spinPlatter(INDEX_POWER);
        } else {
            stopPlatter();
        }
    }

    /**
     * Stops the platter from spinning
     */
    public void stopPlatter() {
        platterServo.setPower(-0.05);
    }

    public ArtifactColor checkColor() {
        NormalizedRGBA cL = colorSensorLeft.getNormalizedColors();
        NormalizedRGBA cR = colorSensorLeft.getNormalizedColors();
        if (cL.green < 0.01 && cL.blue < 0.01 && cR.green < 0.01 && cR.blue < 0.01) {
            return ArtifactColor.NONE;
        }

        return cL.green >= 0.012 && cR.green >= 0.012 ? ArtifactColor.GREEN : ArtifactColor.PURPLE;
    }

    /**
     * Checks if the detected color matches the given target color.
     * ALL is treated as a wildcard target that always matches.
     */
    public boolean isCorrectColor(ArtifactColor target) {
        return target == ArtifactColor.ALL || checkColor() == target;
    }

    /**
     * Checks if there is an artifact present
     * @return true if an artifact is present, false otherwise
     */
    public boolean hasArtifact() {
        double d = distanceSensorLeft.getDistance(DistanceUnit.MM);

        if (Double.isNaN(d) || d <= 0) {
            return false;
        }

        return d < 20.0;
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
        launcherServo.setPosition(0.5); // placeholder, tune on robot
    }

    /**
     * Deactivates the spring loaded launcher with a servo
     */
    public void launcherDeactivate() {
        launcherServo.setPosition(0.2); // placeholder
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
    public void launchableDeactivate() {
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

        telemetry.addData("Distance (mm)", "%.2f", distanceSensorLeft.getDistance(DistanceUnit.MM));
        telemetry.addData("Magnet Tripped", isMagnetTripped() ? "Yes" : "No");

        if (hasArtifact()) {
            telemetry.addData("Artifact Present", "Yes (%s)", detected);
        } else {
            telemetry.addData("Artifact Present", "No");
        }
    }
}
