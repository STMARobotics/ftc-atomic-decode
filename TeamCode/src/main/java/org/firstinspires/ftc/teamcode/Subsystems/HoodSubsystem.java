package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.CRServo;

public class HoodSubsystem extends SubsystemBase {

    public final CRServo hood;

    public HoodSubsystem(HardwareMap hardwareMap) {
        hood = hardwareMap.get(CRServo.class, "hood");
    }

    public void hoodAngleIncrease() {
        hood.set(0.3);
    }

    public void hoodAngleDecrease() {
        hood.set(-0.3);
    }
}