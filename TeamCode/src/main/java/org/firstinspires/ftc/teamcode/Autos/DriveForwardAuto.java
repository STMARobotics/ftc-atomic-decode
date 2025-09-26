package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Subsystems.AutoDriveSubsystem;

@Autonomous(name = "Auto: Drive Forward 12 Inches", group = "Linear OpMode")
public class DriveForwardAuto extends LinearOpMode {

    private AutoDriveSubsystem drive;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // Initialize the drivetrain subsystem
        // Pass 'this' as the opMode argument to enable telemetry and opModeIsActive() checks in the subsystem
        drive = new AutoDriveSubsystem(hardwareMap, this);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("-", "Make sure WHEEL_DIAMETER_INCHES (%.2f) is correct in AutoDriveSubsystem!", AutoDriveSubsystem.WHEEL_DIAMETER_INCHES);
        telemetry.addData("-", "Press Play to start.");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if (opModeIsActive()) {
            telemetry.addData("Status", "Driving forward...");
            telemetry.update();

            // Drive forward 12 inches at 0.5 power
            // The driveInches method in AutoDriveSubsystem is currently robot-centric
            drive.driveInches(12.0, 0.5);

            telemetry.addData("Status", "Movement Complete");
            telemetry.update();

            // You can add more autonomous steps here

            sleep(1000); // Keep the message on screen for a bit
        }
    }
}
