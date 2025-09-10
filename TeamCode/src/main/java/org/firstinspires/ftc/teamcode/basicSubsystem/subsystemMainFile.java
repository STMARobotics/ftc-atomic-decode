package org.firstinspires.ftc.teamcode.basicSubsystem;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp()
public class subsystemMainFile extends LinearOpMode {
    // variables get put here

    @Override
    public void runOpMode() {
        // initialization section
        // this is where we tell the code that motors exist

        subsystem subsystem = new subsystem((Servo) hardwareMap); // more configuration

        waitForStart();
        while (opModeIsActive()) {
            // this is where we put the actual instructions

            subsystem.setServoPosition(26.530); // now we can do long code in one line
        }
    }
}
