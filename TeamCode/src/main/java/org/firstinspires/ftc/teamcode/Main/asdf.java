package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;

@TeleOp
public class asdf extends LinearOpMode {

    private double angle = 0.1;
    private double rpm = 3000;

    @Override
    public void runOpMode() {
        Servo hoodServo = hardwareMap.get(Servo.class, "hoodServo");
        ShooterSubsystem shooterSubsystem = new ShooterSubsystem(hardwareMap);
        LimelightSubsystem limelightSubsystem = new LimelightSubsystem(hardwareMap);
        PlatterSubsystem platterSubsystem = new PlatterSubsystem(hardwareMap);

        limelightSubsystem.pipelineSwitcher(0);

        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                rpm += 1;
                shooterSubsystem.setRPM(rpm);
            } else if (gamepad1.dpad_down) {
                rpm -= 1;
                shooterSubsystem.setRPM(rpm);
            }

            if (gamepad1.a) {
                angle -= 0.001;
                hoodServo.setPosition(angle);
            } else if (gamepad1.y) {
                angle += 0.001;
                hoodServo.setPosition(angle);
            }

            if (gamepad1.right_trigger >= 0.1) {
                platterSubsystem.launcherActivate();
                platterSubsystem.launchableActivate();
                platterSubsystem.spinPlatter(0.4);
            } else {
                platterSubsystem.stopPlatter();
                platterSubsystem.launcherDeactivate();
                platterSubsystem.launchableDeactivate();
            }

            if (gamepad1.dpad_left) {
                limelightSubsystem.pipelineSwitcher(0);
            } else if (gamepad1.dpad_right) {
                limelightSubsystem.pipelineSwitcher(1);
            }

            telemetry.addData("rpm", shooterSubsystem.getRPM());
            telemetry.addData("target rpm", rpm);
            telemetry.addData("hood angle", hoodServo.getPosition());
            telemetry.addData("target hood angle", angle);
            telemetry.addData("distance to target", limelightSubsystem.getDistance());
            telemetry.update();
        }
    }
}