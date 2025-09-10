package org.firstinspires.ftc.teamcode.basicSubsystem;

//import various stuff;


import com.rowanmcalpin.nextftc.core.Subsystem;
import com.qualcomm.robotcore.hardware.Servo;

public class subsystem extends Subsystem {
    // this is where all the variables are
    private final Servo servo;

    // this is scary configuration stuff that i hate doing
    public subsystem(Servo servo) {
        this.servo = servo;
    }


    /**
     * this essentially just does code that would otherwise be done in a main file but its done in
     * a different one
     * @param position the position to set the servo to
     */
    public void setServoPosition(double position) {
        position = Math.max(-90, Math.min(90, position));

        double mappedPosition = (position + 90) / 180.0;

        servo.setPosition(mappedPosition);
    }

}