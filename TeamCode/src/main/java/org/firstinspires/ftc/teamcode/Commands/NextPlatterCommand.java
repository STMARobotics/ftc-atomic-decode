package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class NextPlatterCommand extends CommandBase {
    private final PlatterSubsystem platterSubsystem;

    public NextPlatterCommand(PlatterSubsystem platterSubsystem) {
        this.platterSubsystem = platterSubsystem;
        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        platterSubsystem.spinPlatter(0.12);
    }

    @Override
    public boolean isFinished() {
        return platterSubsystem.isMagnetTripped();
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
    }
}
