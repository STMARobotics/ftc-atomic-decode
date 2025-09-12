package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Main.Robot;

public class TurretSubsystem {

    private final DcMotorEx turret;

    public TurretSubsystem(Robot hardwaremap) {
        this.turret = hardwaremap.hardwareMap.get(DcMotorEx.class, "turret");
    }

    public void turretSpeed(double speed) {
        if (this.turret != null) {
            this.turret.setPower(speed);
        }
    }
}
