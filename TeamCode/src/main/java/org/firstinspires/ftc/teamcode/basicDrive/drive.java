package org.firstinspires.ftc.teamcode.basicDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "drive", group = "linear Opmode")
public class drive extends LinearOpMode {
    // variables get put here

    @Override
    public void runOpMode() {
        // initialization section
        // this is where we tell the code that motors exist

        DcMotorEx left1 = hardwareMap.get(DcMotorEx.class, "left1");
        DcMotorEx left2 = hardwareMap.get(DcMotorEx.class, "left2");
        DcMotorEx right1 = hardwareMap.get(DcMotorEx.class, "right1");
        DcMotorEx right2 = hardwareMap.get(DcMotorEx.class, "right2");

        waitForStart();
        while (opModeIsActive()) {
            // this is where we put the actual instructions
            
            double y = -gamepad1.left_stick_y; // im not actually sure if this is supposed to be -
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;
            
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;
            
            left1.setPower(frontLeftPower);
            left2.setPower(backLeftPower);
            right1.setPower(frontRightPower);
            right2.setPower(backRightPower);
            // we will realistically do this all in a drive subsystem but you get the point
        }
    }
}
