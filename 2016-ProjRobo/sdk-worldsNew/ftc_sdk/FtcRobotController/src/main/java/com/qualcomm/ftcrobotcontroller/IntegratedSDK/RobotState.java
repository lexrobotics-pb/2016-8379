package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Created by Betsy and Eula from Betsy's pseudo codes
 * Latest Update date: 4/30/2015
 * Purpose of the class: store all of the configuration variables and potentially read from various sensor values
 * Status: useful enough for now
 */

public class RobotState {
    HardwareMap hardwareMap = new HardwareMap();
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;
    DcMotor motorLift;
    Servo servo_1;

    Servo grabber;
    Servo hood;
//    Servo holder;//nothing for continuous servo
    Servo trigger;

    UltrasonicSensor USfront;
    UltrasonicSensor USback;

    GyroSensor gyro;
    double EFrontRight, EBackRight, EBackLeft, EFrontLeft, ELift,USFrontR, USBackR, GyroR, servoP;

    RobotState()
    {
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorLift = hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");

        USfront = hardwareMap.ultrasonicSensor.get("USfront");
        USback = hardwareMap.ultrasonicSensor.get("USback");

        gyro = hardwareMap.gyroSensor.get("gyro");

        servo_1 = hardwareMap.servo.get("servo");

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
//      holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

    }

    public void updateState(){//update all parts of the robot
        EFrontLeft = motorFrontLeft.getCurrentPosition();
        EBackRight = motorBackRight.getCurrentPosition();
        EFrontRight = motorFrontRight.getCurrentPosition();
        EBackLeft = motorBackLeft.getCurrentPosition();
        ELift = motorLift.getCurrentPosition();
        USFrontR = USfront.getUltrasonicLevel();
        USBackR = USback.getUltrasonicLevel();
        GyroR = gyro.getRotation();
        servoP = servo_1.getPosition();
    }


    //partially update something?
}
