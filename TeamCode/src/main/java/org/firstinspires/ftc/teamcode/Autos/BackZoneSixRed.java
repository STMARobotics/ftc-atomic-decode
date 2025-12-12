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

@Autonomous(name = "BackZoneSixRed", group = "Autos")
public class BackZoneSixRed extends CommandOpMode {

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

        limelightSubsystem.pipelineSwitcher(1);

        Pose startPose1 = new Pose(56, 8.1, Math.toRadians(180.0));
        PathChain path1 =
                mirrorPath(startPose1, new Pose(15, 8.1, Math.toRadians(180.0)), follower);

        Pose startPose2 = new Pose(15, 8.1, Math.toRadians(180.0));
        PathChain path2 =
                mirrorPath(startPose2, new Pose(51, 8.1, Math.toRadians(180.0)), follower);

        Pose startPose3 = new Pose(51, 8.1, Math.toRadians(180));
        PathChain path3 =
                mirrorPath(startPose3, new Pose(51, 30, Math.toRadians(0.0)), follower);

        follower.setStartingPose(mirrorPose(startPose1));
        follower.setPose(mirrorPose(startPose1));

        schedule(
                new SequentialCommandGroup(
                        new WaitForStartCommand(),
                        new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem, -2.0),
                        new ParallelDeadlineGroup(
                                new AutoIntakeCommand(platterSubsystem, intakeSubsystem).withTimeout(10000),
                                new FollowPathCommand(path1, drivetrainSubsystem, follower, true, 0.7)
                        ),
                        new FollowPathCommand(path2, drivetrainSubsystem, follower, false, 0.8),
                        new ParallelDeadlineGroup(
                                new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem, -2.0),
                                new ReeceMagic(drivetrainSubsystem, follower)
                        ),
                        new FollowPathCommand(path3, drivetrainSubsystem, follower, true, 1.0)
                )
        );
    }

    private class WaitForStartCommand extends CommandBase {
        @Override
        public boolean isFinished() {
            return BackZoneSixRed.this.isStarted();
        }
    }

    private Pose mirrorPose(Pose pose) {
        final double FIELD_WIDTH = 144.0;

        double x1 = pose.getX();
        double y1 = pose.getY();
        double h1 = pose.getHeading();

        return new Pose((FIELD_WIDTH - x1), y1, -h1 + Math.PI);
    }

    private PathChain mirrorPath(Pose pose1, Pose pose2, Follower follower) {
        final double FIELD_WIDTH = 144.0;

        double x1 = pose1.getX();
        double y1 = pose1.getY();
        double h1 = pose1.getHeading();

        double x2 = pose2.getX();
        double y2 = pose2.getY();
        double h2 = pose2.getHeading();

        Pose m1 = new Pose((FIELD_WIDTH - x1), y1, -h1 + Math.PI);
        Pose m2 = new Pose((FIELD_WIDTH - x2), y2, -h2 + Math.PI);

        return follower.pathBuilder()
                .addPath(new BezierLine(m1, m2))
                .setLinearHeadingInterpolation(m1.getHeading(), m2.getHeading())
                .build();
    }
}
