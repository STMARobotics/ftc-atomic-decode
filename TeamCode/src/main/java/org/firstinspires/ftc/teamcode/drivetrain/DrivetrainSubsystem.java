package org.firstinspires.ftc.teamcode.drivetrain;

import static org.firstinspires.ftc.teamcode.pedroPathing.Constants.DRIVE_CONSTANTS;
import static org.firstinspires.ftc.teamcode.pedroPathing.Constants.FOLLOWER_CONSTANTS;
import static org.firstinspires.ftc.teamcode.pedroPathing.Constants.OTOS_CONSTANTS;
import static org.firstinspires.ftc.teamcode.pedroPathing.Constants.PATH_CONSTRAINTS;

import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FollowerBuilder;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Drivetrain subsystem. Encapsulates the details of <i>how</i> the drivetrain works.
 */
public class DrivetrainSubsystem extends SubsystemBase {

    private final HardwareMap hardwareMap;

    // Motors
    private final DcMotor frontLeftMotor;
    private final DcMotor backLeftMotor;
    private final DcMotor frontRightMotor;
    private final DcMotor backRightMotor;

    // OTOS sensor
    private final SparkFunOTOS otosSensor;

    private SparkFunOTOS.Pose2D currentPose = new SparkFunOTOS.Pose2D();

    public DrivetrainSubsystem(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        frontLeftMotor = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.leftFrontMotorName);
        backLeftMotor = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.leftRearMotorName);
        frontRightMotor = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.rightFrontMotorName);
        backRightMotor = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.rightRearMotorName);

        frontLeftMotor.setDirection(DRIVE_CONSTANTS.leftFrontMotorDirection);
        backLeftMotor.setDirection(DRIVE_CONSTANTS.leftRearMotorDirection);
        frontRightMotor.setDirection(DRIVE_CONSTANTS.rightFrontMotorDirection);
        backRightMotor.setDirection(DRIVE_CONSTANTS.rightRearMotorDirection);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set up the OTOS sensor, using the same constants we use to configure it with PedroPathing
        otosSensor = hardwareMap.get(SparkFunOTOS.class, OTOS_CONSTANTS.hardwareMapName);
        otosSensor.setLinearUnit(OTOS_CONSTANTS.linearUnit);
        otosSensor.setAngularUnit(OTOS_CONSTANTS.angleUnit);
        otosSensor.setOffset(OTOS_CONSTANTS.offset);
        otosSensor.setLinearScalar(OTOS_CONSTANTS.linearScalar);
        otosSensor.setAngularScalar(OTOS_CONSTANTS.angularScalar);
        otosSensor.calibrateImu();
        otosSensor.resetTracking();

        // Get the hardware and firmware version
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        otosSensor.getVersionInfo(hwVersion, fwVersion);
        resetLocalization();
    }

    /**
     * Drive the robot on field centric manner
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

        // Rotate the heading based on the robot's heading on the field
        double botHeading = currentPose.h;
        double rotX = modifiedX * Math.sin(botHeading) - modifiedY * Math.cos(botHeading);
        double rotY = modifiedX * Math.cos(botHeading) + modifiedY * Math.sin(botHeading);

        // Calculate the output for each wheel
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(modifiedRotation), 1);
        double frontLeftPower = (rotY + rotX - modifiedRotation) / denominator;
        double backLeftPower = (rotY - rotX - modifiedRotation) / denominator;
        double frontRightPower = (rotY - rotX + modifiedRotation) / denominator;
        double backRightPower = (rotY + rotX + modifiedRotation) / denominator;

        // Apply the output to the motors
        frontLeftMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(backLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backRightMotor.setPower(backRightPower);
    }

    public void stop() {
        frontLeftMotor.setPower(0.0);
        backLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);
    }

    /**
     * Creates a PedroPath Follower.
     * @return new follower
     */
    public Follower createFollower() {
        return new FollowerBuilder(FOLLOWER_CONSTANTS, hardwareMap)
                .pathConstraints(PATH_CONSTRAINTS)
                .OTOSLocalizer(OTOS_CONSTANTS)
                .mecanumDrivetrain(DRIVE_CONSTANTS)
                .build();
    }

    public void resetLocalization() {
        otosSensor.setPosition(new SparkFunOTOS.Pose2D(0, 0, 0));
    }

    @Override
    public void periodic() {
        // This is a blocking call that can take tens of milliseconds, so only do it once per period
        currentPose = otosSensor.getPosition();
    }

    /**
     * Adds drivetrain telemetry data.
     * @param telemetry telemetry object
     */
    public void telemetrize(Telemetry telemetry) {
        // Log the position to the telemetry
        telemetry.addData("X coordinate (meters)", currentPose.x);
        telemetry.addData("Y coordinate (meters)", currentPose.y);
        telemetry.addData("Heading angle (radians)", currentPose.h);
    }

    public static double square(double value) {
        return Math.copySign(value * value, value);
    }

}
