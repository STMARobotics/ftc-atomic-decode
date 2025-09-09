// File: org/firstinspires/ftc/teamcode/learn/LearningSkeleton.java
// Purpose: Clear, boxed sections labeling where everything goes.

package org.firstinspires.ftc.teamcode.learn;

/*
 ╔══════════════════════════════════════════════════════════════════════╗
 ║  LearningSkeleton: Read me top-to-bottom.                            ║
 ║  - Each boxed section is where you add code.                         ║
 ║  - Build & Run as a TeleOp on the Driver Station.                    ║
 ╚══════════════════════════════════════════════════════════════════════╝
*/

// ╔════════════════════════════ IMPORTS ═══════════════════════════════╗
// ║ These lines pull in FTC SDK classes so we can use them.            ║
// ╚════════════════════════════════════════════════════════════════════╝
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="LearningSkeleton", group="learn")
public class LearningSkeleton extends LinearOpMode {

    // ╔══════════════════════ CONFIG / VARIABLES ═══════════════════════╗
    // ║ "Variables" are labeled boxes that hold values we can change.   ║
    // ║ We'll declare our motors here and a slow-mode toggle.           ║
    // ╚═════════════════════════════════════════════════════════════════╝
    DcMotor leftFront, leftRear, rightFront, rightRear;  // Motors
    boolean slowMode = false;                            // On/Off switch

    @Override
    public void runOpMode() {
        // ╔════════════════════════════ INITIALIZATION ═══════════════════════════╗
        // ║ Runs ONCE before you press ▶ (Start).                                 ║
        // ║ - Map hardware by names from the Robot Configuration screen           ║
        // ║ - Set motor directions & behaviors                                    ║
        // ╚═══════════════════════════════════════════════════════════════════════╝
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftRear   = hardwareMap.get(DcMotor.class, "leftRear");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightRear  = hardwareMap.get(DcMotor.class, "rightRear");

        // Right side reversed so forward goes straight
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightRear.setDirection(DcMotor.Direction.REVERSE);

        for (DcMotor m : new DcMotor[]{leftFront,leftRear,rightFront,rightRear})
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("LearningSkeleton ready. Press ▶ to start.");
        telemetry.update();
        waitForStart();

        // ╔════════════════════════════════ WHILE LOOP ═══════════════════════════╗
        // ║ Repeats MANY times per second AFTER you press ▶ until you stop.       ║
        // ║ - Read gamepad                                                        ║
        // ║ - Compute powers                                                      ║
        // ║ - Send powers to motors                                               ║
        // ╚═══════════════════════════════════════════════════════════════════════╝
        while (opModeIsActive()) {
            // Read inputs
            double forward = -gamepad1.left_stick_y; // up is negative
            double strafe  =  gamepad1.left_stick_x;
            double turn    =  gamepad1.right_stick_x;

            // Toggle slow mode (A on, B off)
            if (gamepad1.a) slowMode = true;
            if (gamepad1.b) slowMode = false;
            double scale = slowMode ? 0.4 : 1.0;

            // Mecanum mixing
            double pLF = forward + strafe + turn;
            double pLR = forward - strafe + turn;
            double pRF = forward - strafe - turn;
            double pRR = forward + strafe - turn;

            // Normalize so no wheel exceeds |1|
            double max = Math.max(1.0, Math.max(Math.abs(pLF), Math.max(Math.abs(pLR), Math.max(Math.abs(pRF), Math.abs(pRR)))));
            pLF/=max; pLR/=max; pRF/=max; pRR/=max;

            // Apply slow mode
            pLF*=scale; pLR*=scale; pRF*=scale; pRR*=scale;

            // Set powers
            leftFront.setPower(pLF);
            leftRear.setPower(pLR);
            rightFront.setPower(pRF);
            rightRear.setPower(pRR);

            // Show what's happening
            telemetry.addData("forward", "%.2f", forward);
            telemetry.addData("strafe",  "%.2f", strafe);
            telemetry.addData("turn",    "%.2f", turn);
            telemetry.addData("slowMode", slowMode);
            telemetry.update();
        }
    }
}
