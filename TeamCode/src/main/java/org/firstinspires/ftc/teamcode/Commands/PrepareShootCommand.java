package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class PrepareShootCommand extends CommandBase {

    private ShooterSubsystem shooterSubsystem;
    private LookupTable lookupTable;
    private LimelightSubsystem limelightSubsystem;

    public PrepareShootCommand(ShooterSubsystem shooterSubsystem, LookupTable lookupTable, LimelightSubsystem limelightSubsystem) {
        this.shooterSubsystem = shooterSubsystem;
        this.lookupTable = lookupTable;
        this.limelightSubsystem = limelightSubsystem;
        addRequirements(shooterSubsystem, lookupTable);
    }

    // purpose of this file is to spin shooter and set hood position according to lookuptable
    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        double targetRPM = lookupTable.getShooterRPM(limelightSubsystem.getDistance());
        double targetHoodAngle = lookupTable.getHoodAngle(limelightSubsystem.getDistance());

        shooterSubsystem.setRPM(targetRPM);
        shooterSubsystem.setHoodAngle(targetHoodAngle);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        shooterSubsystem.stop();
        shooterSubsystem.setHoodAngle(0);
    }

}

