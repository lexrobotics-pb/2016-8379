package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Eula on 4/17/2015.
 *
 */
public class Ramp30 extends OpMode{


    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;

    Servo grabber;
    Servo hood;

    GyroSensor gyro;
    double[] speeds;
    double[] speed = {0, 0, 0, 0};

    @Override
    public void start(){
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorThrower = hardwareMap.dcMotor.get("motorThrower");
        gyro = hardwareMap.gyroSensor.get("gyro");
        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        grabber.setPosition(1);
        hood.setPosition(0.3);
    }

    @Override
    public void run() {
        speeds = speed;
        if (this.time <= 3.5) {
            speeds = mecJustMove(-60.0, 0, 0);
        } else if (this.time > 3.5 && this.time < 5.0) {
            speeds = mecJustMove(78.0, 90.0, 0);
        } else if (this.time > 5.0 && this.time < 10.0) {
            speeds = mecJustMove(-78.0, 0, 0);
        }else if (this.time > 10.0 && this.time < 11.0){
            grabber.setPosition(0.588);
        }else if (this.time > 16.0 && this.time < 16.4){
            speeds = mecJustMove(78.0, 0, 0);
        }else if (this.time > 16.4 && this.time < 16.6){
            hood.setPosition(0.7);
        }else if (this.time > 16.6 && this.time < 17.0){
            speeds = mecJustMove(78.0, 90, 0);
        }else if (this.time > 17.0 && this.time < 25.0){
            speeds = mecJustMove(78.0, 0, 0);
        }else if (this.time > 25.0 && this.time < 28.0) {
            speeds = mecJustMove(0, 0, 78);
        }else if (this.time > 28.0 && this.time < 32.0) {
            speeds = mecJustMove(-78.0, 90, 0);
        }
        if (this.time > 17.0 && this.time < 30.0){
            motorThrower.setPower(-1.0);
        }
        motorFrontLeft.setPower(speeds[0]);
        motorFrontRight.setPower(speeds[1]);
        motorBackLeft.setPower(speeds[2]);
        motorBackRight.setPower(speeds[3]);

    }

    @Override
    public void stop(){

    }

    public double[] mecJustMove(double speed, double degrees, double speedRotation)
    {
        speed/=100.0;
        speedRotation/=100.0;
        double radians = toRadians(degrees);
        double[] speeds = new double[4];

        speeds[0] = speed * Math.sin(radians + Math.PI/4) + speedRotation;//front left
        speeds[1] = speed * Math.cos(radians + Math.PI/4) - speedRotation; //front right
        speeds[2] = speed * Math.cos(radians + Math.PI/4) + speedRotation; //back left
        speeds[3] = speed * Math.sin(radians + Math.PI/4) -  speedRotation;//back right
        return speeds;
    }

    double toRadians (double degrees)
    {
        double radians=degrees/180.0*Math.PI;
        return radians;
    }
}
