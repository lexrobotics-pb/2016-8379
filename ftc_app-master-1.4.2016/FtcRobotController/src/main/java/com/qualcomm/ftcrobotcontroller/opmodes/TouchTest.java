package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by Eula on 12/29/2015.
 */
public class TouchTest extends LinearOpMode {


    TouchSensor touch;

    @Override
    public void runOpMode()throws InterruptedException {
        touch=hardwareMap.touchSensor.get("touch");
        waitForStart();
        while(opModeIsActive())
            telemetry.addData("touch",touch.isPressed());
    }
}