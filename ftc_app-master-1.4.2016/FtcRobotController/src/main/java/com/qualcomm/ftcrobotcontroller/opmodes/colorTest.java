package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by zht on 2/20/2016.
 */
public class colorTest extends LinearOpMode {

    otherColor line;
    ColorSensor color;
    @Override
    public void runOpMode() throws InterruptedException {
        line = new otherColor(hardwareMap.deviceInterfaceModule.get("Device Interface Module"), 0);
        color = hardwareMap.colorSensor.get("color");
        waitForStart();
        while(opModeIsActive())
        {
            telemetry.addData("red1", line.red());
            telemetry.addData("green1", line.green());
            telemetry.addData("blue1", line.blue());
            telemetry.addData("red2", color.red());
            telemetry.addData("green2", color.green());
            telemetry.addData("blue2", color.blue());
        }

    }

}
