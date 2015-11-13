package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;


/**
 * Created by Kara Luo on 10/7/2015.
 */
public class ColorTest extends OpMode {
    ColorSensor color;
    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;

    @Override
    public void init() {
        color = hardwareMap.colorSensor.get("color");
        //BlackCalibration();
    }

    @Override
    public void loop() {
        color.enableLed(false);
        telemetry.addData("Clear", color.alpha());
        telemetry.addData("Red  ", color.red());
        telemetry.addData("Green", color.green());
        telemetry.addData("Blue ", color.blue());
        print(color.red(), color.blue());
        //telemetry.addData("Hue", hsvValues[0]);//every single loop it sorts the outputs alphabetically according to the tag
    }

    @Override
    public void stop() {

    }

    //Created by Kara Luo on 10/21/15
    /*public void BlackCalibration() {
        double red = 0.0, green = 0.0, blue = 0.0;
        for (int i = 0; i < 64; i++) {
            red += color.red();
            //telemetry.addData("test",color.red());
            green += color.green();
            blue += color.blue();
            double time = this.time;
            while (this.time < time + 0.1) {
            }
        }
        CALIBRATE_RED = red / 64.0;
        CALIBRATE_GREEN = green / 64.0;
        CALIBRATE_BLUE = blue / 64.0;


        telemetry.addData("Calibrate green", CALIBRATE_GREEN);
        telemetry.addData("Calibrate blue ", CALIBRATE_BLUE);

    }*/

    public double calibrateRed() {
        double red = 0.0;
        for (int i = 0; i < 64; i++) {
            red += color.red();
            double time = this.time;
            while (this.time < time + 0.1) {
            }
        }

        return red / 64.0;
    }
    public double calibrateGreen() {
        double green = 0.0;
        for (int i = 0; i < 64; i++) {
            green += color.green();
            double time = this.time;
            while (this.time < time + 0.1) {
            }
        }
        return green / 64.0;
    }

    public double calibrateBlue() {
        double blue = 0.0;
        for (int i = 0; i < 64; i++) {
            blue += color.blue();
            double time = this.time;
            while (this.time < time + 0.1) {
            }
        }
        return blue / 64.0;
    }

    public void print(double red, double blue) {
        if (red - CALIBRATE_RED > blue - CALIBRATE_BLUE) {
            telemetry.addData("compare", "red");
        } else if (red - CALIBRATE_RED > blue - CALIBRATE_BLUE) {
            telemetry.addData("compare", blue);
        } else {
            telemetry.addData("compare", "indistinguishable");
        }
    }


}


