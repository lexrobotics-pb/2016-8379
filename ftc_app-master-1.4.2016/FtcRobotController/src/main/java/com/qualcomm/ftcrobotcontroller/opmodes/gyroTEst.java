package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by zht on 2/20/2016.
 */
public class gyroTEst extends LinearOpMode{
    public void runOpMode() throws InterruptedException {
        Robot robot = new Robot(this);
        robot.gyro.calibrate();
        waitForStart();
        while(robot.gyro.isCalibrating()){
            Thread.sleep(50);
        }
        robot.turnWithGyro(0.9, 45);
        robot.turnWithGyro(-0.9, 45);
        robot.turnWithGyro(0.9, 45);
        robot.turnWithGyro(-0.9, 45);
        robot.turnWithGyro(0.9, 45);
        robot.turnWithGyro(-0.9, 45);

        robot.turnWithGyro(0.9, 90);
        robot.turnWithGyro(-0.9, 90);
        robot.turnWithGyro(0.9, 90);
        robot.turnWithGyro(-0.9, 90);
        robot.turnWithGyro(0.9, 90);
        robot.turnWithGyro(-0.9, 90);

        robot.turnWithGyro(0.9, 135);
        robot.turnWithGyro(-0.9, 135);
        robot.turnWithGyro(0.9, 135);
        robot.turnWithGyro(-0.9, 135);
        robot.turnWithGyro(0.9, 135);
        robot.turnWithGyro(-0.9, 135);
    }


}
