package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class PrepareShootCommand extends CommandBase {

    private ShooterSubsystem shooterSubsystem;
    private LookupTable lookupTable


    public PrepareShootCommand(ShooterSubsystem shooterSubsystem, LookupTable lookupTable) {
        this.shooterSubsystem = shooterSubsystem;
        this.lookupTable = lookupTable;
        addRequirements(shooterSubsystem, lookupTable);
    }

    // purpose of this file is to spin shooter and set hood position according to lookuptable
    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        double targetRPM = lookupTable.getShooterRPM();
        double targetHoodAngle = lookupTable.getHoodAngle();

        shooterSubsystem.setRPM(targetRPM);
        shooterSubsystem.setHoodAngle(targetHoodAngle);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}

