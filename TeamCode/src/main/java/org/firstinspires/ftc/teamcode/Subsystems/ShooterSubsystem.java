package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;


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
        return Math.abs(getRPM() - targetRPM) <= 75;
    }

    public double getRPM() {
        return flywheelMotor.getVelocity() / 28.0 * 60.0;
    }

    public void telemetrize(Telemetry telemetry) {
        telemetry.addData("Flywheel RPM", flywheelMotor.getVelocity() / 28.0 * 60.0);
        telemetry.addData("Target RPM", targetRPM);
    }
}
