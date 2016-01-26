package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * 2015 Autonomous program for pushing button on the Red Alliance Side
 * Created by Kara Luo on 11/14/2015.
 * Updated by Eula on 11/18/2015
 * Status: All configuration organized in the Robot Class
 */
public class AutoButtonBlue extends LinearOpMode{

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

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
        robot.my_wait(1.0);
        telemetry.addData("robot init", "complete");

        //set the robot perpendicular to the wall, Flipper forward
        robot.Flipper.setPower(-0.6);
        robot.move(0.7, 65);
        robot.my_wait(0.1);
        robot.turnWithGyro(0.6, 45.0); // parallel to diagonal
        robot.my_wait(0.1);
        robot.move(0.2, 145);
        robot.my_wait(0.1);
        robot.Flipper.setPower(0);
        robot.my_wait(0.1);
        robot.turnWithGyro(0.5, 133); // parallel to wall
        robot.my_wait(0.1);
        robot.ParallelRecursion(0, 0.4);

        robot.my_wait(0.5);
        robot.detectWhiteLine(-0.15);
        robot.move(-0.6, 7);
        robot.my_wait(0.1);
        robot.pushButton();
    }
    //        robot.push.setPosition(0.2);
//        robot.my_wait(2);
//        //color sense
//        if(!robot.isBlue())
//            robot.move(0.5, 2);
//        else
//            robot.move(-0.5, 10);
}
