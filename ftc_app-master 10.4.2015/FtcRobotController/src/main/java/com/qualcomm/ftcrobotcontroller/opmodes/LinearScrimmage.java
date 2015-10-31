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
    //GyroSensor gyro;

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
        //gyro = hardwareMap.gyroSensor.get("gyro");

        waitForStart();
        calibrate();


        while (opModeIsActive()) {
//            move(0.9,50.0);
//            wait1Msec(10000);
            //turnWithGyro(0.9,90);

            color.enableLed(true);
            telemetry.addData("Red  ", color.red() - CALIBRATE_RED);
            telemetry.addData("Blue ", color.blue() - CALIBRATE_BLUE);
            telemetry.addData("Calibrate red", CALIBRATE_RED);
            telemetry.addData("Calibrate blue ", CALIBRATE_BLUE);
            telemetry.addData("Adjusted Red  ", color.red() - CALIBRATE_RED);
            telemetry.addData("Adjusted Blue ", color.blue() - CALIBRATE_BLUE);
            print(color.red(), color.blue());
//            waitOneFullHardwareCycle();
        }
    }

    /**
     *
     * @param speed [-1, 1],
     * @param distance > 0, in cm
     */
    public void move(double speed, double distance)
    {
        resetEncoders();
        JustMove(speed, speed);
        int i=0;
        while((motorBackLeft.getCurrentPosition()-ENCODER_B_L)/encoderV < distance/circumference)
        {
            i++;
            telemetry.addData("ran", i);
            telemetry.addData("cycles stop at",distance/circumference );
            telemetry.addData("encoderBL",ENCODER_B_L);
            telemetry.addData("current value", motorBackLeft.getCurrentPosition());
            telemetry.addData("cycles",(motorBackLeft.getCurrentPosition()-ENCODER_B_L)/encoderV);
        }
        stop();
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
        motorFrontLeft.setPower(speedLeft);
        motorBackLeft.setPower(speedLeft);
        motorBackRight.setPower(speedRight);
        motorFrontRight.setPower(speedRight);
    }

    public void Stop(double speed)
    {
        motorBackLeft.setPower(speed);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(speed);
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
     * @param speed [-1, 1]
     * @param degrees angle in degree not in radians [-180, 180],positive = ccw, negative = cw
     */
    /*public void turnWithGyro(double speed, double degrees) {
        double initial = gyro.getRotation();//the gyro is
        double current;


        if (degrees > 0) {
            RIGHT_SPEED = speed;
            LEFT_SPEED = -speed;
        }
        else {
            RIGHT_SPEED = -speed;
            LEFT_SPEED = speed;
        }
        JustMove(RIGHT_SPEED,LEFT_SPEED);
        do {
            current = gyro.getRotation();
            if (degrees > 0)
                if (current <= initial)
                    current += 255;
            else
                 if (current >= initial)
                     current -= 255;
        }while (Math.abs(current - initial) <= Math.abs(degrees));
        Stop();
    }*/

    /**
     *
     * @param time in 1/1000 sec
     */
    public void wait1Msec(double time)
    {
        ElapsedTime waitTime = new ElapsedTime();
        waitTime.reset();
        waitTime.startTime();
        while (waitTime.time() * 1000 < time){}
    }

}


