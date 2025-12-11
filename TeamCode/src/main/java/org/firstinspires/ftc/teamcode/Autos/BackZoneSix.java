package org.firstinspires.ftc.teamcode.Autos;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.ParallelDeadlineGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.Commands.AutoIntakeCommand;
import org.firstinspires.ftc.teamcode.Commands.AutoLockTurretCommand;
import org.firstinspires.ftc.teamcode.Commands.AutoShootCommand;
import org.firstinspires.ftc.teamcode.Commands.FollowPathCommand;
import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Commands.ReeceMagic;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

@Autonomous(name = "BackZoneSix", group = "Autos")
public class BackZoneSix extends CommandOpMode {

    @Override
    public void initialize() {

        PlatterSubsystem platterSubsystem = new PlatterSubsystem(hardwareMap);
        ShooterSubsystem shooterSubsystem = new ShooterSubsystem(hardwareMap);
        TurretSubsystem turretSubsystem = new TurretSubsystem(hardwareMap);
        LimelightSubsystem limelightSubsystem = new LimelightSubsystem(hardwareMap);
        IntakeSubsystem intakeSubsystem = new IntakeSubsystem(hardwareMap);
        DrivetrainSubsystem drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);
        register(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem, intakeSubsystem, drivetrainSubsystem);

        Follower follower = drivetrainSubsystem.getFollower();

        Pose StartPath1 = new Pose(56, 8.1, Math.toRadians(180.0));
        PathChain path1 =
                follower.pathBuilder()
                        .addPath(new BezierLine(new Pose(56, 8.1, Math.toRadians(180.0)), new Pose(15, 8.1, Math.toRadians(180.0))))
                        .setLinearHeadingInterpolation(Math.toRadians(180.0), Math.toRadians(180.0))
                        .build();

        Pose StartPath2 = new Pose(15, 8.1, Math.toRadians(180.0));
        PathChain path2 =
                follower.pathBuilder()
                        .addPath(new BezierLine(new Pose(15, 8.1, Math.toRadians(180.0)), new Pose(56, 8.1, Math.toRadians(180.0))))
                        .setLinearHeadingInterpolation(Math.toRadians(180.0), Math.toRadians(180.0))
                        .build();

        follower.setStartingPose(StartPath1);
        follower.setPose(StartPath1);

        // Schedule the full sequence now but start only after play via the WaitForStartCommand
        schedule(
                new SequentialCommandGroup(
                        new WaitForStartCommand(),
                        new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem),
                        new FollowPathCommand(path1, 0.5, drivetrainSubsystem, follower),
                        new ParallelDeadlineGroup(
                                new AutoIntakeCommand(platterSubsystem, intakeSubsystem),
                                new ReeceMagic(drivetrainSubsystem, follower)
                        ),
                        new FollowPathCommand(path2, 0.5, drivetrainSubsystem, follower),
                        new ParallelDeadlineGroup(
                                new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem),
                                new ReeceMagic(drivetrainSubsystem, follower)
                        )
                )
        );
    }

    private class WaitForStartCommand extends CommandBase {
        @Override
        public boolean isFinished() {
            return BackZoneSix.this.isStarted();
        }
    }
}
