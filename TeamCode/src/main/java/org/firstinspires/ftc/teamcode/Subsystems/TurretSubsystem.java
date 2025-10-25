package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.STALL_CURRENT_AMPS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class TurretSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor;
    private Telemetry telemetry;

    private final double farRPM = 4200;
    private final double closeRPM = 2500;

    public enum TurretState {
        FAR,
        STOPPED,
        CLOSE
    }

    private TurretState currentState = TurretState.STOPPED;

    public TurretSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.telemetry = telemetry;
    }

    public void far() {
        currentState = TurretState.FAR;
    }

    public void close(){
        currentState = TurretState.CLOSE;
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
        telemetry.addData("power", turretMotor.getPower());
        telemetry.addData("velocity", turretMotor.getVelocity());
        switch (currentState) {
            case FAR:
                turretMotor.setVelocity(farRPM*360*60, AngleUnit.DEGREES);
                break;
            case STOPPED:
                turretMotor.setVelocity(0);
                break;
            case CLOSE:
                turretMotor.setVelocity(closeRPM*360*60, AngleUnit.DEGREES);
        }
    }
}
