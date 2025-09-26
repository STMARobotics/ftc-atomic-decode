package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class AutoDriveSubsystem {

    private final DcMotor left1;
    private final DcMotor left2;
    private final DcMotor right1;
    private final DcMotor right2;
    private IMU imu;

    // Constants - PLEASE VERIFY AND ADJUST THESE FOR YOUR ROBOT
    public static final double COUNTS_PER_MOTOR_REV = 537.6; // For GoBILDA 5203 series, 312 RPM (19.2:1 ratio: 28 * 19.2)
    public static double WHEEL_DIAMETER_INCHES = 4.09449;  // <<< IMPORTANT: Adjust this to your actual wheel diameter
    public static double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    private LinearOpMode opMode; // Optional: for opModeIsActive() and telemetry

    /**
     * Constructor for the auto drivetrain subsystem.
     * @param hardwareMap The hardware map from your OpMode.
     */
    public AutoDriveSubsystem(HardwareMap hardwareMap) {
        left1 = hardwareMap.get(DcMotorEx.class, "left1");
        right1 = hardwareMap.get(DcMotorEx.class, "right1");
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");

        imu = hardwareMap.get(IMU.class, "imu");
        // IMPORTANT: Adjust the RevHubOrientationOnRobot parameters to match your robot's configuration.
        // This is a common starting point (Logo UP, USB FORWARD), but you MUST VERIFY AND ADJUST.
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
            RevHubOrientationOnRobot.LogoFacingDirection.UP,
            RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        ));
        imu.initialize(parameters);
        // Optional: Reset yaw after initialization if you always want to start with a 0 heading
        // imu.resetYaw();

        // Set motor directions - IMPORTANT: Adjust these to match your robot's configuration.
        left1.setDirection(DcMotorSimple.Direction.FORWARD);
        left2.setDirection(DcMotorSimple.Direction.FORWARD);
        right1.setDirection(DcMotorSimple.Direction.REVERSE);
        right2.setDirection(DcMotorSimple.Direction.REVERSE);

        resetEncoders();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set zero power behavior to BRAKE
        left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Constructor that also takes a LinearOpMode reference for telemetry and opModeIsActive checks.
     * @param hardwareMap The hardware map from your OpMode.
     * @param opMode The instance of the active LinearOpMode.
     */
    public AutoDriveSubsystem(HardwareMap hardwareMap, LinearOpMode opMode) {
        this(hardwareMap); // Call the main constructor
        this.opMode = opMode;
    }

    /**
     * Drives the robot forward or backward for a specific distance in inches (robot-centric).
     * For field-centric driving, this method needs to be modified or new methods created.
     * @param inches The distance to drive in inches. Positive for forward, negative for backward.
     * @param power The power to apply to the motors (0.0 to 1.0).
     */
    public void driveInches(double inches, double power) {
        if (opMode != null && !opMode.opModeIsActive()) return;

        COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
        int targetTicks = (int) (inches * COUNTS_PER_INCH);

        left1.setTargetPosition(left1.getCurrentPosition() + targetTicks);
        right1.setTargetPosition(right1.getCurrentPosition() + targetTicks);
        left2.setTargetPosition(left2.getCurrentPosition() + targetTicks);
        right2.setTargetPosition(right2.getCurrentPosition() + targetTicks);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        setMotorPower(Math.abs(power));

        while ((opMode == null || opMode.opModeIsActive()) &&
               (left1.isBusy() || right1.isBusy() || left2.isBusy() || right2.isBusy())) {
            if (opMode != null) {
                opMode.telemetry.addData("Drivetrain", "Driving to %d inches", (int)inches);
                opMode.telemetry.addData("FL", "Target: %d, Current: %d", left1.getTargetPosition(), left1.getCurrentPosition());
                opMode.telemetry.addData("FR", "Target: %d, Current: %d", right1.getTargetPosition(), right1.getCurrentPosition());
                // Add IMU heading to telemetry for debugging
                // opMode.telemetry.addData("Heading", "%.2f degrees", getYaw());
                opMode.telemetry.update();
                opMode.idle();
            } else {
                Thread.yield();
            }
        }

        stopMotors();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Gets the current robot heading in degrees from the IMU.
     * @return The robot's current yaw (heading) in degrees.
     */
    public double getYaw() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /**
     * Resets the IMU's yaw to zero.
     */
    public void resetIMUYaw() {
        imu.resetYaw();
    }


    public void setMotorMode(DcMotor.RunMode mode) {
        left1.setMode(mode);
        right1.setMode(mode);
        left2.setMode(mode);
        right2.setMode(mode);
    }

    public void setMotorPower(double power) {
        left1.setPower(power);
        right1.setPower(power);
        left2.setPower(power);
        right2.setPower(power);
    }

    public void stopMotors() {
        setMotorPower(0);
    }

    public void resetEncoders() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public static void setWheelDiameterInches(double diameterInches) {
        WHEEL_DIAMETER_INCHES = diameterInches;
        COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
    }
    
    // TODO: Add methods for turning (e.g., turnToAngle), field-centric strafing.
}
