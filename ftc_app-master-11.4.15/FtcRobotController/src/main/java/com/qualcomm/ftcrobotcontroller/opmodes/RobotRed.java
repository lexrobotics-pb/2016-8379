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


//        robot.dump();
//

        telemetry.clearData();
        //robot.printValues();
        //set the robot perpendicular to the wall
        robot.move(0.5, 65);
        robot.my_wait(0.3);
        robot.turnWithGyro(-0.5, 41); // parallel to diagonal
        robot.my_wait(0.1);
        robot.move(0.9, 147);
        robot.my_wait(0.1);
        robot.turnWithGyro(-0.5, 115); // parallel to wall
        robot.Stop();

        robot.move(0.5, 10);
        robot.calibrate();
        robot.my_wait(3.0);
        robot.detectWhiteLine(-0.1);
        robot.my_wait(0.5);
        //dump dump dump dummmmmmp
        robot.move(0.2, 10);
        robot.Stop();

        //color sense
        if(!robot.isRed())
            robot.move(0.2, 10);
        else
            robot.move(-0.2, 2);

        //push button
        robot.pushButton();
        robot.move(0.5, 90);
    }
}
