package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.NUDGE_POWER;
import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SHOOT_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

public class ShootCommand extends CommandBase {

    private enum State {
        LEAVE_INITIAL_MAGNET,  // leave current slot
        SEEK_NEXT_MAGNET,      // drive forward until we see the next magnet
        NUDGE_BACK,            // reverse slowly until we're back on that magnet
        DONE
    }

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;
    private final Constants.ArtifactColor artifactColor;

    private State state;
    private boolean done;

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        LookupTable lookupTable,
                        LimelightSubsystem limelightSubsystem,
                        Constants.ArtifactColor artifactColor) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.lookupTable = lookupTable;
        this.artifactColor = artifactColor;

        addRequirements(platterSubsystem, shooterSubsystem);
        // limelight / lookup / color are read-only if you use them later
    }

    @Override
    public void initialize() {
        done = false;
        state = State.LEAVE_INITIAL_MAGNET;

        shooterSubsystem.setRPM(3000); // tune pls

        platterSubsystem.launchableActivate();
        platterSubsystem.launcherActivate();
    }

    @Override
    public void execute() {
        switch (state) {
            case LEAVE_INITIAL_MAGNET:
                // We start ON a magnet at the correct color/slot.
                // Move forward until we are OFF this magnet.
                if (platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(SHOOT_POWER);
                } else {
                    // Left the initial slot, now look for the NEXT magnet.
                    state = State.SEEK_NEXT_MAGNET;
                }
                break;

            case SEEK_NEXT_MAGNET:
                // Move forward at shooting speed until we hit the next magnet.
                platterSubsystem.spinPlatter(SHOOT_POWER);
                if (platterSubsystem.isMagnetTripped()) {
                    // We’ve reached the next slot (may coast a bit past).
                    // Now reverse slowly until we're centered back on it.
                    state = State.NUDGE_BACK;
                }
                break;

            case NUDGE_BACK:
                // Reverse slowly until the magnet is tripped again.
                if (!platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(-NUDGE_POWER);
                } else {
                    // Back on magnet → stop and finish.
                    platterSubsystem.stopPlatter();
                    state = State.DONE;
                    done = true;
                }
                break;

            case DONE:
                // Nothing. Wait for end() to clean up.
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void end(boolean interrupted) {
        platterSubsystem.stopPlatter();
        platterSubsystem.launchableDeactivate();
        platterSubsystem.launcherDeactivate();
    }
}
