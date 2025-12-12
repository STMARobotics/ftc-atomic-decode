package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.INDEX_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.util.Timing;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class IntakeCommand extends CommandBase {

    private final PlatterSubsystem platterSubsystem;
    private final IntakeSubsystem intakeSubsystem;

    private State state = State.SEEK_MAGNET;

    Timing.Timer timer;

    private boolean lastHasArtifact = false;
    private int ballsCollected = 0;
    public IntakeCommand(PlatterSubsystem platterSubsystem,
                             IntakeSubsystem intakeSubsystem) {
        this.platterSubsystem = platterSubsystem;
        this.intakeSubsystem = intakeSubsystem;

        addRequirements(platterSubsystem, intakeSubsystem);
    }

    @Override
    public void initialize() {
        state = State.SEEK_MAGNET;
        ballsCollected = 0;
        lastHasArtifact = false;
    }

    @Override
    public void execute() {

        boolean hasArtifact = platterSubsystem.hasArtifact();

        if (hasArtifact && !lastHasArtifact) {
            ballsCollected++;
        }
        lastHasArtifact = hasArtifact;

        switch (state) {

            case SEEK_MAGNET:
                if (!platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(INDEX_POWER);
                } else {
                    platterSubsystem.stopPlatter();
                    state = State.NUDGE;
                }
                break;

            case NUDGE:
                if (!platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(-0.1);
                } else {
                    platterSubsystem.stopPlatter();
                    state = State.WAIT_FOR_ARTIFACT;
                }

            case WAIT_FOR_ARTIFACT:
                if (hasArtifact) {
                    intakeSubsystem.stop();
                    state = State.ADVANCE_TO_NEXT;
                } else {
                    platterSubsystem.spinPlatter(-0.05);
                    platterSubsystem.stopPlatter();
                    intakeSubsystem.intake();
                }
                break;

            case ADVANCE_TO_NEXT:
                intakeSubsystem.stop();
                if (platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(INDEX_POWER);
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

    private enum State {
        SEEK_MAGNET,      // go until we hit a magnet
        WAIT_FOR_ARTIFACT,// stay here and wait for a ball
        ADVANCE_TO_NEXT,   // go to next slot
        NUDGE
    }
}
