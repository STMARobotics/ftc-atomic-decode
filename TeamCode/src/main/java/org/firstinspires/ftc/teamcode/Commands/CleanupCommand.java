package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.TURRET_HOME_DEG;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class CleanupCommand extends CommandBase {

    private final TurretSubsystem turretSubsystem;
    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;

    public CleanupCommand(TurretSubsystem turretSubsystem,
                          PlatterSubsystem platterSubsystem,
                          ShooterSubsystem shooterSubsystem) {
        this.turretSubsystem = turretSubsystem;
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        addRequirements(turretSubsystem, platterSubsystem, shooterSubsystem);
    }

    @Override
    public void initialize() {
        // Nothing to initialize
    }

    @Override
    public void execute() {
        shooterSubsystem.setRPM(1000);
        platterSubsystem.stopPlatter();
        platterSubsystem.launchableDeactivate();
        platterSubsystem.launcherDeactivate();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
    }
}
