package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Commands.IntakeCommand;
import org.firstinspires.ftc.teamcode.Commands.TurretCommand;
import org.firstinspires.ftc.teamcode.Subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.HoodSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ServoSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

// I copied andy so merek can sleep at night

@TeleOp(name="cool op mode", group = "cool things")
public class CoolOpMode extends CommandOpMode {

    private DrivetrainSubsystem drivetrainSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private ServoSubsystem servoSubsystem;
    private TurretSubsystem turretSubsystem;
    private HoodSubsystem hoodSubsystem;
    
    private double reductionFactor = 1;

    @Override
    public void initialize() {
        // Create subsystems
        drivetrainSubsystem = new DrivetrainSubsystem(hardwareMap);
        intakeSubsystem = new IntakeSubsystem(hardwareMap);
        servoSubsystem = new ServoSubsystem(hardwareMap);
        turretSubsystem = new TurretSubsystem(hardwareMap, telemetry);
        hoodSubsystem = new HoodSubsystem(hardwareMap);

        /*
        The origin is the field perimeter corner by the red loading zone.
        We'll drive from the perspective of the red alliance:
         - Pushing up on the left stick moves toward the blue alliance wall, which is positive X.
         - Pushing to the left on the left stick moves toward the obelisk wall, which is positive Y.
         - Pushing to the left on the right stick rotates the the positive direction,
         counterclockwise
         */
        RunCommand teleopDriveCommand = new RunCommand(() -> drivetrainSubsystem.drive(
                -gamepad1.left_stick_y, // Stick up is negative but moves +X, so invert
                -gamepad1.left_stick_x, // Stick left is negative but moves +Y, so invert
                -gamepad1.right_stick_x, // Stick left is negative but moves +rotation, so invert
                reductionFactor),
                drivetrainSubsystem);

        RunCommand telemetryCommand = new RunCommand(() -> {
            drivetrainSubsystem.telemetrize(telemetry);
            telemetry.update();
        });

        // Schedule commands
        schedule(telemetryCommand);

        // Register subsystems
        register(drivetrainSubsystem, intakeSubsystem, servoSubsystem, turretSubsystem);

        // Set default commands for subsystems
        drivetrainSubsystem.setDefaultCommand(teleopDriveCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // Bind driver buttons
        GamepadEx driverGamepad = new GamepadEx(gamepad1);
        GamepadEx operatorGamepad = new GamepadEx(gamepad2);
        driverGamepad.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(drivetrainSubsystem::resetLocalization);



        // Right Bumper: When pressed, start intaking
        driverGamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(new IntakeCommand(intakeSubsystem, IntakeSubsystem.IntakeState.INTAKING));

        // Right Bumper: When released, stop the intake
        driverGamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenReleased(new IntakeCommand(intakeSubsystem, IntakeSubsystem.IntakeState.STOPPED));



        // Left Bumper: When pressed, start outtaking
        driverGamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new IntakeCommand(intakeSubsystem, IntakeSubsystem.IntakeState.OUTTAKING));

        // Left Bumper: When released, stop outtaking
        driverGamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenReleased(new IntakeCommand(intakeSubsystem, IntakeSubsystem.IntakeState.STOPPED));



        // A: When pressed start shooting
        operatorGamepad.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new TurretCommand(turretSubsystem, TurretSubsystem.TurretState.FAR));

        // A: When released, stop shooting
        operatorGamepad.getGamepadButton(GamepadKeys.Button.A)
                .whenReleased(new TurretCommand(turretSubsystem, TurretSubsystem.TurretState.STOPPED));



        operatorGamepad.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(new TurretCommand(turretSubsystem, TurretSubsystem.TurretState.CLOSE));
        operatorGamepad.getGamepadButton(GamepadKeys.Button.B)
                .whenReleased(new TurretCommand(turretSubsystem, TurretSubsystem.TurretState.STOPPED));


        //Left and right bumpers make hood go up and down
        operatorGamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(hoodSubsystem::hoodAngleIncrease);
        operatorGamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenReleased(hoodSubsystem::hoodStop);
        operatorGamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(hoodSubsystem::hoodAngleDecrease);
        operatorGamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenReleased(hoodSubsystem::hoodStop);

        // RS button: When pressed, set speed to half
        driverGamepad.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
                .whenPressed(this::slowMode);
    }

    private void slowMode() {
        reductionFactor = (reductionFactor == 1.0) ? 0.5 : 1.0;
    }
}
