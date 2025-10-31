package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDController;

import static org.firstinspires.ftc.teamcode.Constants.limelightConstants.*;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

public class TurretSubsystem extends SubsystemBase {

    private final PIDController turretPID;
    private final DcMotorEx turretMotor;
    private final Limelight3A limelight;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.setPollRateHz(LIMELIGHT_POLL_HZ);
        limelight.start();

        turretPID = new PIDController(0.5, 0.0, 0.0); // PID PID PID PID PID PID
        turretPID.setSetPoint(TURRET_HOME_ANGLE_DEG);
        turretPID.setTolerance(DEAD_BAND_DEG);
    }

    /**
     * Sets the turret motor power
     * @param power the power to set the turret motor to (-1 to 1)
     */
    public void setTurretPower(double power) {
        turretMotor.setPower(power);
    }

    /**
     * Stops the turret motor
     */
    public void stopTurret() {
        turretMotor.setPower(0);
    }

    /**
     * Automatically aims the turret at the target using pid.
     * (PLEASE WORK FIRST TRY I DONT WANT TO DEBUG THIS)
     */
    public void autolockTurret() {
        double error = getTargetOffset();

        // this would be if limelight cant see or isnt on
        if (Double.isNaN(error)) {
            stopTurret();
            return;
        }

        double output = turretPID.calculate(error);

        // Apply minimum power to overcome static friction, if not at setpoint
        if (!turretPID.atSetPoint()) {
            double sign = Math.signum(output == 0 ? error : output);
            output = sign * Math.max(Math.abs(output), MIN_TURRET_POWER);
        } else {
            output = 0.0;
        }

        // Soft limits so we dont snap a gear
        final double SOFT_MIN_DEG = -134.0;
        final double SOFT_MAX_DEG = 134.0;
        double position = getTurretPosition();
        if (!Double.isNaN(position)) {
            if (position <= SOFT_MIN_DEG && output < 0) {
                output = 0.0;
            } else if (position >= SOFT_MAX_DEG && output > 0) {
                output = 0.0;
            }
        }

        setTurretPower(output);
    }

    /**
     * Switches the limelight pipeline
     * @param pipeline the pipeline index to switch to
     */
    public void pipelineSwitcher(int pipeline) {
        limelight.pipelineSwitch(pipeline); // 0 = blue, 1 = red
    }

//    public double getDistance() {
//        return limelight.getTargetDistance();
//    }

    /**
     * Returns the horizontal offset from crosshair to target
     * Uses LLResult.getTx() per Limelight FTC API. Returns NaN if no result available.
     * @return the horizontal offset in degrees (NaN if unavailable)
     */
    public double getTargetOffset() {
        LLResult result = limelight.getLatestResult();
        if (result == null) {
            return Double.NaN;
        }
        return result.getTx();
    }

    /**
     * Returns the turret position from the potentiometer
     * @return the turret position in degrees
     */
    public double getTurretPosition() {
        // TODO: get turret position from the 10 turn potentiometer (how to read analog values?)
        return 0;
    }
}
