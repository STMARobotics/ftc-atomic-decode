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

    private TurretSubsystem turretSubsystem;
    private LookupTable lookupTable;
    private LimelightSubsystem limelightSubsystem;
    private ShooterSubsystem shooterSubsystem;

    private double distanceToTarget;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    public AutoLockTurretCommand(TurretSubsystem turretSubsystem,
                                 LookupTable lookupTable,
                                 LimelightSubsystem limelightSubsystem,
                                 ShooterSubsystem shooterSubsystem) {
        this.turretSubsystem = turretSubsystem;
        this.lookupTable = lookupTable;
        this.limelightSubsystem = limelightSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        addRequirements(turretSubsystem, lookupTable, limelightSubsystem, shooterSubsystem);
    }

    @Override
    public void initialize() {
        pidController.reset();
    }

    @Override
    public void execute() {
        distanceToTarget = limelightSubsystem.getDistance();
        turretSubsystem.autoLockTurret();
        shooterSubsystem.setRPM(3000); // Only hood movement saves power
        turretSubsystem.setHoodAngle(lookupTable.getHoodAngle(distanceToTarget));
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        // Things get cleaned up in the cleanup command
    }
}
