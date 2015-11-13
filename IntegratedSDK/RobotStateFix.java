package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.io.*;

/**
 * Created by Betsy and Eula from Betsy's pseudo codes on 4/18
 * Latest Update date: 5/11/2015 8AM
 * Latest Update by: Kara
 * Purpose of the class: store all of the configuration variables and potentially read from various sensor values
 * Status: useful enough for now
 */

public class RobotStateFix{
    Telemetry telemetry = new Telemetry();
    //HardwareMap hardwareMap = new HardwareMap();
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorLift;

    Servo arm;
    Servo grabber;
    Servo hood;
    Servo trigger;

//    UltrasonicSensor USfront;
//    UltrasonicSensor USback;

//    GyroSensor gyro;
    double EFrontRight, EBackRight, EBackLeft, EFrontLeft, ELift,USFrontR, USBackR, GyroR;

    public RobotStateFix(HardwareMap hardwareMap)
    {
        DbgLog.msg("run RobotStateFix");
        try {
            DbgLog.msg("********* start initializing hardwaremap");
            motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
            DbgLog.msg("********* hhhhh failed to initialize");
            motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        }catch(Exception e){
            String message = getStackTrace(e);
            DbgLog.msg("*******"+message);
        }
       motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorLift = hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorFrontLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorFrontRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorBackRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorBackLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

//        USfront = hardwareMap.ultrasonicSensor.get("USfront");
//        USback = hardwareMap.ultrasonicSensor.get("USback");

//        gyro = hardwareMap.gyroSensor.get("gyro");

        /*arm = hardwareMap.servo.get("arm");
        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        trigger = hardwareMap.servo.get("trigger");*/
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public void updateState(){//update all parts of the robot
        EFrontLeft = motorFrontLeft.getCurrentPosition();
        EBackRight = motorBackRight.getCurrentPosition();
        EFrontRight = motorFrontRight.getCurrentPosition();
        EBackLeft = motorBackLeft.getCurrentPosition();
        ELift = motorLift.getCurrentPosition();
//        USFrontR = USfront.getUltrasonicLevel();
//        USBackR = USback.getUltrasonicLevel();
//        GyroR = gyro.getRotation();
    }

    public void updatePosition() {//update all parts of the robot
        EFrontLeft = motorFrontLeft.getCurrentPosition();
        EBackRight = motorBackRight.getCurrentPosition();
        EFrontRight = motorFrontRight.getCurrentPosition();
        EBackLeft = motorBackLeft.getCurrentPosition();
    }

    /*public boolean isDEVModeWrite()
    {
        return motorFrontRight.getDeviceMode()==(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorBackRight.getDeviceMode()==(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorBackLeft.getDeviceMode()==(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorFrontLeft.getDeviceMode()==(DcMotorController.DeviceMode.WRITE_ONLY) &&
                motorLift.getDeviceMode()==(DcMotorController.DeviceMode.WRITE_ONLY);
    }

    public boolean isDEVModeRead()
    {
        return motorFrontRight.getDeviceMode()==(DcMotorController.DeviceMode.READ_ONLY) &&
                motorBackRight.getDeviceMode()==(DcMotorController.DeviceMode.READ_ONLY) &&
                motorBackLeft.getDeviceMode()==(DcMotorController.DeviceMode.READ_ONLY) &&
                motorFrontLeft.getDeviceMode()==(DcMotorController.DeviceMode.READ_ONLY) &&
                motorLift.getDeviceMode()==(DcMotorController.DeviceMode.READ_ONLY);
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
    }*/

    //partially update something?
}
