package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.STALL_CURRENT_AMPS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class ShooterSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private final Telemetry telemetry;

    public ShooterSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.telemetry = telemetry;
    }

    public void shoot() {
        turretMotor.setVelocity(5000*360*60, AngleUnit.DEGREES);
    }

    public void stop() {
        turretMotor.setVelocity(0);
    }

    public boolean isStalling() {
        return turretMotor.getCurrent(CurrentUnit.AMPS) > STALL_CURRENT_AMPS;
    }

    /**
     * This method is called repeatedly by the scheduler. It reads the
     * current state and sets the motor power accordingly.
     */
    @Override
    public void periodic() {
        telemetry.addData("power", turretMotor.getPower());
        telemetry.addData("velocity", turretMotor.getVelocity());
    }
}
