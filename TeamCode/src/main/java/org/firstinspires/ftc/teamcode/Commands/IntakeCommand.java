package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.INDEX_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class IntakeCommand extends CommandBase {

    private enum State {
        SEEK_MAGNET,     // move until we hit a magnet
        WAIT_FOR_BALL,   // sit on magnet and wait for an artifact
        ADVANCE_TO_NEXT  // leave this magnet and head toward the next one
    }

    private final PlatterSubsystem platterSubsystem;
    private final IntakeSubsystem intakeSubsystem;

    private State state = State.SEEK_MAGNET;

    public IntakeCommand(PlatterSubsystem platterSubsystem,
                         IntakeSubsystem intakeSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.intakeSubsystem = intakeSubsystem;

        addRequirements(platterSubsystem, intakeSubsystem);
    }

    @Override
    public void initialize() {
        state = State.SEEK_MAGNET;
    }

    @Override
    public void execute() {
        switch (state) {

            case SEEK_MAGNET:
                if (!platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.nextMagnet();
                    intakeSubsystem.stop();
                } else {
                    state = State.WAIT_FOR_BALL;
                }
                break;

            case WAIT_FOR_BALL:
                if (platterSubsystem.hasArtifact()) {
                    state = State.ADVANCE_TO_NEXT;
                } else {
                    platterSubsystem.stopPlatter();
                    intakeSubsystem.intake();
                }
                break;

            case ADVANCE_TO_NEXT:
                if (platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(INDEX_POWER);
                    intakeSubsystem.stop();
                } else {
                    state = State.SEEK_MAGNET;
                }
                break;
        }
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        intakeSubsystem.stop();
    }
}
