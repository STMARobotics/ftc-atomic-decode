package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SHOOT_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;

    private boolean done;

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        LookupTable lookupTable,
                        LimelightSubsystem limelightSubsystem,
                        TurretSubsystem turretSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.turretSubsystem = turretSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.lookupTable = lookupTable;

        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        platterSubsystem.spinPlatter(SHOOT_POWER);
        platterSubsystem.launchableActivate();
        platterSubsystem.launcherActivate();
    }

//    @Override
//    public void execute() {
//    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        platterSubsystem.launcherDeactivate();
        platterSubsystem.launchableDeactivate();
    }
}
