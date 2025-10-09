package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class ScoringCommand extends CommandBase {

    private final ShooterSubsystem ShooterSubsystem;
    private final ShooterSubsystem.TurretState stateToSet;

    /**
     * Creates a command to control the intake.
     * @param ShooterSubsystem The intake subsystem instance.
     * @param stateToSet The state to put the intake in (INTAKING, OUTTAKING, or STOPPED).
     */
    public ScoringCommand(ShooterSubsystem ShooterSubsystem, ShooterSubsystem.TurretState stateToSet) {
        this.ShooterSubsystem = ShooterSubsystem;
        this.stateToSet = stateToSet;
        addRequirements(ShooterSubsystem);
    }

    @Override
    public void initialize() {
        switch (stateToSet) {
            case SHOOTING:
                ShooterSubsystem.shoot(10,17);
                break;
            case STOPPED:
                ShooterSubsystem.stop();
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
