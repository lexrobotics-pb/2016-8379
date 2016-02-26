package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by zht on 2/20/2016.
 */
public class AutoAltRed extends LinearOpMode {

    public void runOpMode() throws InterruptedException {
        Robot robot = new Robot(this);
        robot.gyro.calibrate();
        robot.line.enableLed(true);

        waitForStart();
        while(robot.gyro.isCalibrating())
        {
            telemetry.addData("gyro calibration", robot.gyro.isCalibrating());
            Thread.sleep(50);
        }
        telemetry.addData("robot init", "complete");

        robot.move(0.95, 20);
        robot.niceTurn(-0.6, 30);
        robot.move(0.95, 268);
        robot.niceTurn(0.6, 26);
        robot.detectWhiteLine(-0.15);
        robot.pushButton1();
        robot.pushButton2();

    }
}
