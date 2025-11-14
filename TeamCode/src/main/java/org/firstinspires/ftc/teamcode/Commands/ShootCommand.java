package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.controller.PIDController;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

import java.util.function.BooleanSupplier;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        TurretSubsystem turretSubsystem,
                        LookupTable lookupTable,
                        LimelightSubsystem limelightSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.turretSubsystem = turretSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.lookupTable = lookupTable;

        addRequirements(platterSubsystem, turretSubsystem, shooterSubsystem);
    }

    @Override
    public void initialize() {
        shooterSubsystem.setRPM(3000);
        platterSubsystem.idlePlatter();
        pidController.reset();
    }

    @Override
    public void execute() {
        autoLockTurret();

        if (turretSubsystem.isLockedOn() && shooterSubsystem.flywheelReady()) {
            platterSubsystem.launcherActivate();
            platterSubsystem.launchableActivate();
        } else {
            platterSubsystem.launchableStop();
            platterSubsystem.launcherDeactivate();
        }
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.launchableStop();
        platterSubsystem.launcherDeactivate();
        platterSubsystem.stopPlatter();
        turretSubsystem.setTurretPower(0);
        shooterSubsystem.setRPM(0);
    }

    public void autoLockTurret() {
        double error = limelightSubsystem.getTargetOffset();
        if (Double.isNaN(error)) {
            turretSubsystem.stopTurret();
        }

        double output = pidController.calculate(error, 0.0);

        turretSubsystem.setTurretPower(output);
    }
}
