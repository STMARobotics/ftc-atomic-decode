AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
# Folder layout (paste these into TeamCode)

```
TeamCode/
└─ src/main/java/org/firstinspires/ftc/teamcode/
   ├─ opmodes/
   │  ├─ ShowcaseTemplate.java        # shows inputs + telemetry; starter TeleOp
   │  ├─ TeleOp_Mecanum.java          # full mecanum drive TeleOp
   │  └─ Auto_Bump.java               # simplest autonomous (time-based)
   └─ learn/
      └─ LearningSkeleton.java        # "boxed" sections: imports, vars, init, loop
```

---

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
// ║ These lines pull in FTC SDK classes so we can use them.             ║
// ╚════════════════════════════════════════════════════════════════════╝
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="LearningSkeleton", group="learn")
public class LearningSkeleton extends LinearOpMode {

    // ╔══════════════════════ CONFIG / VARIABLES ═══════════════════════╗
    // ║ "Variables" are labeled boxes that hold values we can change.  ║
    // ║ We'll declare our motors here and a slow-mode toggle.           ║
    // ╚═════════════════════════════════════════════════════════════════╝
    DcMotor leftFront, leftRear, rightFront, rightRear;  // Motors
    boolean slowMode = false;                            // On/Off switch

    @Override
    public void runOpMode() {
        // ╔════════════════════════════ INITIALIZATION ═══════════════════════════╗
        // ║ Runs ONCE before you press ▶ (Start).                                 ║
        // ║ - Map hardware by names from the Robot Configuration screen           ║
        // ║ - Set motor directions & behaviors                                   ║
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
        // ║ Repeats MANY times per second AFTER you press ▶ until you stop.      ║
        // ║ - Read gamepad                                                      ║
        // ║ - Compute powers                                                    ║
        // ║ - Send powers to motors                                             ║
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

---

// File: org/firstinspires/ftc/teamcode/opmodes/ShowcaseTemplate.java
// Purpose: Minimal TeleOp that prints inputs + demonstrates toggles.

package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="ShowcaseTemplate", group="learn")
public class ShowcaseTemplate extends LinearOpMode {
    boolean slowMode = false;

    @Override
    public void runOpMode() {
        DcMotor lf = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor lr = hardwareMap.get(DcMotor.class, "leftRear");
        DcMotor rf = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rr = hardwareMap.get(DcMotor.class, "rightRear");
        rf.setDirection(DcMotor.Direction.REVERSE);
        rr.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotor m : new DcMotor[]{lf,lr,rf,rr}) m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Ready. Press ▶");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double forward = -gamepad1.left_stick_y;
            double strafe  =  gamepad1.left_stick_x;
            double turn    =  gamepad1.right_stick_x;

            if (gamepad1.a) slowMode = true;
            if (gamepad1.b) slowMode = false;
            double k = slowMode ? 0.4 : 1.0;

            // Just display data for learning; motors off
            lf.setPower(0); lr.setPower(0); rf.setPower(0); rr.setPower(0);

            telemetry.addData("forward", "%.2f", forward);
            telemetry.addData("strafe",  "%.2f", strafe);
            telemetry.addData("turn",    "%.2f", turn);
            telemetry.addData("slowMode", slowMode);
            telemetry.update();
        }
    }
}

---

// File: org/firstinspires/ftc/teamcode/opmodes/TeleOp_Mecanum.java
// Purpose: Full mecanum driver control (forward/strafe/turn) with slow mode.

package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="TeleOp_Mecanum", group="drive")
public class TeleOp_Mecanum extends LinearOpMode {
    DcMotor lf, lr, rf, rr;
    boolean slow = false;

    @Override
    public void runOpMode() {
        lf = hardwareMap.get(DcMotor.class, "leftFront");
        lr = hardwareMap.get(DcMotor.class, "leftRear");
        rf = hardwareMap.get(DcMotor.class, "rightFront");
        rr = hardwareMap.get(DcMotor.class, "rightRear");

        rf.setDirection(DcMotor.Direction.REVERSE);
        rr.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotor m : new DcMotor[]{lf,lr,rf,rr}) m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("TeleOp_Mecanum ready. Press ▶");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // forward/back
            double x =  gamepad1.left_stick_x; // strafe
            double rx=  gamepad1.right_stick_x;// turn

            if (gamepad1.a) slow = true;
            if (gamepad1.b) slow = false;
            double scale = slow ? 0.45 : 1.0;

            double pLF = y + x + rx;
            double pLR = y - x + rx;
            double pRF = y - x - rx;
            double pRR = y + x - rx;

            double max = Math.max(1.0, Math.max(Math.abs(pLF), Math.max(Math.abs(pLR), Math.max(Math.abs(pRF), Math.abs(pRR)))));
            lf.setPower((pLF/max)*scale);
            lr.setPower((pLR/max)*scale);
            rf.setPower((pRF/max)*scale);
            rr.setPower((pRR/max)*scale);

            telemetry.addData("slow", slow);
            telemetry.update();
        }
    }
}

---

// File: org/firstinspires/ftc/teamcode/opmodes/Auto_Bump.java
// Purpose: Time-based autonomous. Drives forward 1s, then stops.

package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="Auto_Bump", group="learn")
public class Auto_Bump extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor lf = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor lr = hardwareMap.get(DcMotor.class, "leftRear");
        DcMotor rf = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rr = hardwareMap.get(DcMotor.class, "rightRear");
        rf.setDirection(DcMotor.Direction.REVERSE);
        rr.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotor m : new DcMotor[]{lf,lr,rf,rr}) m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Auto_Bump ready. Press ▶");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            lf.setPower(0.4); lr.setPower(0.4); rf.setPower(0.4); rr.setPower(0.4);
            sleep(1000); // 1 second forward
            lf.setPower(0); lr.setPower(0); rf.setPower(0); rr.setPower(0);
        }
    }
}

---

# Quick mission cards (printable)

- **Mission 1: See Inputs** — Run `ShowcaseTemplate`. Move sticks & press A/B. ✅ when telemetry shows values and A→B toggles slow mode.
- **Mission 2: Drive Tank/Arcade** — Swap in `TeleOp_Mecanum` and test forward/turn. ✅ when it drives straight and turns.
- **Mission 3: Strafe** — In `TeleOp_Mecanum`, verify sideways motion is clean. ✅ when you can strafe without drifting far.
- **Mission 4: Bump Auto** — Run `Auto_Bump`. ✅ when the bot nudges forward ~1s and stops.

# Notes
- Make sure Robot Configuration motor names exactly match: `leftFront`, `leftRear`, `rightFront`, `rightRear`.
- Wheels: mecanum rollers should make an "X" when viewed from above.
