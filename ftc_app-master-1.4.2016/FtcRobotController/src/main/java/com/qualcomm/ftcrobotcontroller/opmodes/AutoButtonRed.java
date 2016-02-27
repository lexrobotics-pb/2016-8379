package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * 2015 Autonomous program for pushing button on the Red Alliance Side
 * Created by Kara Luo on 11/14/2015.
 * Updated by Eula on 11/18/2015
 * Status: All configuration organized in the Robot Class
 */
public class AutoButtonRed extends LinearOpMode{

    @Override
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

        //set the robot perpendicular to the wall, Flipper forward
        robot.Flipper.setPower(-0.6);
        robot.move(0.8, 70);
        robot.my_wait(0.1);
        robot.niceTurn(-0.6, 40.0); // parallel to diagonal
        robot.my_wait(0.1);
        robot.move(0.95, 124);
        robot.my_wait(0.1);
        robot.Flipper.setPower(0);
        robot.my_wait(0.1);
        robot.niceTurn(0.6, 18); // parallel to wall
        robot.my_wait(0.1);
        robot.ParallelRecursion(0, 0.4);

        robot.my_wait(0.5);
        robot.detectWhiteLine(0.15);
        robot.move(-0.8, 10);
        robot.pushButton1();
        robot.colorSenseRed();
        robot.pushButton2();
        robot.my_wait(2);
        robot.move(-0.8, 50);
        robot.push.setPosition(0.5);
        robot.dump.setPosition(0.5);
    }
}
