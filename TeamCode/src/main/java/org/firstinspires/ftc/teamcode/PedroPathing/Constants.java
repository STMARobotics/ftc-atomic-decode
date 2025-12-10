package org.firstinspires.ftc.teamcode.PedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.OTOSConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {

    private static final String FRONT_LEFT_MOTOR_NAME = "left1";
    private static final String BACK_LEFT_MOTOR_NAME = "left2";
    private static final String FRONT_RIGHT_MOTOR_NAME = "right1";
    private static final String BACK_RIGHT_MOTOR_NAME = "right2";
    private static final String OTOS_NAME = "sensor_otos";

    public static final OTOSConstants OTOS_CONSTANTS = new OTOSConstants()
            .hardwareMapName(OTOS_NAME)
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            .linearScalar(0.9983021436711816)
            .angularScalar(0.9938358046794901)
            .offset(new SparkFunOTOS.Pose2D(
                    47 / 25.4, // +x forward
                    47 / 25.4, // +y
                    Math.PI));

    public static final FollowerConstants FOLLOWER_CONSTANTS = new FollowerConstants()
            .mass(13.1541787) // must be kg
            .forwardZeroPowerAcceleration(-58.742)
            .lateralZeroPowerAcceleration(-82.934)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0, 0.0, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(0.8, 0, 0.0, 0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.2, 0, 0.01, 0, 0.02))
            .forwardZeroPowerAcceleration(-32.08424777795885)
            .lateralZeroPowerAcceleration(-49.85446442889164);

    public static final MecanumConstants DRIVE_CONSTANTS = new MecanumConstants()
            .maxPower(1)
            .xVelocity(65.413)
            .yVelocity(52.851)
            .rightFrontMotorName(FRONT_RIGHT_MOTOR_NAME)
            .rightRearMotorName(BACK_RIGHT_MOTOR_NAME)
            .leftRearMotorName(BACK_LEFT_MOTOR_NAME)
            .leftFrontMotorName(FRONT_LEFT_MOTOR_NAME)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .useBrakeModeInTeleOp(true)
            .xVelocity(58.29998827356053)
            .yVelocity(43.76028466412402)
            .useVoltageCompensation(true);

    public static final PathConstraints PATH_CONSTRAINTS =
            new PathConstraints(0.99, 100, 1, 1);

    /**
     * Creates a PedroPathing follower. There should probably only be one follower instance per
     * OpMode.
     * @param hardwareMap hardware map
     * @return new PedroPathing follower
     */
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(FOLLOWER_CONSTANTS, hardwareMap)
                .pathConstraints(PATH_CONSTRAINTS)
                .OTOSLocalizer(OTOS_CONSTANTS)
                .mecanumDrivetrain(DRIVE_CONSTANTS)
                .build();
    }
}