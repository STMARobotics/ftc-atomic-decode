package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.StartEndCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import org.firstinspires.ftc.teamcode.Commands.AutoLockTurretCommand;
import org.firstinspires.ftc.teamcode.Commands.NextPlatterCommand;
import org.firstinspires.ftc.teamcode.Commands.NotShootCommand;
import org.firstinspires.ftc.teamcode.Commands.Drive;
import org.firstinspires.ftc.teamcode.Commands.PrepareShootCommand;
import org.firstinspires.ftc.teamcode.Commands.ShootCommand;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

import java.util.function.BooleanSupplier;

@TeleOp(name="cool op mode")
public class CoolOpMode extends CommandOpMode {

    private DrivetrainSubsystem drivetrainSubsystem;
    private NotShootCommand notShootCommand;
    private AutoLockTurretCommand autoLockTurretCommand;
    private ShooterSubsystem shooterSubsystem;
    private TurretSubsystem turretSubsystem;
    private PlatterSubsystem platterSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private LimelightSubsystem limelightSubsystem;
    private LookupTable lookupTable;

    private GamepadEx gamepad;
    private TriggerReader leftTriggerReader;
    private TriggerReader rightTriggerReader;

    private double reductionFactor = 1.0;

    @Override
    public void initialize() {
        // Subsystems
        drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);
        shooterSubsystem    = new ShooterSubsystem(hardwareMap);
        turretSubsystem     = new TurretSubsystem(hardwareMap);
        platterSubsystem    = new PlatterSubsystem(hardwareMap);
        intakeSubsystem     = new IntakeSubsystem(hardwareMap);
        limelightSubsystem  = new LimelightSubsystem(hardwareMap);
        lookupTable         = new LookupTable();

        autoLockTurretCommand = new AutoLockTurretCommand(turretSubsystem);
        notShootCommand       = new NotShootCommand(platterSubsystem);

        // Gamepad + triggers
        gamepad = new GamepadEx(gamepad1);
        leftTriggerReader  = new TriggerReader(gamepad, GamepadKeys.Trigger.LEFT_TRIGGER);
        rightTriggerReader = new TriggerReader(gamepad, GamepadKeys.Trigger.RIGHT_TRIGGER);

        // Drive command
        Drive teleopDriveCommand = new Drive(
                drivetrainSubsystem,
                () -> -gamepad1.left_stick_y,
                () -> -gamepad1.left_stick_x,
                () -> -gamepad1.right_stick_x,
                () -> reductionFactor
        );

        // Telemetry
        RunCommand telemetryCommand = new RunCommand(() -> {
            drivetrainSubsystem.telemetrize(telemetry);
            telemetry.update();
        });
        schedule(telemetryCommand);

        // Register all subsystems
        register(
                drivetrainSubsystem,
                shooterSubsystem,
                turretSubsystem,
                platterSubsystem,
                intakeSubsystem,
                limelightSubsystem,
                lookupTable
        );

        // Default commands
        drivetrainSubsystem.setDefaultCommand(teleopDriveCommand);
//        platterSubsystem.setDefaultCommand(notShootCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {

        gamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        gamepad.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(this::slowMode);

        gamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(intakeSubsystem::outtake)
                .whenReleased(intakeSubsystem::stop);

        // Left trigger -> intake
        Trigger intakeTrigger = new Trigger(() -> {
            leftTriggerReader.readValue();
            return leftTriggerReader.isDown();
        });

        intakeTrigger
                .whileActiveContinuous(
                        new RunCommand(intakeSubsystem::intake, intakeSubsystem)
                                .alongWith(new NextPlatterCommand(platterSubsystem).andThen(
                                        new RunCommand(() -> platterSubsystem.spinPlatter(0.12), platterSubsystem)
                                                .withTimeout(125)
                                                .whenFinished(platterSubsystem::stopPlatter)
                                ))
                )
                .whenInactive(intakeSubsystem::stop);

        // Right trigger -> shoot
        Trigger shootTrigger = new Trigger(() -> {
//            rightTriggerReader.readValue();
//            return rightTriggerReader.isDown();
            return gamepad1.right_trigger > 0.5;
        });

        shootTrigger
                .whileActiveContinuous(
                        new ShootCommand(platterSubsystem, shooterSubsystem, turretSubsystem, lookupTable, limelightSubsystem, () -> gamepad1.right_trigger > 0.5)
                );
    }

    private void slowMode() {
        reductionFactor = (reductionFactor == 1.0) ? 0.5 : 1.0;
    }
}
