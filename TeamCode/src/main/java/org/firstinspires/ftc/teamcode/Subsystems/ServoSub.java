package org.firstinspires.ftc.teamcode.Subsystems;

//import various stuff;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class ServoSub extends SubsystemBase {
    private final Servo servo;
    public ServoSub(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "servoIndex");
    }

    public void setServoPosition(double position) {
        servo.setPosition(position);
    }
}