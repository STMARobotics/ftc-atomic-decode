package org.firstinspires.ftc.teamcode.Commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;

public class FollowPathCommand extends CommandBase {
    private final PathChain pathChain;
    private final double maxPower;
    private final DrivetrainSubsystem drivetrainSubsystem;
    private final Follower follower;
    public FollowPathCommand(PathChain pathChain, double maxPower, DrivetrainSubsystem drivetrainSubsystem, Follower follower) {
        this.pathChain = pathChain;
        this.maxPower = maxPower;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.follower = follower;
        addRequirements(drivetrainSubsystem);
    }

    @Override
    public void initialize() {
        follower.setMaxPower(maxPower);
        follower.followPath(pathChain, false);
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
    }

    @Override
    public void end(boolean interrupted) {
        drivetrainSubsystem.stop();
    }
}