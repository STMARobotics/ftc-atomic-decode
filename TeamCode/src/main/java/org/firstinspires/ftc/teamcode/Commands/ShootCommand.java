package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

import java.util.function.BooleanSupplier;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final BooleanSupplier held;
    private NextPlatterCommand nextPlatterCommand;

    /**
     * @param platterSubsystem subsystem that controls the platter/launcher
     * @param held a BooleanSupplier that returns true while the button is held
     */
    public ShootCommand(PlatterSubsystem platterSubsystem, BooleanSupplier held) {
        this.platterSubsystem = platterSubsystem;
        this.held = held;
        addRequirements(platterSubsystem);

        nextPlatterCommand = new NextPlatterCommand(platterSubsystem);
    }

    @Override
    public void initialize() {
        if (!platterSubsystem.isMagnetTripped()) {
            nextPlatterCommand.schedule();
        }
    }

    @Override
    public void execute() {
        if (turretSubsystem.isLockedOn() && shooterSubsystem.shooterIsReady()){
            platterSubsystem.launcherActivate();
            platterSubsystem.launchableActivate();
            nextPlatterCommand.schedule();
        }
    }

    @Override
    public boolean isFinished() {
        return !held.getAsBoolean();
    }

    @Override
    public void end(boolean interrupted) {
        // make sure launcher stops when the command ends or is interrupted
        platterSubsystem.launchableStop();
        platterSubsystem.launcherDeactivate();
    }
}