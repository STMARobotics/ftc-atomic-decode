package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SHOOT_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;

    public ShootCommand(PlatterSubsystem platterSubsystem) {
        this.platterSubsystem = platterSubsystem;

        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        platterSubsystem.spinPlatter(SHOOT_POWER);
        platterSubsystem.launchableActivate();
        platterSubsystem.launcherActivate();
    }

    @Override
    public void execute() {
        // Command continues until interrupted
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        platterSubsystem.launcherDeactivate();
        platterSubsystem.launchableDeactivate();
    }
}
