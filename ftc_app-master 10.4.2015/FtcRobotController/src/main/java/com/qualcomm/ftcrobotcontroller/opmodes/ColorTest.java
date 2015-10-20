package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by Kara Luo on 10/7/2015.
 */
public class ColorTest extends OpMode {
    ColorSensor color;
    float hsvValues[] = {0,0,0};
    final float values[] = hsvValues;

    @Override
    public void init()
    {
        color = hardwareMap.colorSensor.get("color");
    }
    @Override
    public void loop()
    {
        color.enableLed(false);
        telemetry.addData("Clear", color.alpha());
        telemetry.addData("Red  ", color.red());
        telemetry.addData("Green", color.green());
        telemetry.addData("Blue ", color.blue());
        telemetry.addData("Hue", hsvValues[0]);//every single loop it sorts the outputs alphabetically according to the tag
    }

    @Override
    public void stop()
    {

    }


}


