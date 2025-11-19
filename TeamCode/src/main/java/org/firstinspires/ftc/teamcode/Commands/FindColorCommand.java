package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class FindColorCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;

    public FindColorCommand(PlatterSubsystem platterSubsystem) {
        this.platterSubsystem = platterSubsystem;
        addRequirements(platterSubsystem);

    }

    @Override
    public void initialize() {
        if (platterSubsystem.isMagnetTripped()) {
            platterSubsystem.spinPlatter(0.2);
        } else {
            platterSubsystem.stopPlatter();
        }
    }

    @Override
    public void execute() {
        if (platterSubsystem.isMagnetTripped()) {

        }

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {

    }
}
