package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutoDriveSubsystem {

    // Assuming: left1 = Front Left, left2 = Rear Left, right1 = Front Right, right2 = Rear Right
    private final DcMotorEx left1;
    private final DcMotorEx left2;
    private final DcMotorEx right1;
    private final DcMotorEx right2;
    private IMU imu;

    // Constants - PLEASE VERIFY AND ADJUST THESE FOR YOUR ROBOT
    public static final double COUNTS_PER_MOTOR_REV = 537.6; // For GoBILDA 5203 series, 312 RPM (19.2:1 ratio: 28 * 19.2)
    public static double WHEEL_DIAMETER_INCHES = 4.09449;  // <<< IMPORTANT: Adjust this to your actual wheel diameter
    public static double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    // PID Turn Constants - ***YOU MUST TUNE THESE***
    public static double KP_TURN = 0.01; // Proportional gain
    public static double KI_TURN = 0.0001; // Integral gain
    public static double KD_TURN = 0.001; // Derivative gain
    public static double TURN_TOLERANCE_DEGREES = 1.0; // Allowed error in degrees
    private ElapsedTime pidTimer = new ElapsedTime();
    private double integralSum = 0;
    private double lastError = 0;

    private LinearOpMode opMode; // Optional: for opModeIsActive() and telemetry

    public AutoDriveSubsystem(HardwareMap hardwareMap) {
        left1 = hardwareMap.get(DcMotorEx.class, "left1");
        right1 = hardwareMap.get(DcMotorEx.class, "right1");
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");

        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
            RevHubOrientationOnRobot.LogoFacingDirection.UP,
            RevHubOrientationOnRobot.UsbFacingDirection.LEFT // TODO: Verify this matches your Hub orientation
        ));
        imu.initialize(parameters);

        left1.setDirection(DcMotorSimple.Direction.FORWARD);
        left2.setDirection(DcMotorSimple.Direction.FORWARD);
        right1.setDirection(DcMotorSimple.Direction.REVERSE);
        right2.setDirection(DcMotorSimple.Direction.REVERSE);

        resetEncoders();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public AutoDriveSubsystem(HardwareMap hardwareMap, LinearOpMode opMode) {
        this(hardwareMap);
        this.opMode = opMode;
    }

    public void driveInches(double inches, double power) {
        if (opMode != null && !opMode.opModeIsActive()) return;
        COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
        int targetTicks = (int) (inches * COUNTS_PER_INCH);

        left1.setTargetPosition(left1.getCurrentPosition() + targetTicks);
        right1.setTargetPosition(right1.getCurrentPosition() + targetTicks);
        left2.setTargetPosition(left2.getCurrentPosition() + targetTicks);
        right2.setTargetPosition(right2.getCurrentPosition() + targetTicks);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        setAllMotorPower(Math.abs(power));

        while ((opMode == null || opMode.opModeIsActive()) && isAnyMotorBusy()) {
            if (opMode != null) {
                opMode.telemetry.addData("Drive", "Target: %.1f in, Power: %.2f", inches, power);
                opMode.telemetry.addData("FL", "T: %d, C: %d", left1.getTargetPosition(), left1.getCurrentPosition());
                opMode.telemetry.addData("FR", "T: %d, C: %d", right1.getTargetPosition(), right1.getCurrentPosition());
                opMode.telemetry.update();
                opMode.idle();
            } else { Thread.yield(); }
        }
        stopMotors();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void strafeInches(double inches, double power) {
        if (opMode != null && !opMode.opModeIsActive()) return;
        COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
        
        // Positive inches = Strafe Right, Negative inches = Strafe Left
        int targetTicks = (int) (Math.abs(inches) * COUNTS_PER_INCH);
        int strafeSign = (int) Math.signum(inches);

        // Mecanum strafing pattern (assuming left1=FL, right1=FR, left2=RL, right2=RR)
        // For Strafe Right (strafeSign = 1):
        // FL: +targetTicks, FR: -targetTicks, RL: -targetTicks, RR: +targetTicks
        left1.setTargetPosition(left1.getCurrentPosition() + strafeSign * targetTicks);
        right1.setTargetPosition(right1.getCurrentPosition() - strafeSign * targetTicks);
        left2.setTargetPosition(left2.getCurrentPosition() - strafeSign * targetTicks); // Corrected from previous thought: RL is also negative for right strafe
        right2.setTargetPosition(right2.getCurrentPosition() + strafeSign * targetTicks);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        setAllMotorPower(Math.abs(power));

        while ((opMode == null || opMode.opModeIsActive()) && isAnyMotorBusy()) {
            if (opMode != null) {
                opMode.telemetry.addData("Strafe", "Target: %.1f in, Power: %.2f", inches, power);
                opMode.telemetry.addData("FL", "T: %d, C: %d", left1.getTargetPosition(), left1.getCurrentPosition());
                opMode.telemetry.addData("RR", "T: %d, C: %d", right2.getTargetPosition(), right2.getCurrentPosition());
                opMode.telemetry.update();
                opMode.idle();
            } else { Thread.yield(); }
        }
        stopMotors();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void turnToAnglePID(double targetAngle, double maxTurnSpeed) {
        if (opMode != null && !opMode.opModeIsActive()) return;

        resetPIDState();
        pidTimer.reset();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER); // Use RUN_USING_ENCODER or RUN_WITHOUT_ENCODER

        double currentAngle = getYaw();
        double error = AngleUnit.normalizeDegrees(targetAngle - currentAngle);

        while ((opMode == null || opMode.opModeIsActive()) && Math.abs(error) > TURN_TOLERANCE_DEGREES) {
            double dt = pidTimer.seconds();
            pidTimer.reset();
            if (dt == 0) dt = 0.00001; // Avoid division by zero if loop is too fast

            double derivative = (error - lastError) / dt;
            
            // Basic anti-windup: only accumulate integral if output is not saturated 
            // or if the integral term would reduce the saturation.
            double potentialTurnPower = (KP_TURN * error) + (KI_TURN * (integralSum + error * dt)) + (KD_TURN * derivative);
            if (Math.abs(potentialTurnPower) < maxTurnSpeed || Math.signum(potentialTurnPower) != Math.signum(integralSum + error * dt) ) {
                 integralSum += error * dt;
            }

            lastError = error;

            double turnPower = (KP_TURN * error) + (KI_TURN * integralSum) + (KD_TURN * derivative);
            turnPower = Range.clip(turnPower, -maxTurnSpeed, maxTurnSpeed);

            // Apply power to motors for turning (Left +, Right - for clockwise with given motor directions)
            left1.setPower(turnPower);
            left2.setPower(turnPower);
            right1.setPower(-turnPower);
            right2.setPower(-turnPower);

            currentAngle = getYaw();
            error = AngleUnit.normalizeDegrees(targetAngle - currentAngle);

            if (opMode != null) {
                opMode.telemetry.addData("TurnPID", "Target: %.1f, Current: %.1f", targetAngle, currentAngle);
                opMode.telemetry.addData("Error", "%.1f, Power: %.2f", error, turnPower);
                opMode.telemetry.addData("PID Terms", "P:%.3f, I:%.3f, D:%.3f", KP_TURN*error, KI_TURN*integralSum, KD_TURN*derivative);
                opMode.telemetry.update();
                opMode.idle();
            } else { Thread.yield(); }
        }
        stopMotors();
    }

    private void resetPIDState(){
        integralSum = 0;
        lastError = 0;
        pidTimer.reset();
    }

    public double getYaw() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void resetIMUYaw() {
        imu.resetYaw();
        resetPIDState(); // Also reset PID state when IMU yaw is reset
    }

    public void setMotorMode(DcMotor.RunMode mode) {
        left1.setMode(mode);
        right1.setMode(mode);
        left2.setMode(mode);
        right2.setMode(mode);
    }

    public void setAllMotorPower(double power) {
        left1.setPower(power);
        right1.setPower(power);
        left2.setPower(power);
        right2.setPower(power);
    }

    public void stopMotors() {
        setAllMotorPower(0);
    }

    public void resetEncoders() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    
    public boolean isAnyMotorBusy(){
        return left1.isBusy() || right1.isBusy() || left2.isBusy() || right2.isBusy();
    }

    public static void setWheelDiameterInches(double diameterInches) {
        WHEEL_DIAMETER_INCHES = diameterInches;
        COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
    }
    
    // TODO: Consider adding field-centric drive/strafe methods that use the IMU heading.
    // TODO: Thoroughly test and tune PID constants for turnToAnglePID.
}
