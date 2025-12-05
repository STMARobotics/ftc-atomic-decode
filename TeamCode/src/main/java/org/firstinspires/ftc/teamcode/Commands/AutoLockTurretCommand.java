package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.TURRET_KD;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.TURRET_KP;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.controller.PIDController;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class AutoLockTurretCommand extends CommandBase {

    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final LookupTable lookupTable;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    public AutoLockTurretCommand(TurretSubsystem turretSubsystem,
                                 LookupTable lookupTable,
                                 LimelightSubsystem limelightSubsystem,
                                 ShooterSubsystem shooterSubsystem) {
        this.turretSubsystem = turretSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.lookupTable = lookupTable;
        addRequirements(turretSubsystem, lookupTable, limelightSubsystem, shooterSubsystem);
    }

    @Override
    public void initialize() {
        pidController.reset();
    }

    @Override
    public void execute() {
        autoLockTurret();
        double distanceToTarget = limelightSubsystem.getDistance();
        double lookupRPM = lookupTable.getShooterRPM(distanceToTarget);
        double hoodAngle = lookupTable.getHoodAngle(distanceToTarget);
        shooterSubsystem.setRPM(lookupRPM);
        turretSubsystem.setHoodAngle(hoodAngle);
    }

    @Override
    public void end(boolean interrupted) {
        // Things get cleaned up in the cleanup command
    }

    public void autoLockTurret() {
        double error = limelightSubsystem.getTargetOffset();
        if (Double.isNaN(error)) {
            turretSubsystem.stopTurret();
        } else {
            double output = pidController.calculate(error, 0.0);

            turretSubsystem.setTurretPower(output);
        }
    }
}
