package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;

@TeleOp(name="test systems", group = "testing")
public class TestSystems extends CommandOpMode {

    private PlatterSubsystem platterSubsystem;

    @Override
    public void initialize() {
        // Create subsystems
        platterSubsystem = new PlatterSubsystem(hardwareMap);

        register(platterSubsystem);

        configureButtonBindings();
    }

    private void configureButtonBindings() {

        // Bind driver buttons
        GamepadEx gamepad = new GamepadEx(gamepad1);

        gamepad.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(platterSubsystem::launcherActivate);

        gamepad.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(platterSubsystem::launcherDeactivate);

    }
}
