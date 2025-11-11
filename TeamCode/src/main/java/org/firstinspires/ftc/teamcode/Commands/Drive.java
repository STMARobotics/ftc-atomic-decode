package org.firstinspires.ftc.teamcode.Commands;

import static org.firstinspires.ftc.teamcode.Constants.DriveCommandConstants.JOYSTICK_DEADZONE;

import com.pedropathing.geometry.Pose;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;

import java.util.function.DoubleSupplier;

public class Drive extends CommandBase {

    private final DrivetrainSubsystem drivetrain;
    private final DoubleSupplier txSupplier;
    private final DoubleSupplier tySupplier;
    private final DoubleSupplier tzSupplier;
    private final DoubleSupplier reductionSupplier;

    private boolean holding = false;
    private Pose heldPose = null;

    public Drive(DrivetrainSubsystem drivetrain,
                 DoubleSupplier txSupplier,
                 DoubleSupplier tySupplier,
                 DoubleSupplier tzSupplier) {
        this(drivetrain, txSupplier, tySupplier, tzSupplier, () -> 1.0);
    }

    public Drive(DrivetrainSubsystem drivetrain,
                 DoubleSupplier txSupplier,
                 DoubleSupplier tySupplier,
                 DoubleSupplier tzSupplier,
                 DoubleSupplier reductionSupplier) {
        this.drivetrain = drivetrain;
        this.txSupplier = txSupplier;
        this.tySupplier = tySupplier;
        this.tzSupplier = tzSupplier;
        this.reductionSupplier = reductionSupplier;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        drivetrain.startTeleop();
    }

    @Override
    public void execute() {
//        double tx = txSupplier.getAsDouble();
//        double ty = tySupplier.getAsDouble();
//        double tz = tzSupplier.getAsDouble();
//
//        boolean inputsZero = Math.abs(tx) <= JOYSTICK_DEADZONE
//                && Math.abs(ty) <= JOYSTICK_DEADZONE
//                && Math.abs(tz) <= JOYSTICK_DEADZONE;
//
//        if (inputsZero) {
//            if (!holding) {
//                heldPose = drivetrain.getCurrentPose();
//                holding = true;
//            }
//            if (heldPose != null) {
//                drivetrain.holdPosition(heldPose);
//            }
//        } else {
//            holding = false;
//            drivetrain.drive(tx, ty, tz, reductionSupplier.getAsDouble());
//        }

        // backup simple drive
        drivetrain.drive(
                txSupplier.getAsDouble(),
                tySupplier.getAsDouble(),
                tzSupplier.getAsDouble(),
                reductionSupplier.getAsDouble());
    }

    @Override
    public boolean isFinished() {
        return false; // Dont stop pls its kinda important
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.stop();
    }
}
