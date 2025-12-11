package org.firstinspires.ftc.teamcode.Autos;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
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

        // Schedule the generated path sequence
//        schedule(
//                new ParallelCommandGroup(
//                        new AutoLockTurretCommand(turretSubsystem, limelightSubsystem, shooterSubsystem),
//                        new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem))
//                        .andThen(
//                                new ParallelCommandGroup(
//                                        new AutoIntakeCommand(platterSubsystem, intakeSubsystem),
//                                        new SequentialCommandGroup(
//                                                new FollowPathCommand(StartPath1, follower, Path1(follower), drivetrainSubsystem),
//                                                new FollowPathCommand(StartPath2, follower, Path2(follower), drivetrainSubsystem)))
//                                        .andThen(
//                                                new ParallelCommandGroup(
//                                                        new AutoLockTurretCommand(turretSubsystem, limelightSubsystem, shooterSubsystem),
//                                                        new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem)
//                                                ))));

        schedule(
                new SequentialCommandGroup(
                        new ParallelDeadlineGroup(
                                new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem),
                                new AutoLockTurretCommand(turretSubsystem, limelightSubsystem, shooterSubsystem)
                        ),
                        new FollowPathCommand(StartPath1, follower, Path1(follower), drivetrainSubsystem).withGlobalMaxPower(0.5),
                        new ParallelDeadlineGroup(
                                new AutoIntakeCommand(platterSubsystem, intakeSubsystem),
                                new ReeceMagic(drivetrainSubsystem, follower)
                        ),
                        new FollowPathCommand(StartPath2, follower, Path2(follower), drivetrainSubsystem).withGlobalMaxPower(-0.3),
                        new ParallelDeadlineGroup(
                                new AutoShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, limelightSubsystem),
                                new AutoLockTurretCommand(turretSubsystem, limelightSubsystem, shooterSubsystem),
                                new ReeceMagic(drivetrainSubsystem, follower)
                )));
    }
    public Pose StartPath1 = new Pose(56, 8.1, Math.toRadians(180.0));
    public static PathChain Path1(Follower follower) {
        return follower.pathBuilder()
                .addPath(new BezierLine(new Pose(56, 8.1, Math.toRadians(180.0)), new Pose(15, 8.1, Math.toRadians(180.0))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

    public Pose StartPath2 = new Pose(15, 8.1, Math.toRadians(180.0));
    public static PathChain Path2(Follower follower) {
        return follower.pathBuilder()
                .addPath(new BezierLine(new Pose(15, 8.1, Math.toRadians(180.0)), new Pose(57.5, 20, Math.toRadians(180.0))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }
}