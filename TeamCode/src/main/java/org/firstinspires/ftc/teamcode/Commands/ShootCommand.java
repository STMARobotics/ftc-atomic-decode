package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.NUDGE_POWER;
import static org.firstinspires.ftc.teamcode.Constants.PlatterConstants.SHOOT_POWER;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

public class ShootCommand extends CommandBase {

    private enum State {
        WAIT_FOR_READY,      // wait for shooter + turret
        LEAVE_INITIAL_MAGNET,
        SEEK_NEXT_MAGNET,
        NUDGE_BACK,
        DONE
    }

    private final PlatterSubsystem platterSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final TurretSubsystem turretSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final LookupTable lookupTable;
    private final Constants.ArtifactColor artifactColor;

    private State state;
    private boolean done;

    public ShootCommand(PlatterSubsystem platterSubsystem,
                        ShooterSubsystem shooterSubsystem,
                        LookupTable lookupTable,
                        LimelightSubsystem limelightSubsystem,
                        TurretSubsystem turretSubsystem,
                        Constants.ArtifactColor artifactColor) {
        this.platterSubsystem = platterSubsystem;
        this.shooterSubsystem = shooterSubsystem;
        this.turretSubsystem  = turretSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        this.lookupTable = lookupTable;
        this.artifactColor = artifactColor;

        addRequirements(platterSubsystem);
    }

    @Override
    public void initialize() {
        done = false;
        state = State.WAIT_FOR_READY;
    }

    @Override
    public void execute() {
        switch (state) {

            case WAIT_FOR_READY:
                // Wait until shooter AND turret are ready
                if (shooterSubsystem.flywheelReady() && turretSubsystem.isLockedOn()) {
                    // Start launcher + rollers exactly when we decide to shoot
                    platterSubsystem.launchableActivate();
                    platterSubsystem.launcherActivate();
                    state = State.LEAVE_INITIAL_MAGNET;
                }
                break;

            case LEAVE_INITIAL_MAGNET:
                // We start ON a magnet at the correct slot.
                // Move forward until we are OFF this magnet.
                if (platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(SHOOT_POWER);
                } else {
                    // Left initial slot → go find next magnet.
                    state = State.SEEK_NEXT_MAGNET;
                }
                break;

            case SEEK_NEXT_MAGNET:
                // Move forward until the next magnet.
                platterSubsystem.spinPlatter(SHOOT_POWER);
                if (platterSubsystem.isMagnetTripped()) {
                    // Hit next magnet (may have coasted a bit); now reverse to center.
                    state = State.NUDGE_BACK;
                }
                break;

            case NUDGE_BACK:
                // Reverse slowly until we are on the magnet again.
                if (!platterSubsystem.isMagnetTripped()) {
                    platterSubsystem.spinPlatter(-NUDGE_POWER);
                } else {
                    platterSubsystem.stopPlatter();
                    state = State.DONE;
                    done = true;
                }
                break;

            case DONE:
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
