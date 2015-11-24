package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * 2015 Autonomous program for pushing button on the Red Alliance Side
 * Created by Kara Luo on 11/14/2015.
 * Updated by Eula on 11/18/2015
 * Status: All configuration organized in the Robot Class
 */
public class RobotRed extends LinearOpMode {
    Robot robot = new Robot(this);

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

    @Override
    public void runOpMode() throws InterruptedException {

        while (robot.gyro.isCalibrating()) {
            telemetry.addData("log", "calibrating");
            Thread.sleep(50);
        }
        //set the robot perpendicular to the wall
        robot.move(0.5, 60);
        robot.my_wait(0.3);
        robot.turnWithGyro(-0.5, 39); // parallel to diagonal
        robot.my_wait(0.1);
        robot.move(0.9, 152);
        robot.my_wait(0.1);
        robot.turnWithGyro(-0.5, 120); // parallel to wall
        robot.my_wait(0.1);
        robot.move(0.5, 10);
        robot.calibrate();
        robot.Stop();

        boolean search = true;

        double now = this.time;
        robot.JustMove(-0.1, -0.1);
        do {
            if ((robot.color.red()-robot.CALIBRATE_RED)>=1)
            {
                robot.Stop();
                if (robot.isRed())
                    search = false;
                else
                    robot.JustMove(-0.1, -0.1);
            }
        }while(this.opModeIsActive() && search && this.time - now < 5.0);
        robot.move(0.1, 5.0);
        robot.my_wait(1.0);
        robot.push.setPosition(0.1);
        robot.my_wait(3.5);
        robot.push.setPosition(0.5);
        robot.my_wait(2);
        robot.push.setPosition(0.7);
        robot.my_wait(2);
        robot.push.setPosition(0.5);
        robot.move(0.5, 90);


    }
}
