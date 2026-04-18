package org.firstinspires.ftc.teamcode.Main;

import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.NORMAL_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.DriveConstants.SLOW_DRIVE_SCALE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.BLUE_PIPELINE;
import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.RED_PIPELINE;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
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

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

@TeleOp(name = "cool op mode")
public class CoolOpMode extends CommandOpMode {

    private enum Mode {
        DEMO,
        SHOWOFF
    }

    // Subsystems
    private DrivetrainSubsystem drivetrainSubsystem;
    private ShooterSubsystem shooterSubsystem;
    private TurretSubsystem turretSubsystem;
    private PlatterSubsystem platterSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private LimelightSubsystem limelightSubsystem;
    private LookupTableMath lookupTable;
    public BooleanSupplier isShowoffMode;

    private GamepadEx gamepad1Ex;
    private GamepadEx gamepad2Ex;
    private TriggerReader leftTriggerReader;
    private TriggerReader rightTriggerReader;

    private double driveScale = NORMAL_DRIVE_SCALE;
    private Mode mode = Mode.DEMO;

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

        Drive teleopDriveCommand = getDrive();

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
            telemetry.addData("Mode", mode.toString());
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

    @NonNull
    private Drive getDrive() {
        final BooleanSupplier c2Safety = () -> gamepad2.left_trigger >= 0.1;

        DoubleSupplier forwardSupplier = () -> {
            if (mode == Mode.SHOWOFF) {
                return -gamepad1.left_stick_y;
            }
            if (c2Safety.getAsBoolean()) {
                if (Math.abs(gamepad2.left_stick_y) > 0.05) return -gamepad2.left_stick_y;
                return -gamepad1.left_stick_y;
            }
            return 0.0;
        };

        DoubleSupplier strafeSupplier = () -> {
            if (mode == Mode.SHOWOFF) {
                return -gamepad1.left_stick_x;
            }
            if (c2Safety.getAsBoolean()) {
                if (Math.abs(gamepad2.left_stick_x) > 0.05) return -gamepad2.left_stick_x;
                return -gamepad1.left_stick_x;
            }
            return 0.0;
        };

        DoubleSupplier rotateSupplier = () -> {
            if (mode == Mode.SHOWOFF) {
                return -gamepad1.right_stick_x;
            }
            if (c2Safety.getAsBoolean()) {
                if (Math.abs(gamepad2.right_stick_x) > 0.05) return -gamepad2.right_stick_x;
                return -gamepad1.right_stick_x;
            }
            return 0.0;
        };

        Drive teleopDriveCommand = new Drive(
                drivetrainSubsystem,
                forwardSupplier,
                strafeSupplier,
                rotateSupplier,
                () -> driveScale
        );
        return teleopDriveCommand;
    }

    private void configureButtonBindings() {
        gamepad1Ex.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.BACK)
                .whenPressed(this::toggleMode);

        Trigger c2EnableForC1 = new Trigger(() -> mode == Mode.DEMO && gamepad2.left_trigger >= 0.1);

        c2EnableForC1.and(new Trigger(() -> gamepad1.right_bumper))
                .whileActiveContinuous(new IntakeCommand(platterSubsystem, intakeSubsystem));

        c2EnableForC1.and(new Trigger(() -> gamepad1.left_bumper))
                .whileActiveContinuous(new RunCommand(intakeSubsystem::outtake))
                .whenInactive(intakeSubsystem::stop);

        c2EnableForC1.and(new Trigger(() -> gamepad1.left_trigger >= 0.1))
                .whileActiveContinuous(new AutoLockTurretCommand(
                        turretSubsystem,
                        limelightSubsystem,
                        shooterSubsystem
                ))
                .whenInactive(new InstantCommand(turretSubsystem::hoodToHome, turretSubsystem));

        c2EnableForC1.and(new Trigger(() -> gamepad1.right_trigger >= 0.1))
                .whileActiveContinuous(new ShootCommand(platterSubsystem, shooterSubsystem, limelightSubsystem));

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(() -> { if (mode == Mode.DEMO) limelightSubsystem.pipelineSwitcher(BLUE_PIPELINE); });

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(() -> { if (mode == Mode.DEMO) limelightSubsystem.pipelineSwitcher(RED_PIPELINE); });

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(() -> { if (mode == Mode.DEMO) turretSubsystem.setTurretPower(0.2); })
                .whenReleased(() -> { if (mode == Mode.DEMO) turretSubsystem.setTurretPower(0); });

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(() -> { if (mode == Mode.DEMO) turretSubsystem.setTurretPower(-0.2); })
                .whenReleased(() -> { if (mode == Mode.DEMO) turretSubsystem.setTurretPower(0); });

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(() -> { if (mode == Mode.DEMO) shooterSubsystem.setRPM(4500); });

        gamepad2Ex.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(this::toggleDriveScale);

        Trigger showoffLeftBumper = new Trigger(() -> mode == Mode.SHOWOFF && gamepad1.left_bumper);
        showoffLeftBumper.whileActiveContinuous(new IntakeCommand(platterSubsystem, intakeSubsystem));

        Trigger showoffRightBumper = new Trigger(() -> mode == Mode.SHOWOFF && gamepad1.right_bumper);
        showoffRightBumper
                .whileActiveContinuous(new RunCommand(intakeSubsystem::outtake))
                .whenInactive(intakeSubsystem::stop);

        Trigger autoLockTrigger = new Trigger(() -> {
            leftTriggerReader.readValue();
            return mode == Mode.SHOWOFF && gamepad1.left_trigger >= 0.1;
        });

        autoLockTrigger
                .whileActiveContinuous(new AutoLockTurretCommand(
                        turretSubsystem,
                        limelightSubsystem,
                        shooterSubsystem
                ));

        autoLockTrigger
                .whenInactive(new InstantCommand(turretSubsystem::hoodToHome, turretSubsystem));

        Trigger shootTrigger = new Trigger(() -> {
            rightTriggerReader.readValue();
            return mode == Mode.SHOWOFF && gamepad1.right_trigger >= 0.1;
        });

        shootTrigger
                .whileActiveContinuous(new ShootCommand(platterSubsystem, shooterSubsystem, limelightSubsystem));

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(() -> { if (mode == Mode.SHOWOFF) limelightSubsystem.pipelineSwitcher(BLUE_PIPELINE); });

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(() -> { if (mode == Mode.SHOWOFF) limelightSubsystem.pipelineSwitcher(RED_PIPELINE); });

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(() -> { if (mode == Mode.SHOWOFF) turretSubsystem.setTurretPower(0.2); })
                .whenReleased(() -> { if (mode == Mode.SHOWOFF) turretSubsystem.setTurretPower(0); });

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(() -> { if (mode == Mode.SHOWOFF) turretSubsystem.setTurretPower(-0.2); })
                .whenReleased(() -> { if (mode == Mode.SHOWOFF) turretSubsystem.setTurretPower(0); });

        gamepad1Ex.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(() -> { if (mode == Mode.SHOWOFF) shooterSubsystem.setRPM(4500); });
    }

    private void toggleDriveScale() {
        driveScale = (driveScale == NORMAL_DRIVE_SCALE) ? SLOW_DRIVE_SCALE : NORMAL_DRIVE_SCALE;
    }

    private void toggleMode() {
        mode = (mode == Mode.DEMO) ? Mode.SHOWOFF : Mode.DEMO;
    }
}
