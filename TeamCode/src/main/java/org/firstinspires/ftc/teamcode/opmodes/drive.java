package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "basic thingy idk", group = "linear Opmode")
public class drive extends LinearOpMode { // Changed class name to Drive
    // variables get put here

    @Override
    public void runOpMode() {
        // initialization section
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            // do stuff here i guess
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
