package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.ArtifactColor.ALL;
import static org.firstinspires.ftc.teamcode.Constants.ArtifactColor.GREEN;
import static org.firstinspires.ftc.teamcode.Constants.ArtifactColor.PURPLE;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Commands.SubCommands.NextPlatterCommand;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class PrepareShootCommand extends CommandBase {

    private ShooterSubsystem shooterSubsystem;
    private TurretSubsystem turretSubsystem;
    private PlatterSubsystem platterSubsystem;

    private Constants.ArtifactColor artifactColor;

    public PrepareShootCommand(ShooterSubsystem shooterSubsystem, TurretSubsystem turretSubsystem, PlatterSubsystem platterSubsystem, Constants.ArtifactColor artifactColor) {
        this.turretSubsystem = turretSubsystem;
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        addRequirements(shooterSubsystem, turretSubsystem, platterSubsystem);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        if (artifactColor == PURPLE) {
            // read color
            if (platterSubsystem.isCorrectColor(artifactColor)) {
                platterSubsystem.launcherActivate();
                platterSubsystem.launchableActivate();
            }



        } else if (artifactColor == GREEN) {

        } else if (artifactColor == ALL) {

        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {

    }
}
