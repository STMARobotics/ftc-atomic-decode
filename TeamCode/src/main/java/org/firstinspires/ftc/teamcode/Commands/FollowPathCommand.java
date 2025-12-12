package org.firstinspires.ftc.teamcode.Commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;

public class FollowPathCommand extends CommandBase {
    private final PathChain pathChain;
    private final double maxPower;
    private final boolean holdPosition;
    private final DrivetrainSubsystem drivetrainSubsystem;
    private final Follower follower;
    public FollowPathCommand(PathChain pathChain, DrivetrainSubsystem drivetrainSubsystem, Follower follower, boolean holdPosition, double maxPower) {
        this.pathChain = pathChain;
        this.maxPower = maxPower;
        this.holdPosition = holdPosition;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.follower = follower;
        addRequirements(drivetrainSubsystem);
    }

    @Override
    public void initialize() {
        follower.setMaxPower(maxPower);
        follower.followPath(pathChain, holdPosition);
    }

    @Override
    public void execute() {
        follower.update();
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