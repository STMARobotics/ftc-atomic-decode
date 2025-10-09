package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.teamcode.Constants.IntakeConstants.STALL_CURRENT_AMPS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class ShooterSubsystem extends SubsystemBase {

    private final DcMotorEx turretMotor1;
    private final DcMotorEx turretMotor2;

    private final Servo shootYaw;
    private final Servo shootUpDown;

    private Telemetry telemetry;

    public enum TurretState {
        SHOOTING,
        STOPPED
    }

    private TurretState currentState = TurretState.STOPPED;

    public ShooterSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        turretMotor1 = hardwareMap.get(DcMotorEx.class, "turret1");
        turretMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor2 = hardwareMap.get(DcMotorEx.class, "turret2");
        turretMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shootYaw = hardwareMap.get(Servo.class, "shootYaw");
        shootUpDown = hardwareMap.get(Servo.class, "shootUpDown");
        this.telemetry = telemetry;
    }

    public void shoot(double angle, double distance) {
        currentState = TurretState.SHOOTING;
        //factor in distance on field
        turretMotor1.setVelocity(5000*360*60, AngleUnit.DEGREES);
        turretMotor2.setVelocity(5000*360*60, AngleUnit.DEGREES);
        //set position and angle. Parameters too
        shootYaw.setPosition(angle);
        //set angle of shootUpDown
        shootUpDown.setPosition(distance);
    }

    //

    public void stop() {
        currentState = TurretState.STOPPED;
    }

    public boolean isStalling() {
        return turretMotor1.getCurrent(CurrentUnit.AMPS) > STALL_CURRENT_AMPS;
    }

    /**
     * This method is called repeatedly by the scheduler. It reads the
     * current state and sets the motor power accordingly.
     */
    @Override
    public void periodic() {
        telemetry.addData("power", turretMotor1.getPower());
        telemetry.addData("velocity", turretMotor1.getVelocity());
        switch (currentState) {
            case SHOOTING:
                turretMotor1.setVelocity(5000*360*60, AngleUnit.DEGREES);
                break;
            case STOPPED:
                turretMotor1.setVelocity(0);
                break;
        }
    }
}
