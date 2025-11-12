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
    private final NextPlatterCommand nextPlatterCommand;

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        TurretSubsystem turretSubsystem,
                        BooleanSupplier held) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.turretSubsystem = turretSubsystem;
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
        if (turretSubsystem.isLockedOn() && shooterSubsystem.shooterIsReady()) {
            platterSubsystem.launcherActivate();
            platterSubsystem.launchableActivate();
            nextPlatterCommand.schedule();
        }
    }

    @Override
    public boolean isFinished() {
        // You *can* keep this, since we’ll use the same `held` supplier
        return !held.getAsBoolean();
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.launchableStop();
        platterSubsystem.launcherDeactivate();
    }
}
