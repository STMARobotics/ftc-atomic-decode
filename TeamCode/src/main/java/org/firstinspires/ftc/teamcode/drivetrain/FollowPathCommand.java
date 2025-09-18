package org.firstinspires.ftc.teamcode.drivetrain;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.seattlesolvers.solverslib.command.CommandBase;

/**
 * Allows you to run a PathChain or a Path (which is then converted into a PathChain) by scheduling it.
 * holdEnd is set to true by default, so you only need to give it your instance of follower and the Path to follow.
 * <p>This command is based on FollowPathCommand from solverslib but adds subsystem integration</p>
 */
public class FollowPathCommand extends CommandBase {

    private final Follower follower;
    private final PathChain pathChain;
    private final Pose startPose;
    private final DrivetrainSubsystem drivetrainSubsystem;

    private boolean holdEnd = false;
    private double maxPower = 1.0;

    public FollowPathCommand(
            Pose startPose,
            Follower follower,
            PathChain pathChain,
            DrivetrainSubsystem drivetrainSubsystem) {
        this.follower = follower;
        this.pathChain = pathChain;
        this.startPose = startPose;
        this.drivetrainSubsystem = drivetrainSubsystem;

        addRequirements(drivetrainSubsystem);
    }

    /**
     * Sets Global Maximum Power for Follower
     *
     * @param globalMaxPower The new globalMaxPower
     * @return This command for chaining
     */
    public FollowPathCommand withGlobalMaxPower(double globalMaxPower) {
        follower.setMaxPower(globalMaxPower);
        maxPower = globalMaxPower;
        return this;
    }

    /**
     * Sets the holdEnd parameter for following path
     * @param holdEnd new holdEnd value
     * @return this command for chaining
     */
    public FollowPathCommand withHoldEnd(boolean holdEnd) {
        this.holdEnd = holdEnd;
        return this;
    }

    @Override
    public void initialize() {
        follower.setStartingPose(startPose);
        if (maxPower != 1.0) {
            follower.followPath(pathChain, maxPower, holdEnd);
        }
        follower.followPath(pathChain, holdEnd);
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