package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.FunctionalCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;

// I copied andy so merek can sleep at night

@TeleOp(name="cool op mode", group = "cool things")
public class CoolOpMode extends CommandOpMode {

    private DrivetrainSubsystem drivetrainSubsystem;

    private double reductionFactor = 1;

    @Override
    public void initialize() {
        // Create subsystems
        drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);

        /*
        The origin is the field perimeter corner by the red loading zone.
        We'll drive from the perspective of the red alliance:
         - Pushing up on the left stick moves toward the blue alliance wall, which is positive X.
         - Pushing to the left on the left stick moves toward the obelisk wall, which is positive Y.
         - Pushing to the left on the right stick rotates the the positive direction,
         counterclockwise
         */
        FunctionalCommand teleopDriveCommand = new FunctionalCommand(drivetrainSubsystem::startTeleop,
                () -> drivetrainSubsystem.drive(
                        -gamepad1.left_stick_y, // Stick up is negative but moves +X, so invert
                        -gamepad1.left_stick_x, // Stick left is negative but moves +Y, so invert
                        -gamepad1.right_stick_x, // Stick left is negative but moves +rotation, so invert
                        1),
                (b) -> drivetrainSubsystem.stop(),
                () -> false,
                drivetrainSubsystem);

        RunCommand telemetryCommand = new RunCommand(() -> {
            drivetrainSubsystem.telemetrize(telemetry);
            telemetry.update();
        });

        // Schedule commands
        schedule(telemetryCommand);

        // Register subsystems
        register(drivetrainSubsystem);

        // Set default commands for subsystems
        drivetrainSubsystem.setDefaultCommand(teleopDriveCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {

        // Bind driver buttons
        GamepadEx gamepad = new GamepadEx(gamepad1);
        gamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        // All the stuff here
    }

    private void slowMode() {
        reductionFactor = (reductionFactor == 1.0) ? 0.5 : 1.0;
    }
}
