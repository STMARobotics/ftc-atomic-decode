package org.firstinspires.ftc.teamcode.Commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;

public class ReeceMagic extends CommandBase {
    private final DrivetrainSubsystem drivetrainSubsystem;
    private final Follower follower;
    private Pose startingPose = new Pose();

    public ReeceMagic(DrivetrainSubsystem drivetrainSubsystem, Follower follower) {
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.follower = follower;
        addRequirements(drivetrainSubsystem);
    }

    @Override
    public void initialize() {
        startingPose = follower.getPose();
    }

    @Override
    public void execute() {
        follower.followPath(
                follower.pathBuilder()
                        .addPath(new BezierLine(drivetrainSubsystem.getPose(), startingPose))
                        .setLinearHeadingInterpolation(drivetrainSubsystem.getPose().getHeading(), startingPose.getHeading())
                        .build()
        );
    }

    @Override
    public boolean isFinished() {
        return drivetrainSubsystem.getPose() == startingPose;
    }

    @Override
    public void end(boolean interrupted) {
        drivetrainSubsystem.stop();
    }
}