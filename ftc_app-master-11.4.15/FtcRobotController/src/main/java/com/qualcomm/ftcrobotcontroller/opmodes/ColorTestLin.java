package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * 2015 Autonomous program for pushing button on the Red Alliance Side
 * Created by Kara Luo on 11/14/2015.
 * Updated by Eula on 11/18/2015
 * Status: All configuration organized in the Robot Class
 */
public class ColorTestLin extends LinearOpMode{

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

    @Override
    public void runOpMode() throws InterruptedException {
        Robot robot = new Robot(this);
        robot.color.enableLed(true);
        telemetry.addData("init", "complete");
        robot.my_wait(3);
        waitForStart();
        robot.printValues();

    }
}
