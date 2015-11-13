/* Contributor: Kara Luo
 * Last Modified:
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

public class KickStand3 extends OpMode {
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
	public KickStand3() {

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
	public void loop() {
        grabber.setPosition(1.00);
        arm.setPosition(0.8);
        hood.setPosition(0.235);
        holder.setPosition(0.51);

        double[] speed = new double[4];

        for (int i=0; i<4; i++)
        {
            speed[i]=0.0;
        }

        if (this.time <= 1.0) {
            speed=mecJustMove(-78, -90, 0);
        } else if (this.time > 2 && this.time <= 6) {
            speed=mecJustMove(-100, 0, 0);
        } else if (this.time > 7 && this.time <= 11) {
            speed=mecJustMove(100, 0, 0);
        }

        motorFrontLeft.setPower(speed[0]);
        motorFrontRight.setPower(speed[1]);
        motorBackLeft.setPower(speed[2]);
        motorBackRight.setPower(speed[3]);
    }

    @Override
    public void stop() {

    }

    public double[] mecJustMove(double speed, double degrees, double speedRotation)
    {
        double[] speedWheel = new double[4];
        speed/=100.0;
        speedRotation/=100.0;
        double radians = toRadians(degrees);
        speedWheel[0]=(speed * Math.sin(radians + Math.PI/4) + speedRotation);
        speedWheel[1]=(speed * Math.cos(radians + Math.PI/4) - speedRotation);
        speedWheel[2]=(speed * Math.cos(radians + Math.PI/4) + speedRotation);
        speedWheel[3]=(speed * Math.sin(radians + Math.PI/4) -  speedRotation);

        return speedWheel;
    }

    private double toRadians (double degrees)
    {
        return degrees/180.0*Math.PI;
    }

    public void armOut(){
        arm.setPosition(0.8);
    }

    public void armIn(){
        arm.setPosition(0.1);
    }
}
