package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.util.MathUtils;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private final AnalogInput pot;
    private final LimelightSubsystem limelightSubsystem;
    private double kP = 0.028;
    private double kD = 0.004;
    private final PIDController pid = new PIDController(kP, 0.0, kD);

    private double filteredOffsetDeg = 0.0;
    private double lastAppliedPower = 0.0;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");
        limelightSubsystem = new LimelightSubsystem(hardwareMap);

        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pid.setSetPoint(0.0);
        pid.setTolerance(DEAD_BAND_DEG);
    }

    public void setTurretPower(double power) {
        lastAppliedPower = MathUtils.clamp(power, -0.5, 0.5);
        turretMotor.setPower(lastAppliedPower);
    }

    public void stopTurret() {
        lastAppliedPower = 0.0;
        turretMotor.setPower(0);
    }

    public void autoLockTurret() {
        if (!limelightSubsystem.hasValidTarget()) {
            stopTurret();
            pid.reset();
            return;
        }

        double offset = limelightSubsystem.getTargetOffset();
        filteredOffsetDeg = LIMELIGHT_ALPHA * offset + (1 - LIMELIGHT_ALPHA) * filteredOffsetDeg;
        double error = filteredOffsetDeg;

        if (Math.abs(error) <= DEAD_BAND_DEG) {
            stopTurret();
            pid.reset();
            return;
        }

        double absErr = Math.abs(error);
        double scale = absErr > 15 ? 1.0 : absErr > 5 ? 0.6 : 0.35;
        pid.setP(kP * scale);

        double rawOutput = pid.calculate(error, 0.0);
        double capped = MathUtils.clamp(rawOutput, -0.4, 0.4);

        double delta = capped - lastAppliedPower;
        if (delta > MAX_POWER_DELTA_PER_LOOP) delta = MAX_POWER_DELTA_PER_LOOP;
        if (delta < -MAX_POWER_DELTA_PER_LOOP) delta = -MAX_POWER_DELTA_PER_LOOP;

        setTurretPower(lastAppliedPower + delta);
    }

    public double getTurretPosition() {
        double v = Math.max(POT_MIN_V, Math.min(pot.getVoltage(), POT_MAX_V));
        double fraction = (v - POT_MIN_V) / (POT_MAX_V - POT_MIN_V);
        return fraction * (SOFT_MAX_DEG - SOFT_MIN_DEG) + SOFT_MIN_DEG;
    }

    public void telemetrize(Telemetry telemetry) {
        telemetry.addData("pot voltage", pot.getVoltage());
        telemetry.addData("turret pos (deg)", getTurretPosition());
        telemetry.addData("tx raw", limelightSubsystem.getTargetOffset());
        telemetry.addData("tx filtered", filteredOffsetDeg);
        telemetry.addData("has target", limelightSubsystem.hasValidTarget());
        telemetry.addData("power cmd", lastAppliedPower);
        telemetry.addData("motor power", turretMotor.getPower());
        telemetry.addData("P", pid.getP());
        telemetry.addData("D", pid.getD());
    }
}
