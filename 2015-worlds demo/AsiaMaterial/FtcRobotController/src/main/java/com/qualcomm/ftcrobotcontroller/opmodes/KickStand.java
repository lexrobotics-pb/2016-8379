/* Contributor: Kara Luo
 * Last Modified:
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

public class KickStand extends OpMode {
    final double encoderScale=1120.0;
    final double wheelRadius=((9.7)/2);
    final double wheelCircumference=Math.PI*2*wheelRadius;

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo grabber;
    Servo hood;
    Servo arm;
    Servo holder;
    Servo trigger;

    GyroSensor gyro;


	/**
	 * Constructor
	 */
	public KickStand() {

	}


	@Override
	public void start() {
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower= hardwareMap.dcMotor.get("motorThrower");
        motorLift= hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");

        gyro = hardwareMap.gyroSensor.get("gyro");

        grabber = hardwareMap.servo.get("grabber");
        arm = hardwareMap.servo.get("arm");
        hood = hardwareMap.servo.get("hood");
        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

        grabber.setPosition(1);
        arm.setPosition(0.51);
        hood.setPosition(0.235);
        trigger.setPosition(0.714);
        holder.setPosition(0.51);
	}

	@Override
	public void run() {
        grabber.setPosition(1.00);
        arm.setPosition(0.8);
        hood.setPosition(0.235);
        holder.setPosition(0.51);

        double FrontLeft=0.0, BackLeft=0.0, FrontRight=0.0, BackRight=0.0;

        if (this.time <= 3.3) {
            //mecJustMove(78, 90, 0);
            FrontLeft=(Math.sin(Math.PI/4*3));
            FrontRight=(Math.cos(Math.PI/4*3));
            BackLeft=(Math.cos(Math.PI/4*3));
            BackRight=(Math.sin(Math.PI/4*3));
            armOut();
        } else if (this.time > 5 && this.time <= 7) {
           //mecJustMove(78, 0, 0);
            FrontLeft=(-Math.sin(Math.PI/4));
            FrontRight=(-Math.cos(Math.PI/4));
            BackLeft=(-Math.cos(Math.PI/4));
            BackRight=(-Math.sin(Math.PI/4));
        } else if (this.time > 9 && this.time <= 11) {
            //mecJustMove(-78, 0, 0);
            FrontLeft=(Math.sin(Math.PI/4));
            FrontRight=(Math.cos(Math.PI/4));
            BackLeft=(Math.cos(Math.PI/4));
            BackRight=(Math.sin(Math.PI/4));
        } else if (this.time > 12 && this.time <= 16) {
            //mecJustMove(78, -90, 0);
            FrontLeft=(Math.sin(-Math.PI/4));
            FrontRight=(Math.cos(-Math.PI/4));
            BackLeft=(Math.cos(-Math.PI/4));
            BackRight=(Math.sin(-Math.PI/4));
        } else if (this.time >18 && this.time<=18.5){
            //mecJustMove(78,90,0);
            FrontLeft=(Math.sin(Math.PI/4*3));
            FrontRight=(Math.cos(Math.PI/4*3));
            BackLeft=(Math.cos(Math.PI/4*3));
            BackRight=(Math.sin(Math.PI/4*3));
        } else if(this.time > 18.5 && this.time <=19.5){
           //mecJustMove(78, 0, 0);
            FrontLeft=(-Math.sin(Math.PI/4));
            FrontRight=(-Math.cos(Math.PI/4));
            BackLeft=(-Math.cos(Math.PI/4));
            BackRight=(-Math.sin(Math.PI/4));
            armIn();
        }

        motorBackLeft.setPower(BackLeft);
        motorBackRight.setPower(BackRight);
        motorFrontLeft.setPower(FrontLeft);
        motorFrontRight.setPower(FrontRight);

	}

	@Override
	public void stop() {

	}

   /* public void mecJustMove(double speed, double degrees, double speedRotation)
    {
        speed/=100.0;
        speedRotation/=100.0;
        double radians = toRadians(degrees);
        motorFrontLeft.setPower(speed * Math.sin(radians + Math.PI/4) + speedRotation);
        motorFrontRight.setPower(speed * Math.cos(radians + Math.PI/4) - speedRotation);
        motorBackLeft.setPower(speed * Math.cos(radians + Math.PI/4) + speedRotation);
        motorBackRight.setPower(speed * Math.sin(radians + Math.PI/4) -  speedRotation);
    }

    private double toRadians (double degrees)
    {
        return degrees/180.0*Math.PI;
    }*/

    public void armOut(){
        arm.setPosition(0.8);
    }

    public void armIn(){
        arm.setPosition(0.1);
    }
}
