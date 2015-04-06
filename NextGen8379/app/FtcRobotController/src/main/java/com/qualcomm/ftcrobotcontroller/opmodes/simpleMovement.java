/**
* Contributor: Kara Luo
* Date Last Modified: 4/6/2015
* Class Description: [Basic Movement]
* Basic robot movement methods and helper methods
*/

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class simpleMovement {
    final double encoderScale=1120.0;
    final double wheelRadius=((9.7)/2);
    final double wheelCircumference=Math.PI*2*wheelRadius;

    final static double MOTOR_POWER = 0.00; // Higher values will cause the robot to move faster

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;
    GyroSensor gyro;
    HardwareMap hardwareMap = new HardwareMap();

    public simpleMovement(){
        motorFrontRight = hardwareMap.dcMotor.get("frontright");
        motorBackRight = hardwareMap.dcMotor.get("backright");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorFrontLeft = hardwareMap.dcMotor.get("frontleft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("backleft");

        gyro = hardwareMap.gyroSensor.get("gyro");

        motorBackLeft.setPower(0.0);
        motorBackRight.setPower(0.0);
        motorFrontLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
    }

    public void turnWithGyro(double speedrotation, double degrees)
    {
        double delTime = 0;
        double curRate = 0;
        double currHeading = 0;
        ElapsedTime Time1 = new ElapsedTime();
        stop();
        justMove (0, 0, speedrotation);//+ = right   - = turn left
        while (Math.abs(currHeading) < Math.abs(degrees)) {
            Time1.startTime();
            curRate = gyro.getRotation();
            if (Math.abs(curRate) > 3) {
                currHeading += curRate * delTime; //Approximates the next heading by adding the rate*time.
                if (currHeading > 360) currHeading -= 360;
                else if (currHeading < -360) currHeading += 360;
            }
            wait1MSec(5);
            delTime = ((double)Time1.time()) / 1000000; //set delta (zero first time around)
        }
        stop();
    }

    public void move(double speed, double degrees, double speedRotation, double distance)
    {
        double radians=toRadians(degrees);
        resetEncoders();
        double min = 0.0;
        if (Math.cos(degrees) == 0.0 || Math.sin(degrees) == 0.0)
        {
            min = 1.0;
        }
        else if (Math.abs(1.0/Math.cos(degrees))<= Math.abs(1.0 / Math.sin(degrees)))
        {
            min = 1.0/Math.cos(degrees);
        }
        else
        {
            min = 1.0/Math.sin(degrees);
        }

        double scaled = Math.abs(encoderScale * (distance * min / wheelCircumference));
        justMove(speed, degrees, speedRotation);
        {
            wait1MSec(5);
        }
        stop();
        resetEncoders();
        wait1MSec(10);
    }

    public void justMove(double speed, double degrees, double speedRotation)
    {
        double radians = toRadians(degrees);
        motorFrontLeft.setPower(speed * Math.sin(radians + Math.PI/4) + speedRotation);
        motorFrontRight.setPower(speed * Math.cos(radians + Math.PI/4) - speedRotation);
        motorBackLeft.setPower(speed * Math.cos(radians + Math.PI/4) + speedRotation);
        motorBackRight.setPower(speed * Math.sin(radians + Math.PI/4) -  speedRotation);=
    }
    
    public double toRadians (double degrees)
    {
        double radians=degrees/180.0*Math.PI;
        return radians;
    }

    public void resetEncoders()
    {

    }

    public void stop()
    {
        motorBackLeft.setPower(0.0);
        motorBackRight.setPower(0.0);
        motorFrontLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);

    }

    public void wait1MSec(int mSec)
    {
        ElapsedTime time = new ElapsedTime();
        time.startTime();
        while (time.time()/1000.0<mSec){
        }
    }
}
