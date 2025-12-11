package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Constants.ArtifactColor;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.NUDGE_POWER;
import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SEARCH_POWER;

public class FindColorCommand extends CommandBase {

    private enum State {
        SEEK_MAGNET,     // move until were on a magnet
        CHECK_COLOR,     // on magnet: check artifact color
        NUDGE_OFF,       // move off current magnet so we can find the next one
        DONE
    }

    private final PlatterSubsystem platterSubsystem;
    private final ArtifactColor targetColor;

    private State state = State.SEEK_MAGNET;

    public FindColorCommand(PlatterSubsystem platterSubsystem,
                            ArtifactColor targetColor) {
        this.platterSubsystem = platterSubsystem;
        this.targetColor = targetColor;
        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        platterSubsystem.launcherDeactivate();
        state = State.SEEK_MAGNET;
    }

    @Override
    public void execute() {
        switch (state) {

            case SEEK_MAGNET:
                if (!platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(SEARCH_POWER);
                } else {
                    platterSubsystem.stopPlatter();
                    state = State.DONE;
                }
                break;

            case CHECK_COLOR:
                if (targetColor == ArtifactColor.ALL ||
                        platterSubsystem.isCorrectColor(targetColor)) {
                    platterSubsystem.stopPlatter();
                    state = State.DONE;
                } else {
                    state = State.NUDGE_OFF;
                }
                break;

            case NUDGE_OFF:
                if (platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(NUDGE_POWER);
                } else {
                    platterSubsystem.stopPlatter();
                    state = State.SEEK_MAGNET;
                }
                break;

            case DONE:
                platterSubsystem.stopPlatter();
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return state == State.DONE;
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
    }
}
