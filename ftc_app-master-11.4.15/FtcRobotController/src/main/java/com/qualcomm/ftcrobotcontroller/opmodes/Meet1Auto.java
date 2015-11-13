package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Kara Luo on 10/2/2015.
 */
public class Meet1Auto extends OpMode {

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo push;

    ColorSensor color;

    double CALIBRATE_RED = 0.0;
    double CALIBRATE_BLUE = 0.0;
    int ENCODER_F_R = 0;
    int ENCODER_F_L = 0;
    int ENCODER_B_R = 0;
    int ENCODER_B_L = 0;

    double circumference = 4.0 * 2.54 * Math.PI;
    double encoderV = 1120.0;

    public Meet1Auto(){
    }

    @Override
    public void init()
    {
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");

        color = hardwareMap.colorSensor.get("color");

        push = hardwareMap.servo.get("push");
    }

    @Override
    public void loop()
    {
        //JustMove(0.9, 0.9);
        //calibrate();
        //print(color.red(), color.blue());
        move(0.9, 50.0);
    }

    //@Override
    //public void stop()
    //{
    //}

    public void move(double speed, double distance)
    {
        resetEncoders();
        motorFrontRight.setTargetPosition(ENCODER_F_R+(int)(distance/circumference*encoderV));
        motorBackRight.setTargetPosition(ENCODER_B_R+(int)(distance/circumference*encoderV));
        motorFrontLeft.setTargetPosition(ENCODER_F_L+(int)(distance/circumference*encoderV));
        motorBackLeft.setTargetPosition(ENCODER_B_L+(int)(distance/circumference*encoderV));

        //JustMove(speed, speed);
        /*while((motorBackLeft.getCurrentPosition()-ENCODER_B_L)/encoderV < (distance/circumference))
        {
            telemetry.addData("need run",distance/circumference );
            telemetry.addData("encoderBL",ENCODER_B_L);
            telemetry.addData("current value", motorBackLeft.getCurrentPosition());
            telemetry.addData("cycles",(motorBackLeft.getCurrentPosition()-ENCODER_B_L)/encoderV);
        }*/
        //Stop();
    }



    public void calibrate()
    {
        telemetry.addData("running", "calibrate");
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
        telemetry.addData("complete", "calibrate");
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

    public void Stop()
    {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
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
