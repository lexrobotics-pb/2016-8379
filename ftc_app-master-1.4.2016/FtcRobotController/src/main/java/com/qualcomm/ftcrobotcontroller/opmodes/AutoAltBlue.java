package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by zht on 2/20/2016.
 */
public class AutoAltBlue extends LinearOpMode {

    public void runOpMode() throws InterruptedException {
        Robot robot = new Robot(this);
        robot.gyro.calibrate();
        robot.line.enableLed(true);
        waitForStart();

        robot.move(0.95, 250);//22.5, 14 cm

    }
}
