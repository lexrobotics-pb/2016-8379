package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by Kara Luo on 10/2/2015.
 */
public class Meet1Auto extends OpMode {

    final static double DEADZONE= 0.1;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo push;

    ColorSensor color;

    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    double ENCODER_F_R = 0.0;
    double ENCODER_F_L = 0.0;
    double ENCODER_B_R = 0.0;
    double ENCODER_B_L = 0.0;

    double circumference = 4.0 * 2.54 * Math.PI;
    double encoderV = 1120.0;

    int frontback;
    Telemetry telemetry = new Telemetry();


    @Override
    public void init()
    {
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.FORWARD); //forwards back left motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.FORWARD); //forwards front left motor

        color = hardwareMap.colorSensor.get("color");

        push = hardwareMap.servo.get("push");

    }

    @Override
    public void loop()
    {
        calibrate();
        move(1, 50.0);
    }

    @Override
    public void stop()
    {

    }

    public void move(double speed, double distance)
    {
        resetEncoders();
        JustMove(speed, speed);
        int i=0;
        while((motorBackLeft.getCurrentPosition()-ENCODER_B_L)/encoderV < (distance/circumference))
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
    }

    public void resetEncoders()
    {
        ENCODER_F_R = motorFrontRight.getCurrentPosition();
        ENCODER_F_L = motorFrontLeft.getCurrentPosition();
        ENCODER_B_R = motorBackRight.getCurrentPosition();
        ENCODER_B_L = motorBackLeft.getCurrentPosition();

    }

}
