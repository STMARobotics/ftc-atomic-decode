package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.util.MathUtils;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private final AnalogInput pot;

    private LimelightSubsystem limelightSubsystem;

    // PD gains (tune these)
    private double kP = 0.03;
    private double kD = 0.0;
    private double kF = 0.01;

    // state for derivative calculation
    private double lastError = 0.0;
    private long lastTimeMs = 0;

    private double lastAppliedPower = 0.0; // track for telemetry

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");

        limelightSubsystem = new LimelightSubsystem(hardwareMap);

        turretMotor.setDirection(DcMotorEx.Direction.REVERSE);
        turretMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        lastTimeMs = System.currentTimeMillis();
    }

    /**
     * Sets the turret motor power
     * @param power the power to set the turret motor to (-1 to 1)
     */
    public void setTurretPower(double power) {
        lastAppliedPower = MathUtils.clamp(power, -1.0, 1.0);
        turretMotor.setPower(lastAppliedPower);
    }

    /**
     * Stops the turret motor
     */
    public void stopTurret() {
        lastAppliedPower = 0.0;
        turretMotor.setPower(0);
    }

    /**
     * Automatically aims the turret at the target using a simple PD controller.
     */
    public void autoLockTurret() {
        double offsetDeg = limelightSubsystem.getTargetOffset();

        if (Double.isNaN(offsetDeg)) {
            stopTurret();
            return;
        }

        if (Math.abs(offsetDeg) <= DEAD_BAND_DEG) {
            stopTurret();
            return;
        }

        long now = System.currentTimeMillis();
        double dtSec = (now - lastTimeMs) / 1000.0;
        if (dtSec <= 0) dtSec = 0.02; // fallback typical loop time

        double error = offsetDeg; // want to drive error to 0
        double dError = (error - lastError) / dtSec;

        double pTerm = kP * error;
        double dTerm = kD * dError;
        double fTerm = kF * Math.signum(error); // simple static feedforward

        double rawOutput = pTerm + dTerm + fTerm;

        if (Math.abs(rawOutput) < MIN_TURRET_POWER) {
            rawOutput = Math.signum(rawOutput) * MIN_TURRET_POWER;
        }

        setTurretPower(rawOutput);

        lastError = error;
        lastTimeMs = now;
    }

    /**
     * Returns the turret position in degrees.
     */
    public double getTurretPosition() {
        double v = pot.getVoltage();

        v = Math.max(POT_MIN_V,
                Math.min(v, POT_MAX_V));

        double fraction = (v - POT_MIN_V)
                / (POT_MAX_V - POT_MIN_V);

        double angle = fraction * (SOFT_MAX_DEG - SOFT_MIN_DEG)
                + SOFT_MIN_DEG;

        return angle;
    }


    public void telemetrize(Telemetry telemetry) {
        telemetry.addData("voltage from pot", pot.getVoltage());
        telemetry.addData("turret position (deg)", getTurretPosition());
        double offsetDeg = limelightSubsystem.getTargetOffset();
        telemetry.addData("limelight target offset (deg)", offsetDeg);
        telemetry.addData("limelight has target", limelightSubsystem.hasValidTarget());
        telemetry.addData("turret commanded power", lastAppliedPower);
        telemetry.addData("turret motor getPower()", turretMotor.getPower());
        telemetry.addData("turret last error", lastError);
    }
}
