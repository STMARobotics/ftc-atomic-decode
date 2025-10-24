package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {

    public final CRServo hood;

    public HoodSubsystem(HardwareMap hardwareMap) {
        hood = hardwareMap.get(CRServo.class, "hood");
    }

    public void hoodAngleIncrease() {
        hood.setPower(0.3);
    }

    public void hoodAngleDecrease() {
        hood.setPower(-0.3);
    }

    public void hoodStop() {
        hood.setPower(0);
    }
}