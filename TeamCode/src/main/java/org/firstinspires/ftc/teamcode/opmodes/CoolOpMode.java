package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.drivetrain.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.drivetrain.FollowPathCommand;
import org.firstinspires.ftc.teamcode.led.LedSubsystem;
import org.firstinspires.ftc.teamcode.vision.AprilTagSubsystem;
import org.firstinspires.ftc.teamcode.vision.MotifIndicatorCommand;

// I copied andy so merek can sleep at night

@TeleOp(name="Cool op mode from andy", group = "cool things")
public class CoolOpMode extends CommandOpMode {

    private DrivetrainSubsystem drivetrainSubsystem;
    private LedSubsystem ledSubsystem;
    private AprilTagSubsystem aprilTagSubsystem;

    @Override
    public void initialize() {
        // Create subsystems
        drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);
        ledSubsystem = new LedSubsystem(hardwareMap);
        aprilTagSubsystem = new AprilTagSubsystem(hardwareMap);

        /*
        The origin is the field perimeter corner by the red loading zone.
        We'll drive from the perspective of the red alliance:
         - Pushing up on the left stick moves toward the blue alliance wall, which is positive X.
         - Pushing to the left on the left stick moves toward the obelisk wall, which is positive Y.
         - Pushing to the left on the right stick rotates the the positive direction,
         counterclockwise
         */
        RunCommand teleopDriveCommand = new RunCommand(() -> drivetrainSubsystem.drive(
                -gamepad1.left_stick_y, // Stick up is negative but moves +X, so invert
                -gamepad1.left_stick_x, // Stick left is negative but moves +Y, so invert
                -gamepad1.right_stick_x, // Stick left is negative but moves +rotation, so invert
                1),
                drivetrainSubsystem);

        RunCommand telemetryCommand = new RunCommand(() -> {
            drivetrainSubsystem.telemetrize(telemetry);
            telemetry.update();
        });

        // Schedule commands
        schedule(telemetryCommand);

        // Register subsystems
        register(drivetrainSubsystem);

        // Set default commands for subsystems
        drivetrainSubsystem.setDefaultCommand(teleopDriveCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // Test PedroPathing command
        Follower follower = drivetrainSubsystem.createFollower();
        Pose pose1 = new Pose(0,0, 0);
        Pose pose2 = new Pose(12, 0, 90);
        Pose pose3 = new Pose(12, 12, 90);
        PathChain path = follower.pathBuilder()
                .addPath(new BezierLine(pose1, pose2))
                .setLinearHeadingInterpolation(pose1.getHeading(), pose2.getHeading())
                .addPath(new BezierLine(pose2, pose3))
                .setLinearHeadingInterpolation(pose2.getHeading(), pose3.getHeading())
                .build();
        FollowPathCommand followPathCommand =
                new FollowPathCommand(pose1, follower, path, drivetrainSubsystem);

        // Motif AprilTag
        MotifIndicatorCommand motifIndicatorCommand =
                new MotifIndicatorCommand(ledSubsystem, aprilTagSubsystem, telemetry);

        // Bind driver buttons
        GamepadEx gamepad = new GamepadEx(gamepad1);
        gamepad.getGamepadButton(GamepadKeys.Button.B).toggleWhenPressed(motifIndicatorCommand);
        gamepad.getGamepadButton(GamepadKeys.Button.A).whileHeld(followPathCommand);
        gamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);
    }

}
