package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Created by Betsy and Eula from Betsy's pseudo codes on 4/18
 * Latest Update date: 5/1/2015
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

    Servo grabber;
    Servo hood;
    Servo trigger;
    Servo arm;

    UltrasonicSensor USfront;
    UltrasonicSensor USback;

    GyroSensor gyro;
    double EFrontRight, EBackRight, EBackLeft, EFrontLeft, ELift,USFrontR, USBackR, GyroR;

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

        arm = hardwareMap.servo.get("arm");
        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
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
    }

    public void updatePosition() {//update all parts of the robot
        EFrontLeft = motorFrontLeft.getCurrentPosition();
        EBackRight = motorBackRight.getCurrentPosition();
        EFrontRight = motorFrontRight.getCurrentPosition();
        EBackLeft = motorBackLeft.getCurrentPosition();
    }

    public boolean isDEVModeWrite()
    {
        return motorFrontRight.getDeviceMode().equals(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorBackRight.getDeviceMode().equals(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorBackLeft.getDeviceMode().equals(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorFrontLeft.getDeviceMode().equals(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorLift.getDeviceMode().equals(DcMotorController.DeviceMode.WRITE_ONLY);
    }

    public boolean isDEVModeRead()
    {
        return motorFrontRight.getDeviceMode().equals(DcMotorController.DeviceMode.READ_ONLY) &&
                motorBackRight.getDeviceMode().equals(DcMotorController.DeviceMode.READ_ONLY) &&
                motorBackLeft.getDeviceMode().equals(DcMotorController.DeviceMode.READ_ONLY) &&
                motorFrontLeft.getDeviceMode().equals(DcMotorController.DeviceMode.READ_ONLY) &&
                motorLift.getDeviceMode().equals(DcMotorController.DeviceMode.READ_ONLY);
    }

    public void switchAllToRead(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorLift.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
    }

    public void switchAllToWrite(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorLift.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
    }

    //partially update something?
}
