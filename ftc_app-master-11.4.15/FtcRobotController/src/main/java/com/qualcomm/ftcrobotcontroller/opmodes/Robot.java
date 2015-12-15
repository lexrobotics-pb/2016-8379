package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Created by Eula on 10/8/2015.
 * Last Update: 2015/11/19 by Eula
 * Use this class to configure all robot parts and store main movement of the robot for Autonomous
 * Status: Updating for Meet 1
 */

public class Robot {
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor Flipper;
    DcMotor Lift;


    ColorSensor color;
    ColorSensor line;
//    OpticalDistanceSensor ods;

//    double CALIBRATE_ODS = 0.0;
    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    double ENCODER_F_R = 0;
    double ENCODER_F_L = 0;
    double ENCODER_B_R = 0;
    double ENCODER_B_L = 0;

    GyroSensor gyro;
    Servo push;
    Servo LeftTrigger;
    Servo RightTrigger;
//    Servo dump;

    static LinearOpMode waiter;

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

    public Robot(LinearOpMode hello) {
        waiter = hello;

        motorBackRight = hello.hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.FORWARD); //forwards back left motor
        motorFrontRight = hello.hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.FORWARD); //forwards back left motor
        motorBackLeft = hello.hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontLeft = hello.hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //forwards front left motor


        color = hello.hardwareMap.colorSensor.get("color");
        line = hello.hardwareMap.colorSensor.get("line");
        gyro = hello.hardwareMap.gyroSensor.get("gyro");

        push = hello.hardwareMap.servo.get("push");
        //dump = hello.hardwareMap.servo.get("dump");
        LeftTrigger = hello.hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hello.hardwareMap.servo.get("RightTrigger");
        RightTrigger.setPosition(0.95);
        LeftTrigger.setPosition(0.15);
      //  dump.setPosition(0.0);

        line.setI2cAddress(0x70);
        push.setPosition(0.5);

        line.enableLed(false);
        gyro.calibrate();
        while(gyro.isCalibrating())
        {
            hello.telemetry.addData("gyro calibration", gyro.isCalibrating());
            my_wait(0.1);
        }
        my_wait(1.0);

        hello.telemetry.addData("robot init", "complete");
        my_wait(1);
    }

    //====================================All Functions=====================================================================================================
    public void printValues() {
        color.enableLed(true);
        while (waiter.opModeIsActive()) {
            waiter.telemetry.addData("color", color.red());
            waiter.telemetry.addData("line", line.red());
        }
    }

    public void detectWhiteLine(double speed) {
        line.enableLed(true);
        JustMove(speed, speed);
        while (true && waiter.opModeIsActive()) {
            if (line.red()>=10 && line.blue()>=10 && line.green()>=10) {
                waiter.telemetry.addData("line red", line.red());
                waiter.telemetry.addData("line blue", line.blue());
                waiter.telemetry.addData("line green", line.green());
                break;
            }
            try {
                Thread.sleep(50);
            }catch (InterruptedException e){}
        }
        Stop();
    }

    /**
     * @param speed    [-1, 1],
     * @param distance > 0, in cm
     */
    public void move(double speed, double distance) {
        resetEncoders();
        JustMove(speed, speed);
        int i = 0;
        while (waiter.opModeIsActive() && (motorFrontLeft.getCurrentPosition() - ENCODER_F_L) / encoderV < distance / circumference) {
            i++;
        }
        Stop();
    }

    public void calibrate() {
        double red = 0.0;
        double blue = 0.0;
        for (int i = 0; i < 64; i++) {
            red += color.red();
            blue += color.blue();
        }
        CALIBRATE_RED = red / 64.0;
        CALIBRATE_BLUE = blue / 64.0;
    }

    public void JustMove(double speedRight, double speedLeft) {
        motorFrontLeft.setPower(speedLeft);
        motorBackLeft.setPower(speedLeft);
        motorBackRight.setPower(speedRight);
        motorFrontRight.setPower(speedRight);
    }

    public void Stop() {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        resetEncoders();
    }

    public void resetEncoders() {
        ENCODER_F_R = motorFrontRight.getCurrentPosition();
        ENCODER_F_L = motorFrontLeft.getCurrentPosition();
        ENCODER_B_R = motorBackRight.getCurrentPosition();
        ENCODER_B_L = motorBackLeft.getCurrentPosition();

    }

    /**
     * turn the robot on the spot
     *
     * @param speed   [-1, 1]
     * @param degrees angle in degree not in radians [0, 180]
     *                adjust cw and ccw using speed positive = cw, negative = ccw
     */
    public void turnWithGyro(double speed, double degrees) {
        gyro.resetZAxisIntegrator();
        my_wait(0.5);
        double left, right;

        right = -speed;
        left = speed;

        if (speed < 0) {
            degrees = 360 - degrees;
            JustMove(right, left);
            my_wait(0.5);
            while (waiter.opModeIsActive() && gyro.getHeading() > degrees) {
            }
        } else {
            JustMove(right, left);
            while (waiter.opModeIsActive() && gyro.getHeading() < degrees) {
            }
        }
        Stop();
    }

    public void my_wait(double sec) {
        double current = waiter.time;
        while (waiter.opModeIsActive() && (waiter.time - current) < sec) {
        }
    }

    public boolean isBlue() {
        Stop();
        double blue = 0.0;
        for (int x = 0; x < 20; x++) {
            blue += color.blue();
            my_wait(0.1);
        }
        blue /= 20;
        blue -= CALIBRATE_BLUE;
        return blue >= 1.0;
    }

    public boolean isRed() {
        Stop();
        double red = 0.0;
        for (int x = 0; x < 20; x++) {
            red += color.red();
            my_wait(0.1);
        }
        red /= 20;
        red -= CALIBRATE_RED;
        return red >= 1.0;
    }

    public void pushButton(){
        push.setPosition(0.1);
        my_wait(3.5);
        push.setPosition(0.5);
        my_wait(2);
        push.setPosition(0.7);
        my_wait(2);
        push.setPosition(0.5);
    }

    public void dump(){
//        dump.setPosition(0.59);
        my_wait(2);
    }
}
