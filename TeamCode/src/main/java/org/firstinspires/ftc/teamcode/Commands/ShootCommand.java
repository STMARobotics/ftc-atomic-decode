package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.controller.PIDController;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

import static org.firstinspires.ftc.teamcode.Constants.TempShooterConstants.DELAY_MS;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    private boolean successfulShot;

    private boolean hasLaunched = false;
    private long launchTimeMs = 0;

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
        shooterSubsystem.setRPM(2750); // tune pls
        pidController.reset();
        platterSubsystem.launchableActivate();

        successfulShot = false;
        hasLaunched = false;
        launchTimeMs = 0;
    }

    @Override
    public void execute() {
        autoLockTurret();

        if (hasLaunched) {
            long now = System.currentTimeMillis();
            if (now - launchTimeMs >= DELAY_MS) {
                successfulShot = true;  // command can now end
            }
            return;
        }

        boolean readyToShoot =
                turretSubsystem.isLockedOn() && shooterSubsystem.flywheelReady();

        if (readyToShoot) {
            platterSubsystem.launcherActivate();
            hasLaunched = true;
            launchTimeMs = System.currentTimeMillis();
        } else {
            platterSubsystem.launcherDeactivate();
            successfulShot = false;
        }
    }

    @Override
    public boolean isFinished() {
        return successfulShot;
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.launchableDeactivate();
        platterSubsystem.launcherDeactivate();
        platterSubsystem.stopPlatter();
        turretSubsystem.setTurretPower(0);
        shooterSubsystem.setRPM(0);
    }

    public void autoLockTurret() {
        double error = limelightSubsystem.getTargetOffset();
        if (!limelightSubsystem.hasValidTarget()) {
            turretSubsystem.setTurretPower(0);
        } else {
            double output = pidController.calculate(error, 0.0);
            turretSubsystem.setTurretPower(output);
        }
    }
}
