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
        waitForStart();
        while (robot.gyro.isCalibrating()) {
            telemetry.addData("log", "calibrating");
            Thread.sleep(50);
        }
        robot.line.enableLed(true);
        robot.color.enableLed(true);

        while(this.opModeIsActive())
        {
            telemetry.addData("US1", robot.US1.getValue());
            telemetry.addData("US2", robot.US2.getValue());
        }
//        //robot.printValues();
//        //set the robot perpendicular to the wall, Flipper forward
//        robot.move(-0.5, 55);
//        robot.my_wait(0.3);
//        robot.turnWithGyro(0.6, 45); // parallel to diagonal
//        robot.my_wait(0.1);
//        robot.move(-0.5, 145);
//
//        robot.my_wait(0.1);
//        robot.turnWithGyro(-0.5, 45); // parallel to wall
//        robot.Stop();
//        robot.my_wait(0.5);
//        robot.ParallelRecursion(0, 0.35);
////        robot.move(-0.4, 10);
//        robot.calibrate();
//        robot.my_wait(3.0);
//        robot.detectWhiteLine(-0.3);
//        robot.move(0.4, 8);
//        robot.my_wait(1);
//        robot.push.setPosition(0.7);
//        robot.my_wait(2);
//        //color sense
//        if(!robot.isBlue())
//            robot.move(0.5, 10);
//        else
//            robot.move(-0.8, 2);
//        robot.pushButton();
//        robot.move(-0.7, 70);
    }
}
