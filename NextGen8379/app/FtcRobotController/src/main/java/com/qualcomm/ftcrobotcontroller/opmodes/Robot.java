package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
/**
 * Created by Eula Zhong on 4/1/2015.
 */
public class Robot extends OpMode{

    double posGrabber;

    double posHood;

    double posTrigger;

    double posHolder;

    int frontback;

    static double encoderScale=1120.0;
    static double wheelRadius=((9.7)/2);
    static double wheelCircumference=Math.PI*2*wheelRadius;
    static int counter = 0;
    static boolean isUp = false;

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo grabber;
    Servo hood;
    Servo USbackservo;
    Servo holder;//nothing for continuous servo
    Servo trigger;

    UltrasonicSensor USfront;
    UltrasonicSensor USback;

    public void Robot()
    {
        motorFrontRight = hardwareMap.dcMotor.get("frontright");
        motorBackRight = hardwareMap.dcMotor.get("backright");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower= hardwareMap.dcMotor.get("thrower");
        motorLift= hardwareMap.dcMotor.get("lift");
        motorFrontLeft = hardwareMap.dcMotor.get("frontleft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("backleft");

        USfront = hardwareMap.ultrasonicSensor.get("USfront");
        USback = hardwareMap.ultrasonicSensor.get("USback");

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        USbackservo = hardwareMap.servo.get("USbackservo");
        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

        grabber.setPosition(1);
        hood.setPosition(-0.606);
        trigger.setPosition(0.433);
        holder.setPosition(0);

        motorBackLeft.setPower(0.0);
        motorBackRight.setPower(0.0);
        motorFrontLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
        motorLift.setPower(0.0);
        motorThrower.setPower(0.0);
    }

    public void wait1Msec(double time)
    {
        ElapsedTime waitTime = new ElapsedTime();
        waitTime.startTime();
        while (waitTime.time() < time / 1000){}
    }

    public void mecJustMove(double speed, double degrees, double speedRotation)
    {
        degrees = toDegrees(degrees);
        motorFrontLeft.setPower(speed * Math.sin(degrees + 45) + speedRotation);
        motorFrontRight.setPower(speed * Math.cos(degrees + 45) - speedRotation);
        motorBackLeft.setPower(speed * Math.cos(degrees + 45) + speedRotation);
        motorBackRight.setPower(speed * Math.sin(degrees + 45) -  speedRotation);

    }

    public void mecMove(double speed, double degrees, double speedRotation, double distance)
    { //speed [-100,100], degrees [0, 360] to the right, speedRotation [-100,100], distance cm
        degrees=toDegrees(degrees);
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

        //writeDebugStreamLine("*************************");

        mecJustMove(speed, degrees, speedRotation);


//        while((Math.abs(nMotorEncoder[FrontLeft])<scaled) && (abs(nMotorEncoder[FrontRight])<scaled) && (abs(nMotorEncoder[BackLeft])< scaled) && (abs(nMotorEncoder[BackRight])< scaled))
        {
            wait1Msec(5);
//		writeDebugStreamLine("%d, %d, %d, %d ", (nMotorEncoder[FrontLeft]), (nMotorEncoder[FrontRight]), (nMotorEncoder[BackLeft]), (nMotorEncoder[BackRight]));
        }
        Stop();
        resetEncoders();
        wait1Msec(10);
    }

    void moveTillUS(double speed, double degrees, double speedRotation, double threshold, boolean till)//if till = true, move until sees something; if till = false, move until not seeing something
    {
        mecJustMove(speed, degrees, speedRotation);
        if (till){
            while (USfront.getUltrasonicLevel() > threshold){}}
        else{
            while (USfront.getUltrasonicLevel() < threshold || USback.getUltrasonicLevel() < threshold){}}//should be ||, so stop when none of them is in the threshold
        Stop();
    }

    void moveTillTouch(double speed, double degrees, double speedRotation, boolean till)
    {
 /*       mecJustMove(speed, degrees, speedRotation);
        if (till){
            while ((!TSreadState(TOUCHFront)) && (!TSreadState(TOUCHBack))){
                nxtDisplayCenteredTextLine(2, "%d, %d", TSreadState(TOUCHfront), TSreadState(TOUCHback));
                //if(HTGYROreadRot(gyro)>5){break;}
                if (counter>=10)
                    break;
            }
        }
        else
        {
            while ((TSreadState(TOUCHFront)) || (TSreadState(TOUCHBack))){
                //if(HTGYROreadRot(gyro)>5){break;}
                if (counter>=10)
                    break;
            }
        }*/
        Stop();
    }

    public void Stop()
    {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
    }

    private double toDegrees (double angle) {
        return angle * (180.0 / Math.PI);
    }

    private void resetEncoders(){
//        nMotorEncoder[FrontLeft] = 0;
//        nMotorEncoder[FrontRight] = 0;
//        nMotorEncoder[BackLeft] = 0;
//        nMotorEncoder[BackRight] = 0;
        wait1Msec(50);
    }

    public void start(){}
    public void stop(){}
    public void run(){}
}
