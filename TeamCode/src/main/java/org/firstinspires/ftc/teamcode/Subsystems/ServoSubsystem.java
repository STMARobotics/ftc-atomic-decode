package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class ServoSubsystem extends SubsystemBase {

    public final Servo servo;

    public ServoSubsystem(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "servo");
    }

    public void setPosition(double position){
        servo.setPosition(position);
    }
}