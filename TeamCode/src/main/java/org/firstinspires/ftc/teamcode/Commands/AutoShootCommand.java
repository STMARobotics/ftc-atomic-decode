package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SHOOT_POWER;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.TURRET_KP;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.TURRET_KD;
import static org.firstinspires.ftc.teamcode.Constants.INTERPOLATOR;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.controller.PIDController;

import org.firstinspires.ftc.teamcode.Math.LookupTableMath;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class AutoShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    private int magsHit = 0;
    private boolean lastMagState = false;
    private boolean done = false;

    private static final int MAG_HIT_TARGET = 4;

    public AutoShootCommand(PlatterSubsystem platterSubsystem,
                            ShooterSubsystem shooterSubsystem,
                            TurretSubsystem turretSubsystem,
                            LimelightSubsystem limelightSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.turretSubsystem = turretSubsystem;
        this.limelightSubsystem = limelightSubsystem;

        addRequirements(platterSubsystem, shooterSubsystem, turretSubsystem);
    }

    @Override
    public void initialize() {
        magsHit = 0;
        lastMagState = platterSubsystem.isMagnetTripped();
        done = false;

        pidController.reset();

        platterSubsystem.launchableActivate();
        platterSubsystem.launcherActivate();
    }

    @Override
    public void execute() {
        autoLockAndSpinup();

        boolean readyToAdvance = shooterSubsystem.flywheelReady();

        if (readyToAdvance) {
            platterSubsystem.spinPlatter(SHOOT_POWER);
        } else {
            platterSubsystem.stopPlatter();
        }

        boolean magNow = platterSubsystem.isMagnetTripped();

        if (magNow && !lastMagState) {
            magsHit++;
        }

        lastMagState = magNow;

        if (magsHit >= MAG_HIT_TARGET) {
            done = true;
        }
    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        platterSubsystem.launchableDeactivate();
        platterSubsystem.launcherDeactivate();

        turretSubsystem.stopTurret();
        shooterSubsystem.setRPM(0);
    }

    private void autoLockAndSpinup() {
        double error = limelightSubsystem.getTargetOffset();
        boolean hasTarget = limelightSubsystem.hasValidTarget();

        if (!hasTarget || Double.isNaN(error)) {
            turretSubsystem.stopTurret();
        } else {
            double output = pidController.calculate(error, 0.0);
            turretSubsystem.setTurretPower(output);
        }

        double distance = limelightSubsystem.getDistance();
        if (!Double.isNaN(distance)) {
            LookupTableMath.ShootingSettings s = INTERPOLATOR.calculate(distance);
            double targetRpm   = s.getVelocity();
            double hoodAngle   = s.getPitch();

            shooterSubsystem.setRPM(targetRpm);
            turretSubsystem.setHoodAngle(hoodAngle);
        }
    }
}
