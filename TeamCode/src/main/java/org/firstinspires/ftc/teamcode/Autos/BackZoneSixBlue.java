package org.firstinspires.ftc.teamcode.Autos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.ParallelDeadlineGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.Commands.AutoIntakeCommand;
import org.firstinspires.ftc.teamcode.Commands.AutoShootCommand;
import org.firstinspires.ftc.teamcode.Commands.FollowPathCommand;
import org.firstinspires.ftc.teamcode.Commands.ReeceMagic;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

@Autonomous(name = "BackZoneSixBlue", group = "Autos")
public class BackZoneSixBlue extends CommandOpMode {

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

        Pose startPose1 = new Pose(56, 8.1, Math.toRadians(180.0));
        PathChain path1 =
                follower.pathBuilder()
                        .addPath(new BezierLine(startPose1, new Pose(15, 8.1, Math.toRadians(180.0))))
                        .setLinearHeadingInterpolation(Math.toRadians(180.0), Math.toRadians(180.0))
                        .build();

        Pose startPose2 = new Pose(15, 8.1, Math.toRadians(180.0));
        PathChain path2 =
                follower.pathBuilder()
                        .addPath(new BezierLine(startPose2, new Pose(51, 8.1, Math.toRadians(180.0))))
                        .setLinearHeadingInterpolation(Math.toRadians(180.0), Math.toRadians(180.0))
                        .build();

        Pose startPose3 = new Pose(51, 8.1, Math.toRadians(180));
        PathChain path3 =
                follower.pathBuilder()
                                .addPath(new BezierLine(startPose3, new Pose(51, 30, Math.toRadians(180.0))))
                                .setLinearHeadingInterpolation(Math.toRadians(180.0), Math.toRadians(180.0))
                                .build();

        follower.setStartingPose(startPose1);
        follower.setPose(startPose1);

        schedule(
                new SequentialCommandGroup(
                        new WaitForStartCommand(),
                        new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem, 2.0),
                        new ParallelDeadlineGroup(
                                new AutoIntakeCommand(platterSubsystem, intakeSubsystem).withTimeout(10000),
                                new FollowPathCommand(path1, drivetrainSubsystem, follower, true, 0.7)
                        ),
                        new FollowPathCommand(path2, drivetrainSubsystem, follower, false, 0.8),
                        new ParallelDeadlineGroup(
                                new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem, 2.0),
                                new ReeceMagic(drivetrainSubsystem, follower)
                        ),
                        new FollowPathCommand(path3, drivetrainSubsystem, follower, true, 1.0)
                )
        );
    }

    private class WaitForStartCommand extends CommandBase {
        @Override
        public boolean isFinished() {
            return BackZoneSixBlue.this.isStarted();
        }
    }
}
