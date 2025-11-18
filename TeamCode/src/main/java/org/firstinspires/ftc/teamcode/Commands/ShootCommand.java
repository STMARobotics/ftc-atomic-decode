package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.controller.PIDController;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;
    private final Constants.ArtifactColor artifactColor;

    private final PIDController pidController = new PIDController(TURRET_KP, 0.0, TURRET_KD);

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        LookupTable lookupTable,
                        LimelightSubsystem limelightSubsystem,
                        Constants.ArtifactColor artifactColor) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.lookupTable = lookupTable;
        this.artifactColor = artifactColor;

        addRequirements(platterSubsystem, shooterSubsystem, limelightSubsystem, lookupTable);
    }

    @Override
    public void initialize() {
        shooterSubsystem.setRPM(3000); // tune pls
        pidController.reset();
    }

    @Override
    public void execute() {
        // are we at magnet?
        // no: platterSubsystem.nextMagnet();
        // yes: continue
        // is correct color?
        // no: platterSubsystem.nextMagnet();
        // yes: continue
        // turretSubsystem.isLocked();
        // no: loop execute
        // yes: continue
        // platterSubsystem.launchableActivate();
        // platterSubsystem.launcherActivate();
        // delay it
        // platterSubsystem.nextMagnet();
    }

    @Override
    public boolean isFinished() {
        return false; // We don't want it to end on its own
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.launchableDeactivate();
        platterSubsystem.launcherDeactivate();
        platterSubsystem.stopPlatter();
        shooterSubsystem.setRPM(0);
    }
}
