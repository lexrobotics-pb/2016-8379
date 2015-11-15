package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;


/**
 * Created by Kara Luo on 10/30/2015.
 */
public class LinearScrimmage extends LinearOpMode {
    ColorSensor color;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    //    AnalogInput light;
    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    double ENCODER_F_R = 0;
    double ENCODER_F_L = 0;
    double ENCODER_B_R = 0;
    double ENCODER_B_L = 0;
    //    I2cDevice gyro;
    GyroSensor gyro;
    Servo push;

    double circumference = 4.0 * 2.54 * Math.PI, encoderV = 1120.0;

    @Override
    public void runOpMode() throws InterruptedException {
        color = hardwareMap.colorSensor.get("color");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.FORWARD); //forwards back left motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.FORWARD); //forwards front left motor

        gyro = hardwareMap.gyroSensor.get("gyro");
        push = hardwareMap.servo.get("push");
        gyro.calibrate();

        waitForStart();

        while (gyro.isCalibrating()) {
            telemetry.addData("log", "calibrating");
            Thread.sleep(50);
        }


        push.setPosition(0.7);
        my_wait(1.0);
        push.setPosition(0.3);
        my_wait(3.5);
        push.setPosition(0.5);

        telemetry.addData("done", "setting position");

        gyro.resetZAxisIntegrator();
        telemetry.addData("done", "done");
        sleep(1000);

        turnWithGyro(0.5, 90);

        while (opModeIsActive()) {
            telemetry.addData("gyro", gyro.getHeading());
            Thread.sleep(100);
        }
//            telemetry.addData("gyro2", gyro.getRotation());
//            push.setPosition(0);
        //           turnWithGyro(0.9, 90.0);
        //move(0.9, 50.0);
        //wait1Msec(10000);
        //turnWithGyro(0.9, 90);
        //turnWithGyro(0.9,90);
        //telemetry.addData("gyro", gyro.getCopyOfReadBuffer());
        //color.enableLed(true);
            /*telemetry.addData("Red  ", color.red() - CALIBRATE_RED);
            telemetry.addData("Blue ", color.blue() - CALIBRATE_BLUE);
            telemetry.addData("Calibrate red", CALIBRATE_RED);
            telemetry.addData("Calibrate blue ", CALIBRATE_BLUE);
            telemetry.addData("Adjusted Red  ", color.red() - CALIBRATE_RED);
            telemetry.addData("Adjusted Blue ", color.blue() - CALIBRATE_BLUE);
            print(color.red(), color.blue());*/
//        }
    }

    /**
     * @param speed    [-1, 1],
     * @param distance > 0, in cm
     */
    public void move(double speed, double distance) {
        resetEncoders();
        JustMove(speed, speed);
        int i = 0;
        while (opModeIsActive() && (motorBackLeft.getCurrentPosition() - ENCODER_B_L) / encoderV < distance / circumference) {
            i++;
            telemetry.addData("ran", i);
            telemetry.addData("cycles stop at", distance / circumference);
            telemetry.addData("encoderBL", ENCODER_B_L);
            telemetry.addData("current value", motorBackLeft.getCurrentPosition());
            telemetry.addData("cycles", (motorBackLeft.getCurrentPosition() - ENCODER_B_L) / encoderV);
        }
        Stop();
    }

    public void calibrate() {
        double red = 0.0;
        double blue = 0.0;
        for (int i = 0; i < 64; i++) {
            red += color.red();
            blue += color.blue();
            double time = this.time;
            while (this.time < time + 0.05) {
            }
        }
        CALIBRATE_RED = red / 64.0;
        CALIBRATE_BLUE = blue / 64.0;
    }

    public void print(double red, double blue) {
        if (red - CALIBRATE_RED > blue - CALIBRATE_BLUE) {
            telemetry.addData("compare", "red");
        } else if (red - CALIBRATE_RED > blue - CALIBRATE_BLUE) {
            telemetry.addData("compare", blue);
        } else {
            telemetry.addData("compare", "indistinguishable");
        }
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
     * @param degrees angle in degree not in radians [-180, 180],positive = cw, negative = ccw
     */
    public void turnWithGyro(double speed, double degrees) {
//        if(!gyro.isI2cPortInReadMode()){
//            gyro.enableI2cReadMode(0x20, 0x05, 1);
//        }
//        double initial = gyro.getCopyOfReadBuffer()[0];
//
        gyro.resetZAxisIntegrator();
        telemetry.addData("Gyro", gyro.getHeading());

        double left, right;

        if (degrees > 0) {
            right = -speed;
            left = speed;
        } else {
            right = speed;
            left = -speed;
        }

        JustMove(right, left);
        while (opModeIsActive() && gyro.getHeading() < degrees) {
        }
        Stop();
    }

    public void my_wait(double sec) {
        double current = this.time;
        while (this.opModeIsActive() && (this.time - current) < sec) {
        }
    }
}


