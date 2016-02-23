package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by zht on 2/20/2016.
 */
public class colorTest extends LinearOpMode {

    otherColor line;
    @Override
    public void runOpMode() throws InterruptedException {
        line = new otherColor(hardwareMap.deviceInterfaceModule.get("Device Interface Module"), 0);
        waitForStart();
        while(opModeIsActive())
        {
            telemetry.addData("red", line.red());
            telemetry.addData("green", line.green());
            telemetry.addData("blue", line.blue());
        }

    }

}
