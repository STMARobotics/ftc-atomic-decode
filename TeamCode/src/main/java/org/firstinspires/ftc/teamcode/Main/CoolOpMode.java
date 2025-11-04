package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.command.button.Button;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import org.firstinspires.ftc.teamcode.Commands.AutoLockTurretCommand;
import org.firstinspires.ftc.teamcode.Commands.NotShootCommand;
import org.firstinspires.ftc.teamcode.Commands.Drive;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

// I copied andy so merek can sleep at night

@TeleOp(name="cool op mode", group = "cool things")
public class CoolOpMode extends CommandOpMode {

    private DrivetrainSubsystem drivetrainSubsystem;
    private NotShootCommand notShootCommand;
    private AutoLockTurretCommand autoLockTurretCommand;
    private ShooterSubsystem shooterSubsystem;
    private TurretSubsystem turretSubsystem;
    private PlatterSubsystem platterSubsystem;
    private IntakeSubsystem intakeSubsystem;

    private GamepadEx gamepad;
    private TriggerReader leftTriggerReader;

    private double reductionFactor = 1;

    @Override
    public void initialize() {
        // Create subsystems
        drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);
        shooterSubsystem = new ShooterSubsystem(hardwareMap);
        turretSubsystem = new TurretSubsystem(hardwareMap);
        platterSubsystem = new PlatterSubsystem(hardwareMap);
        intakeSubsystem = new IntakeSubsystem(hardwareMap);

        notShootCommand = new NotShootCommand(platterSubsystem);
        autoLockTurretCommand = new AutoLockTurretCommand(turretSubsystem);

        /*
        The origin is the field perimeter corner by the red loading zone.
        We'll drive from the perspective of the red alliance:
         - Pushing up on the left stick moves toward the blue alliance wall, which is positive X.
         - Pushing to the left on the left stick moves toward the obelisk wall, which is positive Y.
         - Pushing to the left on the right stick rotates the the positive direction,
         counterclockwise
         */
        Drive teleopDriveCommand = new Drive(
                drivetrainSubsystem,
                () -> -gamepad1.left_stick_y,  // Stick up is negative but moves +X, so invert
                () -> -gamepad1.left_stick_x,  // Stick left is negative but moves +Y, so invert
                () -> -gamepad1.right_stick_x, // Stick left is negative but moves +rotation, so invert
                () -> reductionFactor
        );

        RunCommand telemetryCommand = new RunCommand(() -> {
            drivetrainSubsystem.telemetrize(telemetry);
            telemetry.update();
        });
        schedule(telemetryCommand);

        schedule(autoLockTurretCommand);

        register(drivetrainSubsystem);

        // Set default commands for subsystems
        platterSubsystem.setDefaultCommand(notShootCommand);
        drivetrainSubsystem.setDefaultCommand(teleopDriveCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {

        // Bind driver buttons
        GamepadEx gamepad = new GamepadEx(gamepad1);
        gamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        gamepad.getGamepadButton(GamepadKeys.Button.A).whenPressed(intakeSubsystem::intake);
        gamepad.getGamepadButton(GamepadKeys.Button.A).whenReleased(intakeSubsystem::stop);
        gamepad.getGamepadButton(GamepadKeys.Button.B).whileHeld(intakeSubsystem::outtake);
        gamepad.getGamepadButton(GamepadKeys.Button.B).whenPressed(intakeSubsystem::stop);
    }

    private void slowMode() {
        reductionFactor = (reductionFactor == 1.0) ? 0.5 : 1.0;
    }
}
