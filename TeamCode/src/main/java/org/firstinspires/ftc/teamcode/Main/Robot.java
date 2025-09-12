package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

@TeleOp(name = "Fc-drive", group = "Drive")
public class Robot extends LinearOpMode {

    @Override
    public void runOpMode() {
        DriveSubsystem drive = new DriveSubsystem(this);
        TurretSubsystem turretSubsystem = new TurretSubsystem(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        boolean slowModeActive = false;
        boolean aButtonPreviouslyPressed = false;

        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {

            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            boolean aButtonPressedCurrently = gamepad1.a;

            // Toggle slow mode when A is pressed
            if (aButtonPressedCurrently && !aButtonPreviouslyPressed) {
                slowModeActive = !slowModeActive;
            }
            aButtonPreviouslyPressed = aButtonPressedCurrently;

            double speedMultiplier;
            if (slowModeActive) {
                speedMultiplier = 0.5;
            } else {
                speedMultiplier = 1.0;
            }

            if (gamepad1.y) {drive.resetIMU();}
            
            drive.drive(y, x, rx, speedMultiplier);

            telemetry.addData("Status", "Run Time: " + getRuntime());
            telemetry.addData("heading", drive.getHeading());
            telemetry.addData("Slow Mode", slowModeActive);
            telemetry.update();
        }
    }
}
