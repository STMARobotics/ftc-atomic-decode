package org.firstinspires.ftc.teamcode.led;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

/**
 * Subsystem for interacting with the LED driver.
 */
public class LedSubsystem extends SubsystemBase {

    private final RevBlinkinLedDriver ledDriver;

    /**
     * Constructor
     * @param hardwareMap robot hardware map
     */
    public LedSubsystem(HardwareMap hardwareMap) {
        ledDriver = hardwareMap.get(RevBlinkinLedDriver.class, "led_driver");
    }

    /**
     * Sets the driver LED pattern
     * @param pattern pattern to set
     */
    public void setLedPattern(RevBlinkinLedDriver.BlinkinPattern pattern) {
        ledDriver.setPattern(pattern);
    }

}
