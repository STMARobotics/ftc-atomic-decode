package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.STALL_CURRENT_AMPS;
import static org.firstinspires.ftc.teamcode.Constants.TurretConstants.TURRET_POWER;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;

    public enum TurretState {
        SHOOTING,
        STOPPED
    }

    private TurretState currentState = TurretState.STOPPED;

    public TurretSubsystem(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
    }

    public void shoot() {
        currentState = TurretState.SHOOTING;
    }

    public void stop() {
        currentState = TurretState.STOPPED;
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
        switch (currentState) {
            case SHOOTING:
                turretMotor.setPower(TURRET_POWER);
                break;
            case STOPPED:
                turretMotor.setPower(0.0);
                break;
        }
    }
}
