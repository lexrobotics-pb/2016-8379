package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;


/**
 * Created by Kara Luo on 10/30/2015.
 */
public class LinearScrimmage extends LinearOpMode {
    ColorSensor color;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    double LEFT_SPEED = 0.0;
    double RIGHT_SPEED = 0.0;
    double ENCODER_F_R = 0;
    double ENCODER_F_L = 0;
    double ENCODER_B_R = 0;
    double ENCODER_B_L = 0;
    GyroSensor gyro;

    @Override
    public void runOpMode() throws InterruptedException {
        color = hardwareMap.colorSensor.get("color");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //forwards front left motor
        gyro = hardwareMap.gyroSensor.get("gyro");

        waitForStart();
        calibrate();


        while (opModeIsActive()) {
            double leftEncoder = motorFrontLeft.getCurrentPosition();
            double rightEncoder = motorFrontRight.getCurrentPosition();

            motorBackLeft.setPower(0.9);
            motorFrontLeft.setPower(-0.9);
            while(motorBackLeft.getCurrentPosition()<leftEncoder+500) {
            }

            motorBackLeft.setPower(0.0);
            motorFrontLeft.setPower(0.0);

            color.enableLed(true);
            telemetry.addData("Red  ", color.red() - CALIBRATE_RED);
            telemetry.addData("Blue ", color.blue() - CALIBRATE_BLUE);
            telemetry.addData("Calibrate red", CALIBRATE_RED);
            telemetry.addData("Calibrate blue ", CALIBRATE_BLUE);
            telemetry.addData("Adjusted Red  ", color.red() - CALIBRATE_RED);
            telemetry.addData("Adjusted Blue ", color.blue() - CALIBRATE_BLUE);
            print(color.red(), color.blue());
            waitOneFullHardwareCycle();
        }
    }

    public void calibrate()
    {
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

    public void JustMove(double speedRight, double speedLeft)
    {
//        switchAllToWrite();

        motorFrontLeft.setPower(speedLeft);
        motorBackLeft.setPower(speedLeft);
        motorBackRight.setPower(speedRight);
        motorFrontRight.setPower(speedRight);

//        switchAllToRead();
    }

    public void Stop()
    {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        resetEncoders();
    }

    public void resetEncoders()
    {
        ENCODER_F_R = motorFrontRight.getCurrentPosition();
        ENCODER_F_L = motorFrontLeft.getCurrentPosition();
        ENCODER_B_R = motorBackRight.getCurrentPosition();
        ENCODER_B_L = motorBackLeft.getCurrentPosition();

    }


    /**
     * turn the robot on the spot
     * @param speed [-1, 1], negative speed = ccw; positive = cw
     * @param degrees angle in degree not in radians,
     */
    public void turnWithGyro(double speed, double degrees) {
        double initial = gyro.getRotation();
        double current;
        double speedRight;
        double speedLeft;
        if (degrees < 0) {
            speedRight = speed;
            speedLeft = -speed;
        }
        else {
            speedRight = -speed;
            speedLeft = speed;
        }
        JustMove(speedRight,speedLeft);
        do {
            current = gyro.getRotation();
            if (current > )
        }

        while (gyro.getRotation() - initial <= degrees)
        {}
        Stop();
    }

    public void wait1Msec(double time)
    {
        ElapsedTime waitTime = new ElapsedTime();
        waitTime.reset();
        waitTime.startTime();
        while (waitTime.time() * 1000 < time){}
    }

}


