package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

import java.util.function.BooleanSupplier;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;
    private final BooleanSupplier held;

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        TurretSubsystem turretSubsystem,
                        LookupTable lookupTable,
                        LimelightSubsystem limelightSubsystem,
                        BooleanSupplier held) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.turretSubsystem = turretSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.lookupTable = lookupTable;
        this.held = held;

        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        shooterSubsystem.setRPM(3000);
//        platterSubsystem.idlePlatter();
    }

    @Override
    public void execute() {
        turretSubsystem.autoLockTurret();

        if (turretSubsystem.isLockedOn() && shooterSubsystem.flywheelReady()) {
            platterSubsystem.launcherActivate();
            platterSubsystem.launchableActivate();
        } else {
            platterSubsystem.launchableStop();
            platterSubsystem.launcherDeactivate();
        }


    }

    @Override
    public boolean isFinished() {
        return !held.getAsBoolean();
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.launchableStop();
        platterSubsystem.launcherDeactivate();
        turretSubsystem.setTurretPower(0);
        shooterSubsystem.setRPM(0);
    }
}
