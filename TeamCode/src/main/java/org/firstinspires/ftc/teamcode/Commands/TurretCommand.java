package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class TurretCommand extends CommandBase {

    private final TurretSubsystem TurretSubsystem;
    private final TurretSubsystem.TurretState stateToSet;

    /**
     * Creates a command to control the intake.
     * @param TurretSubsystem The intake subsystem instance.
     * @param stateToSet The state to put the intake in (INTAKING, OUTTAKING, or STOPPED).
     */
    public TurretCommand(TurretSubsystem TurretSubsystem, TurretSubsystem.TurretState stateToSet) {
        this.TurretSubsystem = TurretSubsystem;
        this.stateToSet = stateToSet;
        addRequirements(TurretSubsystem);
    }

    @Override
    public void initialize() {
        switch (stateToSet) {
            case SHOOTING:
                TurretSubsystem.shoot();
                break;
            case STOPPED:
                TurretSubsystem.stop();
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
