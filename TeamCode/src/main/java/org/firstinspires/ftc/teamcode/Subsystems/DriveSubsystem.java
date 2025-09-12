package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Main.Robot;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.rowanmcalpin.nextftc.core.Subsystem;

public class DriveSubsystem extends Subsystem {

    private final DcMotorEx left1;
    private final DcMotorEx left2;
    private final DcMotorEx right1;
    private final DcMotorEx right2;
    private final IMU imu;
    private double headingOffset = 0.0;

    public DriveSubsystem(Robot hardwareMap) {
        left1 = hardwareMap.hardwareMap.get(DcMotorEx.class, "left1");
        left2 = hardwareMap.hardwareMap.get(DcMotorEx.class, "left2");
        right1 = hardwareMap.hardwareMap.get(DcMotorEx.class, "right1");
        right2 = hardwareMap.hardwareMap.get(DcMotorEx.class, "right2");

        imu = hardwareMap.hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.DOWN;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        resetIMU();

        left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left1.setDirection(DcMotor.Direction.REVERSE);
        left2.setDirection(DcMotor.Direction.REVERSE);
        right1.setDirection(DcMotor.Direction.FORWARD);
        right2.setDirection(DcMotor.Direction.FORWARD);

        left1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        left2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Drives the robot field-centrically.
     * @param y The desired forward/backward speed on the field (usually from left stick Y).
     * @param x The desired strafing speed on the field (usually from left stick X).
     * @param rx The desired rotational speed of the robot (usually from right stick X).
     * @param speedMultiplier The multiplier for the robot's translational speed. Defaults to 1.0 if null. Turning speed is not affected.
     */
    public void drive(double y, double x, double rx, Double speedMultiplier) {
        double actualSpeedMultiplier = (speedMultiplier == null) ? 1.0 : speedMultiplier;

        double rawHeadingDeg = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        double adjustedHeadingRad = Math.toRadians(rawHeadingDeg - headingOffset);
        
        double rotatedXInput = x * Math.cos(adjustedHeadingRad) - y * Math.sin(adjustedHeadingRad);
        double rotatedYInput = x * Math.sin(adjustedHeadingRad) + y * Math.cos(adjustedHeadingRad);

        double driveY = rotatedYInput * actualSpeedMultiplier;
        double driveX = rotatedXInput * actualSpeedMultiplier;

        double denominator = Math.max(Math.abs(driveY) + Math.abs(driveX) + Math.abs(rx), 1.0);

        double left1Power  = (driveY + driveX + rx) / denominator;
        double left2Power   = (driveY - driveX + rx) / denominator;
        double right1Power = (driveY - driveX - rx) / denominator;
        double right2Power  = (driveY + driveX - rx) / denominator;


        left1.setPower(left1Power);
        left2.setPower(left2Power);
        right1.setPower(right1Power);
        right2.setPower(right2Power);
    }

    /**
     * Resets the IMU's yaw angle to make the current orientation the new zero.
     */
    public void resetIMU() {
        headingOffset = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        imu.resetYaw();
    }

    /**
     * Gets the robot's current heading in degrees, adjusted by the last reset.
     * @return The current heading in degrees
     */
    public double getHeading() {
        double currentYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        double adjustedYaw = currentYaw - headingOffset;
        while (adjustedYaw > 180.0) {
            adjustedYaw -= 360.0;
        }
        while (adjustedYaw <= -180.0) {
            adjustedYaw += 360.0;
        }
        return adjustedYaw;
    }

    /**
     * Stops the robot.
     */
    public void stop() {
        left1.setPower(0);
        left2.setPower(0);
        right1.setPower(0);
        right2.setPower(0);
    }

    /**
     * Snaps the robot to a target heading.
     * This is just a placeholder and still needs some work but we only need it to drive for now.
     * @param targetHeadingDegrees The desired heading in degrees.
     * @param turnPowerMagnitude The maximum power to apply for turning.
     */
    public void snapToHeading(double targetHeadingDegrees, double turnPowerMagnitude) {
        // Placeholder for P-controller or other turning logic
        // Example P-controller logic:
    }
}
