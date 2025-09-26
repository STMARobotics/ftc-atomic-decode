package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.ServoSubsystem;

@TeleOp
public class ServoTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        ServoSubsystem servoSubsystem = new ServoSubsystem(hardwareMap);
        final Servo servo = hardwareMap.get(Servo.class, "servo");
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            if (gamepad1.x){servo.setPosition(1);}
            if (gamepad1.y){servo.setPosition(0);}
        }
    }
}