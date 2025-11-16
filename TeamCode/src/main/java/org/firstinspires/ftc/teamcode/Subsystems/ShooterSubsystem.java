package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


public class ShooterSubsystem extends SubsystemBase {

    public final DcMotorEx flywheelMotor;
    public final Servo hoodServo;
    public double targetAngle;
    public double targetRPM;

    public ShooterSubsystem(HardwareMap hardwareMap) {
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheelMotor");
        hoodServo = hardwareMap.get(Servo.class, "hoodServo");
    }

    /**
     * Sets the flywheel to a desired rpm
     * @param RPM the rpm that we want the flywheel to run at
     */
    public void setRPM(double RPM) {
        flywheelMotor.setVelocity(RPM*360/60, AngleUnit.DEGREES);
        targetRPM = RPM;
    }

    /**
     * Sets the flywheel motor power to 0
     */
    public void stop() {
        flywheelMotor.setPower(0);
        targetRPM = 0;
    }

    /**
     * Sets the hood servo to desired angle
     * @param angle the angle we want the hood to be set at
     */
    public void setHoodAngle(double angle) {
        hoodServo.setPosition(angle);
        targetAngle = angle;
        // TODO: map the servo angle to the actual angle of the shot
    }

    /**
     * Returns t/f if the shooter and the hood are ready for shooting
     */
    public boolean shooterIsReady() {
        return flywheelReady() && hoodReady();
    }

    /**
     * Returns t/f if the hood is ready for shooting
     */
    public boolean hoodReady() {
        return hoodServo.getPosition() == targetAngle;
    }

    /**
     * Returns t/f if the flywheel is ready for shooting
     */
    public boolean flywheelReady() {
        return Math.abs(flywheelMotor.getVelocity() / 28 * 60 - targetRPM) <= 50;
    }

    public void shootMax() {
        flywheelMotor.setVelocity(6000*360/60, AngleUnit.DEGREES);
    }
}
