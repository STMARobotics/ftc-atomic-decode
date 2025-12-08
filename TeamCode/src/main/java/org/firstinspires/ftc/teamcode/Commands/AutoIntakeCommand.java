package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.INDEX_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

public class AutoIntakeCommand extends CommandBase {

    private enum State {
        SEEK_MAGNET,      // go until we hit a magnet
        WAIT_FOR_ARTIFACT,// stay here and wait for a ball
        ADVANCE_TO_NEXT   // go to next slot
    }

    private final PlatterSubsystem platterSubsystem;
    private final IntakeSubsystem intakeSubsystem;

    private State state = State.SEEK_MAGNET;

    private boolean lastHasArtifact = false;
    private int ballsCollected = 0;
    private static final int BALL_TARGET = 3;

    public AutoIntakeCommand(PlatterSubsystem platterSubsystem,
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
                    intakeSubsystem.intake();
                } else {
                    platterSubsystem.stopPlatter();
                    state = State.WAIT_FOR_ARTIFACT;
                }
                break;

            case WAIT_FOR_ARTIFACT:
                if (hasArtifact) {
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
    public boolean isFinished() {
        return ballsCollected >= BALL_TARGET;
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        intakeSubsystem.stop();
    }
}
