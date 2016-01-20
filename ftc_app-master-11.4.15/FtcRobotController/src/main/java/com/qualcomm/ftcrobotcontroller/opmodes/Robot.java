package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.AnalogInput;

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
    DcMotor Box;
    DcMotor Conveyor;

    Servo LeftTrigger;
    Servo RightTrigger;
    Servo dump;
    Servo push;
    Servo gate;

    ColorSensor color;
    otherColor line;
    AnalogInput US1;
    AnalogInput US2;
    GyroSensor gyro;

    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    double ENCODER_F_R = 0;
    double ENCODER_F_L = 0;
    double ENCODER_B_R = 0;
    double ENCODER_B_L = 0;


    double minSpeed = 0.15;
    double USfactor = 1.2;//actual_distance / reading

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

        Flipper = hello.hardwareMap.dcMotor.get("Flipper");
        Box = hello.hardwareMap.dcMotor.get("Box");
        Conveyor = hello.hardwareMap.dcMotor.get("Conveyor");

        color = hello.hardwareMap.colorSensor.get("color");
        line = new otherColor(hello.hardwareMap.deviceInterfaceModule.get("Device Interface Module"), 0);
        gyro = hello.hardwareMap.gyroSensor.get("gyro");
        US1 = hello.hardwareMap.analogInput.get("US1");
        US2 = hello.hardwareMap.analogInput.get("US2");

        push = hello.hardwareMap.servo.get("push");
        dump = hello.hardwareMap.servo.get("dump");
        LeftTrigger = hello.hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hello.hardwareMap.servo.get("RightTrigger");
        gate=hello.hardwareMap.servo.get("gate");

        RightTrigger.setPosition(0.15);
        LeftTrigger.setPosition(0.95);
        dump.setPosition(0.5);
        push.setPosition(0.5);
        gate.setPosition(0.0);
    }

    //====================================All Functions=====================================================================================================
    public void printValues() {
        color.enableLed(true);
        while (waiter.opModeIsActive()) {
            waiter.telemetry.addData("color", color.red());
            waiter.telemetry.addData("line", line.red());
        }
    }

    public void detectWhiteLine(double speed) throws InterruptedException{
        line.enableLed(true);
        JustMove(speed, speed);
        double now = waiter.time;
        while (true && waiter.opModeIsActive() && waiter.time - now < 5) {
            waiter.telemetry.addData("line red", line.red());
            waiter.telemetry.addData("line blue", line.blue());
            waiter.telemetry.addData("line green", line.green());
            if (line.red()!=0 || line.blue()!=0 && line.green()!=0) {
                waiter.telemetry.addData("line red", line.red());
                waiter.telemetry.addData("line blue", line.blue());
                waiter.telemetry.addData("line green", line.green());
                break;
            }
            Thread.sleep(50);
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
        while (waiter.opModeIsActive() && Math.abs(motorBackLeft.getCurrentPosition() - ENCODER_B_L) / encoderV < distance / circumference) {
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
        waiter.telemetry.addData("calibrate", "complete");
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
     * @param speed   [-1, 1]
     * @param degrees angle in degree not in radians [0, 180]
     *                adjust cw and ccw using speed positive = cw, negative = ccw
     */
    public void turnWithGyro(double speed, double degrees) {
        gyro.resetZAxisIntegrator();
        my_wait(1.0);
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
        for (int x = 0; x < 15; x++) {
            blue += color.blue();
            my_wait(0.05);
        }
        blue /= 20;
        blue -= CALIBRATE_BLUE;
        return blue >= 1.0;
    }

    public boolean isRed() {
        Stop();
        double red = 0.0;
        for (int x = 0; x < 15; x++) {
            red += color.red();
            my_wait(0.05);
        }
        red /= 20;
        red -= CALIBRATE_RED;
        return red >= 1.0;
    }

    public void pushButton(){
//        push.setPosition(0.3);
        dump.setPosition(0.3);
        my_wait(2.0);
        push.setPosition(0.3);
        my_wait(2.0);
        push.setPosition(0.5);
        dump.setPosition(0.5);
        my_wait(1.0);
        push.setPosition(0.8);
        dump.setPosition(0.8);
        my_wait(3.0);
        push.setPosition(0.5);
        dump.setPosition(0.5);
    }

    /**
     * parallels the robot to the wall using recursion
     * @param x iteration counter
     * @param speed [0, 1] > minSpeed
     * @throws InterruptedException for sleep()
     */
    public void ParallelRecursion(int x, double speed) throws InterruptedException
    {
        double speedL = speed, speedR = speed*-1;//clock wise

        //base case: max adjustment or parallel
        if (x >= 10)//limit only to 10
            return;
        int usL = 0, usR = 0;
        for (int y = 0; y < 5; y++)//sometimes they fluctuate
        {
            usL += US1.getValue();
            usR += US2.getValue();
            waiter.sleep(50); //break required between each reading
        }
        if (usL == usR)
            return;

        boolean direction;//true = US1 > US2
        if (US1.getValue() > US2.getValue()) {
            JustMove(speedR, speedL);
            direction = true;
        }
        else if (US1.getValue() < US2.getValue()){
            JustMove(speedR * -1, speedL * -1);
            direction = false;
        }
        else
            direction = true;

        double now = waiter.time;
        while(waiter.opModeIsActive()
                &&((US1.getValue() > US2.getValue()) == direction)
                && (waiter.time - now) < 1.0-0.05*(10-x)){
            waiter.sleep(50);
        }
        Stop();

        if (speed > minSpeed)
            ParallelRecursion(x+1, speed-0.01);//getting slower for minor adjustments
        else
            ParallelRecursion(x, speed);
    }

    /**
     *not very accurate? reading < actual?
     * @param speed [-1.0, 1.0]
     * @param threshold in cm, >20cm
     */
    public void MoveTillUS(double speed, double threshold)
    {
        int x = 0;
        double[] data = new double[20];
//        String output = " ";
        JustMove(speed, speed);
        do
        {
            if (x % 2 == 0)
                data[x/2] = US1.getValue()*USfactor;
            my_wait(0.05);
            x++;
        }while(waiter.opModeIsActive()&& (US1.getValue()*USfactor)> threshold);
        Stop();
    }
}
