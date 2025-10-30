package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.*;

public class IntakeSubsystem extends SubsystemBase {
    private final DcMotorEx intakeMotor;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
    }

    public void intake() {
        intakeMotor.setPower(INTAKE_POWER);
    }

    public void outtake() {
        intakeMotor.setPower(OUTTAKE_POWER);
    }

    public void stop() {
        intakeMotor.setPower(0.0);
    }

    public boolean isStalling() {
        return intakeMotor.getCurrent(CurrentUnit.AMPS) > STALL_CURRENT_AMPS;
    }
}
