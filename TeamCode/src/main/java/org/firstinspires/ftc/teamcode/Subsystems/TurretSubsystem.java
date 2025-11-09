package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.util.MathUtils;

import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private final AnalogInput pot;

    private LimelightSubsystem limelightSubsystem;

    private PIDController pidController;

    // PD gains (tune these)
    private double kP = 0.028;
    private double kD = 0.01;

    // state for derivative calculation
    private double lastError = 0.0;
    private long lastTimeMs = 0;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        pot = hardwareMap.get(AnalogInput.class, "turretPotentiometer");

        limelightSubsystem = new LimelightSubsystem(hardwareMap);

        turretMotor.setDirection(DcMotorEx.Direction.REVERSE);
        turretMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        lastTimeMs = System.currentTimeMillis();

        pidController = new PIDController(kP, 0.0, kD);
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
     * Automatically aims the turret at the target using a simple PD controller.
     */
    public void autoLockTurret() {
        // idk fam its just annoying
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
        telemetry.addData("limelight target offset (deg)", limelightSubsystem.getTargetOffset());
    }
}
