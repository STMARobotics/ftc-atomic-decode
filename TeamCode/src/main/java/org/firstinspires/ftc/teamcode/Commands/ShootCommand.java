package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SHOOT_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class ShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final LimelightSubsystem limelightSubsystem;

    public ShootCommand(PlatterSubsystem platterSubsystem, ShooterSubsystem shooterSubsystem, LimelightSubsystem limelightSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        platterSubsystem.spinPlatter(SHOOT_POWER);
    }

    @Override
    public void execute() {
        if (shooterSubsystem.flywheelReady() && limelightSubsystem.getDistance() > 9.0) {
            platterSubsystem.launchableActivate();
            platterSubsystem.launcherActivate();
        } else if (limelightSubsystem.getDistance() <= 9.0) {
            platterSubsystem.launchableActivate();
            platterSubsystem.launcherActivate();
        } else {
            platterSubsystem.launcherDeactivate();
            platterSubsystem.launchableDeactivate();
        }
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        platterSubsystem.launcherDeactivate();
        platterSubsystem.launchableDeactivate();
    }
}
