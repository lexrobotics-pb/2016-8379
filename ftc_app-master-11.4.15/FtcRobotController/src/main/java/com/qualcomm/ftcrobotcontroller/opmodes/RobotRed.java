package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * 2015 Autonomous program for pushing button on the Red Alliance Side
 * Created by Kara Luo on 11/14/2015.
 * Updated by Eula on 11/18/2015
 * Status: All configuration organized in the Robot Class
 */
public class RobotRed extends LinearOpMode{

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

    @Override
    public void runOpMode() throws InterruptedException {
        Robot robot = new Robot(this);

        telemetry.addData("init", "complete");
        robot.my_wait(3);

        waitForStart();
        robot.line.enableLed(true);
        robot.color.enableLed(true);

        telemetry.clearData();
        //robot.printValues();
        //set the robot perpendicular to the wall
        robot.move(0.7, 65);
        robot.my_wait(0.3);
        robot.turnWithGyro(-0.8, 44); // parallel to diagonal
        robot.my_wait(0.1);
        robot.move(0.9, 155);

        robot.my_wait(0.1);
        robot.turnWithGyro(-0.8, 120); // parallel to wall
        robot.Stop();

        robot.move(0.7, 10);
        robot.calibrate();
        robot.my_wait(3.0);
        robot.detectWhiteLine(-0.2);
        robot.move(0.5, 8);
        robot.my_wait(1);
        robot.push.setPosition(0.7);
        robot.my_wait(2);
        //robot.dump();
        //robot.my_wait(1);


        //color sense
        if(!robot.isRed())
            robot.move(0.5, 10);
        else
            robot.move(-0.8, 2);
        robot.pushButton();
        robot.move(0.7, 70);
    }
}
