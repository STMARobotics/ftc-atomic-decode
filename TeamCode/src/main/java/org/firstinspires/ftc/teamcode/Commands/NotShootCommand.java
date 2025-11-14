package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class NotShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;

    public NotShootCommand(PlatterSubsystem platterSubsystem) {
        this.platterSubsystem = platterSubsystem;
        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        platterSubsystem.launcherDeactivate();
        platterSubsystem.launchableDeactivate();
        platterSubsystem.idlePlatter();
    }
}
