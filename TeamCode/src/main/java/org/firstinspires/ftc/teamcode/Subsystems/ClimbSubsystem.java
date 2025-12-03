package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import static org.firstinspires.ftc.teamcode.Constants.ClimbConstants.*;

public class ClimbSubsystem extends SubsystemBase {

    private final DcMotorEx left2;
    private final DcMotorEx right2;
    private final Servo clutchServo;

    public ClimbSubsystem(HardwareMap hardwareMap) {
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");
        left2.setDirection(DcMotorEx.Direction.REVERSE);
        clutchServo = hardwareMap.get(Servo.class, "clutchServo");
    }

    /**
     * Sets the climb motors (back l/r) to a given power.
     * Power is clamped to the maximum allowed by CLIMB_POWER.
     * @param power the power to set the climb motors to
     */
    public void setClimbPower(double power) {
        double clampedPower = Math.min(Math.abs(power), CLIMB_POWER);
        left2.setPower(clampedPower);
        right2.setPower(clampedPower);
    }

    /**
     * Stops the climb motors
     */
    public void stopClimb() {
        left2.setPower(0);
        right2.setPower(0);
    }

    /**
     * Engages the clutch to enable climbing
     */
    public void engageClutch() {
        clutchServo.setPosition(CLUTCH_ENGAGED_POS);
    }

    /**
     * Disengages the clutch to disable climbing
     */
    public void disengageClutch() {
        clutchServo.setPosition(CLUTCH_DISENGAGED_POS);
    }
}
