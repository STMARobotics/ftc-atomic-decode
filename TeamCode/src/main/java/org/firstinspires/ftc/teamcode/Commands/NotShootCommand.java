package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class NotShootCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;

    public NotShootCommand(PlatterSubsystem platterSubsystem, ShooterSubsystem shooterSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        addRequirements(platterSubsystem, shooterSubsystem);
    }

    @Override
    public void initialize() {
        platterSubsystem.launcherDeactivate();
        platterSubsystem.launchableDeactivate();
        platterSubsystem.idlePlatter();
        shooterSubsystem.setRPM(3000);
    }
}
