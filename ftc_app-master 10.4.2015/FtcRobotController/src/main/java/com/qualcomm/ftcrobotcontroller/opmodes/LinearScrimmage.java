package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;
import com.qualcomm.robotcore.hardware.I2cDevice;


/**
 * Created by Kara Luo on 10/30/2015.
 */
public class LinearScrimmage extends LinearOpMode {
    ColorSensor color;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    AnalogInput light;
    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    double ENCODER_F_R = 0;
    double ENCODER_F_L = 0;
    double ENCODER_B_R = 0;
    double ENCODER_B_L = 0;
    I2cDevice gyro;

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
        light = hardwareMap.analogInput.get("light");
        gyro = hardwareMap.i2cDevice.get("gyro");
        gyro.enableI2cReadMode(0x20, 0x05, 1);
        gyro.enableI2cWriteMode(0x20, 0x30, 1);
//       I2cDeviceReader gyroReading = new I2cDeviceReader(gyro, 0x20, 0x70, 1);

        waitForStart();
        calibrate();


        while (opModeIsActive()) {
            telemetry.addData("EOPD", light.getValue());
            //move(0.9, 50.0);
            //wait1Msec(10000);
            //turnWithGyro(0.9, 90);
            //turnWithGyro(0.9,90);m
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
        waitOneFullHardwareCycle();
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
        if(!gyro.isI2cPortInReadMode()){
            gyro.enableI2cReadMode(0x20, 0x05, 1);
        }
        double initial = gyro.getCopyOfReadBuffer()[0];
        double current;
        double left;
        double right;
        telemetry.addData("Gyro", gyro.getCopyOfReadBuffer());

        if (degrees > 0) {
            right = -speed;
            left = speed;
        } else {
            right = speed;
            left = -speed;
        }
        JustMove(right, left);
        do {
            current = gyro.getCopyOfReadBuffer()[0];
            if (degrees > 0)
                if (current <= initial)
                    current += 255;
                else if (current >= initial)
                    current -= 255;
        } while (opModeIsActive()&&Math.abs(current - initial) <= Math.abs(degrees));
        Stop();
    }

    /**
     * @param time in 1/1000 sec
     */
    public void wait1Msec(double time) {
        ElapsedTime waitTime = new ElapsedTime();
        waitTime.reset();
        waitTime.startTime();
        while (waitTime.time() * 1000 < time) {
        }
    }

}


