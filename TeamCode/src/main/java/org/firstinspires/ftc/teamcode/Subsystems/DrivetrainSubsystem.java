package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.pedroPathing.Constants.DRIVE_CONSTANTS;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Drivetrain subsystem. Encapsulates the details of <i>how</i> the drivetrain works.
 */
public class DrivetrainSubsystem extends SubsystemBase {

    // Motors
    private final DcMotor left1;
    private final DcMotor left2;
    private final DcMotor right1;
    private final DcMotor right2;
    private final IMU imu;

    public DrivetrainSubsystem(HardwareMap hardwareMap) {

        left1 = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.leftFrontMotorName);
        left2 = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.leftRearMotorName);
        right1 = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.rightFrontMotorName);
        right2 = hardwareMap.get(DcMotor.class, DRIVE_CONSTANTS.rightRearMotorName);

        left1.setDirection(DRIVE_CONSTANTS.leftFrontMotorDirection);
        left2.setDirection(DRIVE_CONSTANTS.leftRearMotorDirection);
        right1.setDirection(DRIVE_CONSTANTS.rightFrontMotorDirection);
        right2.setDirection(DRIVE_CONSTANTS.rightRearMotorDirection);

        left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        );

        IMU.Parameters imuParams = new IMU.Parameters(orientationOnRobot);
        imu.initialize(imuParams);
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
        double clampedReduction = MathUtils.clamp(reductionFactor, 0.0, 1);

        // Square and reduce the axes
        double modifiedY = square(translationY * clampedReduction);
        double modifiedX = square(translationX * clampedReduction);
        double modifiedRotation = square(rotation * clampedReduction);

        // Rotate the heading based on the robot's heading on the field
        double botHeading = getHeading();
        double rotX = modifiedX * Math.sin(botHeading) - modifiedY * Math.cos(botHeading);
        double rotY = modifiedX * Math.cos(botHeading) + modifiedY * Math.sin(botHeading);

        // Calculate the output for each wheel
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(modifiedRotation), 1);
        double frontLeftPower = (rotY + rotX - modifiedRotation) / denominator;
        double backLeftPower = (rotY - rotX - modifiedRotation) / denominator;
        double frontRightPower = (rotY - rotX + modifiedRotation) / denominator;
        double backRightPower = (rotY + rotX + modifiedRotation) / denominator;

        // Apply the output to the motors
        left1.setPower(frontLeftPower);
        left2.setPower(backLeftPower);
        right1.setPower(frontRightPower);
        right2.setPower(backRightPower);
    }

    public void stop() {
        left1.setPower(0.0);
        left2.setPower(0.0);
        right1.setPower(0.0);
        right2.setPower(0.0);
    }

    /**
     * Adds drivetrain telemetry data.
     * @param telemetry telemetry object
     */
    public void telemetrize(Telemetry telemetry) {
    }

    public static double square(double value) {
        return Math.copySign(value * value, value);
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void resetLocalization() {
        imu.resetYaw();
    }
}
