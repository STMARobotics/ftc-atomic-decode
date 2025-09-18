package org.firstinspires.ftc.teamcode.intake;

import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.OUTTAKE_POWER;
import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.STALL_CURRENT_AMPS;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class IntakeSubsystem extends SubsystemBase {

    private final DcMotorEx intakeMotor;

    public enum IntakeState {
        INTAKING,
        OUTTAKING,
        STOPPED
    }

    private IntakeState currentState = IntakeState.STOPPED;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
    }

    public void intake() {
        currentState = IntakeState.INTAKING;
    }

    public void outtake() {
        currentState = IntakeState.OUTTAKING;
    }

    public void stop() {
        currentState = IntakeState.STOPPED;
    }

    public boolean isStalling() {
        return intakeMotor.getCurrent(CurrentUnit.AMPS) > STALL_CURRENT_AMPS;
    }

    /**
     * This method is called repeatedly by the scheduler. It reads the
     * current state and sets the motor power accordingly.
     */
    @Override
    public void periodic() {
        // 5. Implement the state machine
        switch (currentState) {
            case INTAKING:
                intakeMotor.setPower(INTAKE_POWER); // Or your desired intake power
                break;
            case OUTTAKING:
                intakeMotor.setPower(OUTTAKE_POWER); // Or your desired outtake power
                break;
            case STOPPED:
                intakeMotor.setPower(0.0);
                break;
        }
    }
}
