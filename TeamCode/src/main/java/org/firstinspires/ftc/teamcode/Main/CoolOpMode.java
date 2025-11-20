package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RepeatCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import org.firstinspires.ftc.teamcode.Commands.AutoLockTurretCommand;
import org.firstinspires.ftc.teamcode.Commands.CleanupCommand;
import org.firstinspires.ftc.teamcode.Commands.Drive;
import org.firstinspires.ftc.teamcode.Commands.FindColorCommand;
import org.firstinspires.ftc.teamcode.Commands.NextPlatterCommand;
import org.firstinspires.ftc.teamcode.Commands.NotShootCommand;
import org.firstinspires.ftc.teamcode.Commands.ShootCommand;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Constants.ArtifactColor;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.NORMAL_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.SLOW_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.BLUE_PIPELINE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.RED_PIPELINE;

@TeleOp(name = "cool op mode")
public class CoolOpMode extends CommandOpMode {

    // Subsystems
    private DrivetrainSubsystem drivetrainSubsystem;
    private ShooterSubsystem shooterSubsystem;
    private TurretSubsystem turretSubsystem;
    private PlatterSubsystem platterSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private LimelightSubsystem limelightSubsystem;
    private LookupTable lookupTable;

    // Commands
    private NotShootCommand notShootCommand;
    private AutoLockTurretCommand autoLockTurretCommand;
    private CleanupCommand cleanupCommand;

    private GamepadEx gamepad;
    private TriggerReader leftTriggerReader;
    private TriggerReader rightTriggerReader;

    private double driveScale = NORMAL_DRIVE_SCALE;

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

        // Commands
        autoLockTurretCommand = new AutoLockTurretCommand(
                turretSubsystem,
                lookupTable,
                limelightSubsystem,
                shooterSubsystem
        );
        notShootCommand = new NotShootCommand(platterSubsystem, shooterSubsystem);
        cleanupCommand  = new CleanupCommand(turretSubsystem, platterSubsystem, shooterSubsystem);

        // Gamepad
        gamepad = new GamepadEx(gamepad1);
        leftTriggerReader  = new TriggerReader(gamepad, GamepadKeys.Trigger.LEFT_TRIGGER);
        rightTriggerReader = new TriggerReader(gamepad, GamepadKeys.Trigger.RIGHT_TRIGGER);

        // Drive command
        Drive teleopDriveCommand = new Drive(
                drivetrainSubsystem,
                () -> -gamepad1.left_stick_y,
                () -> -gamepad1.left_stick_x,
                () -> -gamepad1.right_stick_x,
                () -> driveScale
        );

        // Telemetry
        RunCommand telemetryCommand = new RunCommand(() -> {
            drivetrainSubsystem.telemetrize(telemetry);
            turretSubsystem.telemetrize(telemetry);
            telemetry.update();
        });
        schedule(telemetryCommand);

        // Register subsystems
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
        platterSubsystem.setDefaultCommand(notShootCommand);
        turretSubsystem.setDefaultCommand(autoLockTurretCommand);

        configureButtonBindings();
    }

    private Command makeShot(ArtifactColor color) {
        return new RepeatCommand(
                new FindColorCommand(platterSubsystem, color)
                        .andThen(new ShootCommand(
                                platterSubsystem,
                                shooterSubsystem,
                                lookupTable,
                                limelightSubsystem,
                                color
                        ))
                        .alongWith(new AutoLockTurretCommand(
                                turretSubsystem,
                                lookupTable,
                                limelightSubsystem,
                                shooterSubsystem
                        ))
        ).andThen(new CleanupCommand(
                turretSubsystem,
                platterSubsystem,
                shooterSubsystem
        ));
    }

    private void configureButtonBindings() {
        // Reset field-centric
        gamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        // Intake reverse
        gamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(intakeSubsystem::outtake)
                .whenReleased(intakeSubsystem::stop);

        // -------- Intake Cycle (LT) --------
        Trigger intakeTrigger = new Trigger(() -> {
            leftTriggerReader.readValue();
            return leftTriggerReader.isDown();
        });

        intakeTrigger
                .whileActiveContinuous(
                        new RunCommand(intakeSubsystem::intake, intakeSubsystem)
                                .alongWith(new NextPlatterCommand(platterSubsystem))
                )
                .whenInactive(intakeSubsystem::stop);

        // -------- Shoot Cycle --------
        Trigger shootTrigger = new Trigger(() -> {
            rightTriggerReader.readValue();
            return rightTriggerReader.isDown();
        });

        // RT = ALL
        shootTrigger
                .whileActiveContinuous(
                        makeShot(ArtifactColor.ALL)
                );

        // LL pipeline selection
        gamepad.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(() -> limelightSubsystem.pipelineSwitcher(BLUE_PIPELINE));

        gamepad.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(() -> limelightSubsystem.pipelineSwitcher(RED_PIPELINE));
    }

    private void toggleDriveScale() {
        driveScale = (driveScale == NORMAL_DRIVE_SCALE) ? SLOW_DRIVE_SCALE : NORMAL_DRIVE_SCALE;
    }
}
