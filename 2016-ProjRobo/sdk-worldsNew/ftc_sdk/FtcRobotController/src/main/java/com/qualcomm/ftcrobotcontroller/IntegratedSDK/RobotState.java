package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

public class RobotState {
    HardwareMap hardwareMap = new HardwareMap();
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;
    DcMotor motorLift;

    Servo grabber;
    Servo hood;
//    Servo holder;//nothing for continuous servo
    Servo trigger;

    UltrasonicSensor USfront;
    UltrasonicSensor USback;

    GyroSensor gyro;
    double EFrontRight, EBackRight, EBackLeft, EFrontLeft, ELift,USFrontR, USBackR,GyroR;

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

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
//        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

    }

    public void updateState(){//update all parts of the robot
        EFrontLeft = motorFrontLeft.getCurrentPosition();
        EBackRight = motorBackRight.getCurrentPosition();
        EFrontRight = motorFrontRight.getCurrentPosition();
        EBackLeft = motorBackLeft.getCurrentPosition();
        ELift = motorLift.getCurrentPosition();
        USBackR = USfront.getUltrasonicLevel();
        USBackR = USback.getUltrasonicLevel();
        GyroR = gyro.getRotation();
    }


    //partially update something?
}
