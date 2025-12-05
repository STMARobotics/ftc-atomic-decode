package org.firstinspires.ftc.teamcode.Main;

import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.NORMAL_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.SLOW_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.BLUE_PIPELINE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.RED_PIPELINE;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import org.firstinspires.ftc.teamcode.Commands.AutoLockTurretCommand;
import org.firstinspires.ftc.teamcode.Commands.Drive;
import org.firstinspires.ftc.teamcode.Commands.IntakeCommand;
import org.firstinspires.ftc.teamcode.Commands.NotShootCommand;
import org.firstinspires.ftc.teamcode.Commands.ShootCommand;
import org.firstinspires.ftc.teamcode.Math.LookupTableMath;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

@TeleOp(name = "cool op mode")
public class CoolOpMode extends CommandOpMode {

    // Subsystems
    private DrivetrainSubsystem drivetrainSubsystem;
    private ShooterSubsystem shooterSubsystem;
    private TurretSubsystem turretSubsystem;
    private PlatterSubsystem platterSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private LimelightSubsystem limelightSubsystem;
    private LookupTableMath lookupTable;

    private GamepadEx gamepad;
    private TriggerReader leftTriggerReader;
    private TriggerReader rightTriggerReader;

    private double driveScale = NORMAL_DRIVE_SCALE;

    @Override
    public void initialize() {
        drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);
        shooterSubsystem    = new ShooterSubsystem(hardwareMap);
        turretSubsystem     = new TurretSubsystem(hardwareMap);
        platterSubsystem    = new PlatterSubsystem(hardwareMap);
        intakeSubsystem     = new IntakeSubsystem(hardwareMap);
        limelightSubsystem  = new LimelightSubsystem(hardwareMap);

        NotShootCommand notShootCommand = new NotShootCommand(platterSubsystem, shooterSubsystem);

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
            telemetry.addData("|-----|Drivetrain Telemetry|-----|", "");
            drivetrainSubsystem.telemetrize(telemetry);
            telemetry.addData("", "");
            telemetry.addData("|-----|Turret Telemetry|-----|", "");
            turretSubsystem.telemetrize(telemetry);
            telemetry.addData("distance to target", limelightSubsystem.getDistance());
            telemetry.addData("", "");
            telemetry.addData("|-----|Shooter Telemetry|-----|", "");
            shooterSubsystem.telemetrize(telemetry);
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
                limelightSubsystem
        );

        // Default commands
        drivetrainSubsystem.setDefaultCommand(teleopDriveCommand);
        platterSubsystem.setDefaultCommand(notShootCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // Reset field-centric
        gamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        // Intake forward
        gamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whileActiveContinuous(new IntakeCommand(platterSubsystem, intakeSubsystem));

        // Intake reverse
        gamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenHeld(new RunCommand(intakeSubsystem::outtake))
                .whenReleased(intakeSubsystem::stop);

        // -------- Auto Lock Cycle (LT) --------
        Trigger autoLockTrigger = new Trigger(() -> {
            leftTriggerReader.readValue();
            return gamepad1.left_trigger >= 0.1;
        });

        // LT = AUTO-LOCK
        autoLockTrigger
                .whileActiveContinuous(
                        new AutoLockTurretCommand(
                                turretSubsystem,
                                limelightSubsystem,
                                shooterSubsystem
                        ));

        // -------- Shoot Cycle --------
        Trigger shootTrigger = new Trigger(() -> {
            rightTriggerReader.readValue();
            return gamepad1.right_trigger >= 0.1;
        });

        // RT = ALL
        shootTrigger
                .whileActiveContinuous(
                        new ShootCommand(
                                platterSubsystem
                        ));

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
