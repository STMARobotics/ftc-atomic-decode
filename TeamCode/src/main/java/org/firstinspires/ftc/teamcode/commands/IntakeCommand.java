package org.firstinspires.ftc.teamcode.commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.intake.IntakeSubsystem;

// This command sets the state of the intake and then immediately finishes.
public class IntakeCommand extends CommandBase {

    private final IntakeSubsystem intakeSubsystem;
    private final IntakeSubsystem.IntakeState stateToSet;

    /**
     * Creates a command to control the intake.
     * @param intakeSubsystem The intake subsystem instance.
     * @param stateToSet The state to put the intake in (INTAKING, OUTTAKING, or STOPPED).
     */
    public IntakeCommand(IntakeSubsystem intakeSubsystem, IntakeSubsystem.IntakeState stateToSet) {
        this.intakeSubsystem = intakeSubsystem;
        this.stateToSet = stateToSet;
        // Declare that this command requires the intake subsystem.
        addRequirements(intakeSubsystem);
    }

    @Override
    public void initialize() {
        // When the command starts, set the subsystem to the desired state.
        switch (stateToSet) {
            case INTAKING:
                intakeSubsystem.intake();
                break;
            case OUTTAKING:
                intakeSubsystem.outtake();
                break;
            case STOPPED:
                intakeSubsystem.stop();
                break;
        }
    }

    @Override
    public boolean isFinished() {
        // This command is finished immediately after setting the state.
        // The subsystem's periodic() loop will handle keeping the motor running.
        return true;
    }
}
