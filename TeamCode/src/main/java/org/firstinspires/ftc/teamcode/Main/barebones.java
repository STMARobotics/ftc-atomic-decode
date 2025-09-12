package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="barebones", group="Linear Opmode")
public class barebones extends LinearOpMode {
    private DcMotor left1;
    private DcMotor left2;
    private DcMotor right1;
    private DcMotor right2;

    @Override
    public void runOpMode() throws InterruptedException {
        left1 = hardwareMap.dcMotor.get("left1");
        left2 = hardwareMap.dcMotor.get("left2");
        right1 = hardwareMap.dcMotor.get("right1");
        right2 = hardwareMap.dcMotor.get("right2");

        left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double left1power = (y + x + rx) / denominator;
            double left2power = (y + x + rx) / denominator;
            double right1power = (y + x - rx) / denominator;
            double right2power = (y - x - rx) / denominator;
            left1.setPower(left1power);
            left2.setPower(left2power);
            right1.setPower(right1power);
            right2.setPower(right2power);
        }
    }
}
