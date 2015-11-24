package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by Eula on 10/8/2015.
 * Last Update: 2015/11/19 by Eula
 * Use this class to configure all robot parts and store main movement of the robot for Autonomous
 * Status: Updating for Meet 1
 */

public class Robot{
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;

    ColorSensor color;
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

    Telemetry my_telemetry;
    LinearOpMode waiter;

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

    public Robot(LinearOpMode hello){
        waiter = hello;
        color = waiter.hardwareMap.colorSensor.get("color");
        motorBackRight = waiter.hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontRight = waiter.hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorBackLeft = waiter.hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.FORWARD); //forwards back left motor
        motorFrontLeft = waiter.hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.FORWARD); //forwards front left motor

        LeftTrigger = waiter.hardwareMap.servo.get("LeftTrigger");
        RightTrigger = waiter.hardwareMap.servo.get("RightTrigger");
        push = hello.hardwareMap.servo.get("push");

        gyro = waiter.hardwareMap.gyroSensor.get("gyro");

        LeftTrigger.setPosition(0.0);
        RightTrigger.setPosition(1.0);
        push.setPosition(0.5);

        gyro.calibrate();
        my_wait(1.0);
    }


    //====================================All Functions=====================================================================================================

    /**
     * @param speed    [-1, 1],
     * @param distance > 0, in cm
     */
    public void move(double speed, double distance) {
        resetEncoders();
        JustMove(speed, speed);
        int i = 0;
        while (waiter.opModeIsActive() && (motorBackLeft.getCurrentPosition() - ENCODER_B_L) / encoderV < distance / circumference) {
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
            //double time = Time;
            //while (this.time < time + 0.05) {
            //}
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

        if(speed<0){
            degrees=360-degrees;
            JustMove(right, left);
            my_wait(0.5);
            while (waiter.opModeIsActive() && gyro.getHeading() > degrees){}
        }
        else
        {
            JustMove(right, left);
            while (waiter.opModeIsActive() && gyro.getHeading() < degrees) {}
        }
        Stop();
    }

    public void my_wait(double sec) {
        double current = waiter.time;
        while (waiter.opModeIsActive() && (waiter.time - current) < sec) {
        }
    }

    public boolean isBlue()
    {
        Stop();
        double blue = 0.0;
        for (int x = 0; x < 20; x++)
        {
            blue += color.blue();
            my_wait(0.1);
        }
        blue/=20;
        blue -= CALIBRATE_BLUE;
        return blue>=1.0;
    }

    public boolean isRed()
    {
        Stop();
        double red = 0.0;
        for (int x = 0; x < 20; x++)
        {
            red +=color.red();
            my_wait(0.1);
        }
        red/=20;
        red -= CALIBRATE_RED;
        return red>=1.0;
    }
}
