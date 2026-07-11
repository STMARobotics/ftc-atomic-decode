package org.firstinspires.ftc.teamcode.Main;

import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.NORMAL_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.SLOW_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.BLUE_PIPELINE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.RED_PIPELINE;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
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

    private GamepadEx gamepad1Ex;
    private GamepadEx gamepad2Ex;
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

        // Gamepads
        gamepad1Ex = new GamepadEx(gamepad1);
        gamepad2Ex = new GamepadEx(gamepad2);
        leftTriggerReader  = new TriggerReader(gamepad1Ex, GamepadKeys.Trigger.LEFT_TRIGGER);
        rightTriggerReader = new TriggerReader(gamepad1Ex, GamepadKeys.Trigger.RIGHT_TRIGGER);

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
            telemetry.addData("|-----|Platter Telemetry|-----|", "");
            platterSubsystem.telemetrize(telemetry);
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
        gamepad1Ex.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        Trigger c1Enable = new Trigger(() -> gamepad1.left_trigger >= 0.1);

        c1Enable.and(new Trigger(() -> gamepad2.left_trigger >= 0.1))
                .whileActiveContinuous(new IntakeCommand(platterSubsystem, intakeSubsystem));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whileActiveContinuous(new RunCommand(intakeSubsystem::outtake))
                .whenInactive(intakeSubsystem::stop);

        c1Enable.and(new Trigger(() -> gamepad2.right_trigger >= 0.1))
                .whileActiveContinuous(
                        new ParallelCommandGroup(
                                new AutoLockTurretCommand(turretSubsystem,
                                        limelightSubsystem,
                                        shooterSubsystem),
                                new ShootCommand(platterSubsystem,
                                        shooterSubsystem,
                                        limelightSubsystem)))
                .whenInactive(
                        new InstantCommand(turretSubsystem::hoodToHome, turretSubsystem));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(() -> limelightSubsystem.pipelineSwitcher(BLUE_PIPELINE));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(() -> limelightSubsystem.pipelineSwitcher(RED_PIPELINE));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.X)
                .whileActiveContinuous(() -> turretSubsystem.setTurretPower(0.2));
        gamepad1Ex.getGamepadButton(GamepadKeys.Button.X)
                .whenReleased(() -> turretSubsystem.setTurretPower(0.2));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.B)
                .whileActiveContinuous(() -> turretSubsystem.setTurretPower(-0.2));
        gamepad1Ex.getGamepadButton(GamepadKeys.Button.B)
                .whenReleased(() -> turretSubsystem.setTurretPower(0.0));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.A)
                .whileActiveContinuous(() -> shooterSubsystem.setRPM(4500));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(this::toggleDriveScale);
    }

    private void toggleDriveScale() {
        driveScale = (driveScale == NORMAL_DRIVE_SCALE) ? SLOW_DRIVE_SCALE : NORMAL_DRIVE_SCALE;
    }
}