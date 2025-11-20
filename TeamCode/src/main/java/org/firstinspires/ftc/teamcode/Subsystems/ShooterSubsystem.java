package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


public class ShooterSubsystem extends SubsystemBase {

    private final DcMotorEx flywheelMotor;
    private double targetRPM;

    public ShooterSubsystem(HardwareMap hardwareMap) {
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheelMotor");
    }

    /**
     * Sets the flywheel to a desired rpm
     * @param RPM the rpm that we want the flywheel to run at
     */
    public void setRPM(double RPM) {
        flywheelMotor.setVelocity(RPM*28/60);
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
     * Returns t/f if the flywheel is ready for shooting
     */
    public boolean flywheelReady() {
        return Math.abs(flywheelMotor.getVelocity() / 28 * 60 - targetRPM) <= 50;
    }

    public void shootMax() {
        flywheelMotor.setVelocity(6000*360/60, AngleUnit.DEGREES);
    }
}
