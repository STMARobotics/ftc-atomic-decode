package org.firstinspires.ftc.teamcode.vision;

import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.BLACK;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.GREEN;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.VIOLET;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RepeatCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.led.LedSubsystem;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

/**
 * Command to detect the AprilTag from the Obelisk and blink the LEDs to indicate the Motif.
 */
public class MotifIndicatorCommand extends CommandBase {

    private final LedSubsystem ledSubsystem;
    private final AprilTagSubsystem aprilTagSubsystem;
    private final Telemetry telemetry;

    private int lastTagId = 0;
    private Command ledCommand;

    public MotifIndicatorCommand(
            LedSubsystem ledSubsystem, AprilTagSubsystem aprilTagSubsystem, Telemetry telemetry) {
        this.aprilTagSubsystem = aprilTagSubsystem;
        this.ledSubsystem = ledSubsystem;
        this.telemetry = telemetry;

        addRequirements(ledSubsystem, aprilTagSubsystem);
    }

    @Override
    public void initialize() {
        // Turn the LEDs off
        ledSubsystem.setLedPattern(BLACK);
    }

    @Override
    public void execute() {
        // Get the detections
        List<AprilTagDetection> detections = aprilTagSubsystem.getDetections();
        AprilTagSubsystem.telemetryDetections(telemetry, detections);

        // Check for detection of a new Obelisk tag
        int newTagId = lastTagId;
        for (AprilTagDetection detection : detections) {
            if (detection.id >= 21 && detection.id <= 23) {
                // Found an Obelisk tag
                newTagId = detection.id;
                break;
            }
        }

        if (newTagId != lastTagId) {
            // New Obelisk tag detected
            if (ledCommand != null) {
                // Cancel the previous Motif comment
                ledCommand.cancel();
            }
            // Create and schedule a command to blink the LEDs for the new Motif
            switch (newTagId) {
                case 21:
                    ledCommand = makeLedCommand(GREEN, VIOLET, VIOLET);
                    break;
                case 22:
                    ledCommand = makeLedCommand(VIOLET, GREEN, VIOLET);
                    break;
                case 23:
                    ledCommand = makeLedCommand(VIOLET, VIOLET, GREEN);
                    break;
            }
            ledCommand = new RepeatCommand(ledCommand.andThen(new WaitCommand(1000)));
            ledCommand.schedule();
        }
        lastTagId = newTagId;
    }

    /**
     * Makes a command group that will blink the LEDs red, then blink the specified pattern
     * @param pattern pattern to blink (should be Motif color pattern)
     * @return command group to blink the LEDs
     */
    private Command makeLedCommand(BlinkinPattern... pattern) {
        // Start by blinking RED
        Command result = new InstantCommand(() -> ledSubsystem.setLedPattern(BlinkinPattern.RED))
                .andThen(new WaitCommand(500));

        // For each pattern color, turn the LEDs off briefly and then blink the color
        for (BlinkinPattern p : pattern) {
            Command patternCommand = new InstantCommand(() -> ledSubsystem.setLedPattern(BLACK))
                            .andThen(new WaitCommand(500))
                    .andThen(new InstantCommand(() -> ledSubsystem.setLedPattern(p)))
                    .andThen(new WaitCommand(500));
            result = result.andThen(patternCommand);
        }
        // Turn the LEDs off at the end of the sequence
        return result.andThen(new InstantCommand(() -> ledSubsystem.setLedPattern(BLACK)));
    }

    @Override
    public void end(boolean interrupted) {
        // Cancel any active Motif command
        if (ledCommand != null) {
            ledCommand.cancel();
        }
        // Turn the LEDs off
        ledSubsystem.setLedPattern(BLACK);
    }
}
