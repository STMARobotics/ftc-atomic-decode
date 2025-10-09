package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.DucShooterSubsystem;

public class DucScoringCommand extends CommandBase {

    private final DucShooterSubsystem DucShooterSubsystem;
    private final DucShooterSubsystem.TurretState stateToSet;

    /**
     * Creates a command to control the intake.
     * @param ShooterSubsystem The intake subsystem instance.
     * @param stateToSet The state to put the intake in (INTAKING, OUTTAKING, or STOPPED).
     */
    public DucScoringCommand(DucShooterSubsystem ShooterSubsystem, DucShooterSubsystem.TurretState stateToSet) {
        this.DucShooterSubsystem = ShooterSubsystem;
        this.stateToSet = stateToSet;
        addRequirements(ShooterSubsystem);
    }

    @Override
    public void initialize() {
        switch (stateToSet) {
            case SHOOTING:
                DucShooterSubsystem.shoot(10,17);
                break;
            case STOPPED:
                DucShooterSubsystem.stop();
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
