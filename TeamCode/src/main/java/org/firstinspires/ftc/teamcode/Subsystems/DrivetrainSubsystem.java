package org.firstinspires.ftc.teamcode.Subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * Drivetrain subsystem. Encapsulates the details of <i>how</i> the drivetrain works.
 */
public class DrivetrainSubsystem extends SubsystemBase {

    private final Follower follower;

    private Pose currentPose = new Pose();

    public DrivetrainSubsystem(HardwareMap hardwareMap) {
        follower = Constants.createFollower(hardwareMap);
    }

    /**
     * Drive the robot in field centric manner. This function also squares the inputs for better
     * fine control.
     * @param translationX robot strafe along the X axis in range [-1, 1]. The X axis runs along the
     *                     field perimeter on the audience side. The robot is facing the positive
     *                     X direction when it has a heading of 0 radians (0°)
     * @param translationY robot speed along the Y axis in range [-1, 1]. The Y axis runs along the
     *                     field perimeter on the red alliance side. The robot is facing the
     *                     positive Y direction when it has a heading of 1/2 PI radians (90°).
     * @param rotation robot rotation speed in range of [-1, 1]. Counterclockwise positive
     * @param reductionFactor value to multiply the speed parameters by in range [0, 1]
     */
    public void drive(double translationX, double translationY, double rotation, double reductionFactor) {
        double clampedReduction = MathUtils.clamp(reductionFactor, 0.0, 1.0);

        // Square and reduce the axes
        double modifiedY = square(translationY * clampedReduction);
        double modifiedX = square(translationX * clampedReduction);
        double modifiedRotation = square(rotation * clampedReduction);

        follower.setTeleOpDrive(modifiedX, modifiedY, modifiedRotation, false);
    }

    /**
     * Drive the robot robot centric manner. This method is useful for autonomous control.
     * @param translationX robot strafe along the X axis in range [-1, 1]
     * @param translationY robot speed along the Y axis in range [-1, 1]
     * @param rotation robot rotation speed in range of [-1, 1]
     */
    public void driveRobotCentric(double translationX, double translationY, double rotation) {
        follower.setTeleOpDrive(translationX, translationY, rotation, true);
    }

    public void startTeleop() {
        follower.startTeleopDrive();
        follower.setMaxPower(1);
    }

    /**
     * Stops the drivetrain.
     */
    public void stop() {
        follower.setTeleOpDrive(0.0, 0.0, 0.0, true);
    }

    /**
     * Returns a new PedroPath PathBuilder.
     * @return new path builder
     */
    public PathBuilder pathBuilder() {
        return follower.pathBuilder();
    }

    /**
     * Resets localization to the origin.
     */
    public void resetLocalization() {
        Pose resetPose = new Pose();
        follower.setStartingPose(resetPose);
        follower.setPose(resetPose);
    }

    @Override
    public void periodic() {
        // Because this calls the OTOS, we can assume it is a blocking call that can take tens of
        // milliseconds, so only do it once per period
        follower.update();
        currentPose = follower.getPose();
    }

    public Follower getFollower() {
        return follower;
    }

    /**
     * Adds drivetrain telemetry data.
     * @param telemetry telemetry object
     */
    public void telemetrize(Telemetry telemetry) {
        // Log the position to the telemetry
        telemetry.addData("X coordinate (meters)", currentPose.getX());
        telemetry.addData("Y coordinate (meters)", currentPose.getY());
        telemetry.addData("Heading angle (radians)", currentPose.getHeading());
    }

    public static double square(double value) {
        return Math.copySign(value * value, value);
    }

}