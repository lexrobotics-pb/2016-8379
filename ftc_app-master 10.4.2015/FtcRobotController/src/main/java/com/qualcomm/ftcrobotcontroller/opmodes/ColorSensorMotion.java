package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Eula on 10/8/2015.
 * For FTC 2015-16 ResQ color beacon detection
 * Last Update: 2015.10.8
 * Message: class created to accommodate the button pressing mechanism designed by Jeana, Ada, Abbie, and Michell
 */
public class ColorSensorMotion extends OpMode {

    private ColorSensor color;
    private Servo servo;

    public void ColorSensorMotion(ColorSensor sensor, Servo motion)
    {
        color = sensor;
        color.enableLed(false);
        servo = motion;// idk servo = continuous? - Eula
    }

    /**
     * The color sensor is and only faces either blue or red
     *
     * @return true if the color detected is blue
     */
    public boolean IsBlueBR()
    {
        int red = 0, blue = 0;
//        double position = servo.getPosition();
        for (int x  = 0; x < 20; x++)
        {
            red += color.red();
            blue += color.blue();
            //move rack/servo left (or right depending on the set up) a little bit
            double time = this.time;
            while(this.time < time+0.1){}
        }
        if (blue > red)
            return true;
        return false;
    }

    @Override
    public void init(){}

    @Override
    public void loop(){}

    @Override
    public void stop(){}
}
