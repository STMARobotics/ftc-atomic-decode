package org.firstinspires.ftc.teamcode.Subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

/**
 * Drivetrain subsystem. Encapsulates the details of how the drivetrain works.
 */
public class DrivetrainSubsystem extends SubsystemBase {

    private final Follower follower;
    private Pose currentPose = new Pose();

    private final PIDController xController = new PIDController(0.07, 0.0, 0.0);
    private final PIDController yController = new PIDController(0.07, 0.0, 0.0);
    private final PIDController headingController = new PIDController(0.2, 0.0, 0.0);

    private final double headingDeadzoneRad = Math.toRadians(2.0);

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
        double modifiedY = square(translationY) * clampedReduction;
        double modifiedX = square(translationX) * clampedReduction;
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
        telemetry.addData("X coordinate (mm)", currentPose.getX());
        telemetry.addData("Y coordinate (mm)", currentPose.getY());
        telemetry.addData("Heading angle (degrees)", Math.toDegrees(currentPose.getHeading()));
    }

    public static double square(double value) {
        return Math.copySign(value * value, value);
    }

    /**
     * Returns the current estimated pose of the robot.
     * @return current pose
     */
    public Pose getCurrentPose() {
        return currentPose;
    }

    /**
     * Holds the robot at a specific position using probably pid or something i guess
     * @param targetPose the target position to hold, use Pose2d targetPosition = new Pose2d(currentPose); to get correct format
     */
    public void holdPosition(Pose targetPose) {
        Pose cur = getCurrentPose();

        double xError = targetPose.getX() - cur.getX();
        double yError = targetPose.getY() - cur.getY();
        double headingError = angleWrap(targetPose.getHeading() - cur.getHeading());

        double distanceError = Math.hypot(xError, yError);

        double positionDeadzoneMm = 10.0;
        if (distanceError <= positionDeadzoneMm && Math.abs(headingError) <= headingDeadzoneRad) {
            follower.setTeleOpDrive(0.0, 0.0, 0.0, false);
            return;
        }

        double xPower = xController.calculate(cur.getX(), targetPose.getX());
        double yPower = yController.calculate(cur.getY(), targetPose.getY());
        double turnPower = headingController.calculate(cur.getHeading(), targetPose.getHeading());

        follower.setTeleOpDrive(xPower, yPower, turnPower, false);
    }

    /**
     * angleWrap is broken so were bringing our own
     * @param angle angle in radians
     * @return wrapped angle in radians between -PI and PI
     */
    public static double angleWrap(double angle) {
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

}