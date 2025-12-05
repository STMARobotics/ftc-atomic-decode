package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Commands.AutoLockTurretCommand;
import org.firstinspires.ftc.teamcode.Subsystems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.LookupTable;
import org.firstinspires.ftc.teamcode.Subsystems.PlatterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.TurretSubsystem;

@TeleOp
public class asdf extends LinearOpMode {

    private Servo hoodServo;
    private ShooterSubsystem shooterSubsystem;
    private LimelightSubsystem limelightSubsystem;
    private PlatterSubsystem platterSubsystem;
    private AutoLockTurretCommand autoLockTurretCommand;
    private TurretSubsystem turretSubsystem;
    private LookupTable lookupTable;
    private double angle = 0.1;
    private double rpm = 3000;

    @Override
    public void runOpMode() {
        hoodServo = hardwareMap.get(Servo.class, "hoodServo");
        shooterSubsystem = new ShooterSubsystem(hardwareMap);
        limelightSubsystem = new LimelightSubsystem(hardwareMap);
        platterSubsystem = new PlatterSubsystem(hardwareMap);
        turretSubsystem = new TurretSubsystem(hardwareMap);
        lookupTable = new LookupTable();
        autoLockTurretCommand = new AutoLockTurretCommand(turretSubsystem, lookupTable, limelightSubsystem, shooterSubsystem);

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