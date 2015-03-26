package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by May Tomic on 3/25/2015.
 */
public class TeleOp8379 extends OpMode{

    final static double DEADZONE= 0.1;

    double posGrabber;

    double posHood;

    double posTrigger;

    double posHolder;

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;


    Servo grabber;
    Servo hood;
    Servo USbackservo;
    Servo holder;
    Servo trigger;



    public TeleOp8379(){

    }

    @Override
    public void start (){
        motorFrontRight = hardwareMap.dcMotor.get("frontright");
        motorBackRight = hardwareMap.dcMotor.get("backright");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower= hardwareMap.dcMotor.get("thrower");
        motorLift= hardwareMap.dcMotor.get("lift");
        motorFrontLeft = hardwareMap.dcMotor.get("frontleft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("backleft");

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        USbackservo = hardwareMap.servo.get("USbackservo");
        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

        /*initialization*/
        //Convert to range [-1, 1]
        posGrabber = 255;
        posHood = 50;
        posTrigger = 182;
        posHolder = 127;
    }

    public void run (){

        /*Sets joystick deadzone. The joysticks we used before went to +/- 100,
        but these tutorials all use +/- 1...so the deadzone should be .1 instead of 10, I guess?.
        Also, checking the documentation, there is a class called
        public void setJoystickDeadZone(float deadzone),
        but I couldn't figure out how to implement it*/

        if (Math.abs(gamepad1.left_stick_x) < DEADZONE){
            gamepad1.left_stick_x = 0;
        }
        if (Math.abs(gamepad1.left_stick_y) < DEADZONE){
            gamepad1.left_stick_y = 0;
        }
        if (Math.abs(gamepad1.right_stick_x) < DEADZONE){
            gamepad1.right_stick_x = 0;
        }
        if (Math.abs(gamepad1.right_stick_y) < DEADZONE){
            gamepad1.right_stick_y = 0;
        }

        /*---------------------Movement----------------------------*/
        //scaled to range [-1, 1] - Kara
        motorFrontLeft.setPower((gamepad1.left_stick_y + ((gamepad1.right_stick_x + gamepad1.left_stick_x) / 2))/200.0);
        motorBackLeft.setPower((gamepad1.left_stick_y - ((gamepad1.right_stick_x + gamepad1.left_stick_x) / 2))/200.0);
        motorFrontRight.setPower((gamepad1.right_stick_y - ((gamepad1.right_stick_x + gamepad1.left_stick_x) / 2))/200.0);
        motorBackRight.setPower((gamepad1.right_stick_y + ((gamepad1.right_stick_x + gamepad1.left_stick_x) / 2))/200.0);

        /*------------------------Primary------------------------*/
        //Thrower
        if (gamepad1.x){ //thrower stop
            motorThrower.setPower(0);
        }
        if (gamepad1.left_bumper){ //thrower forward
            motorThrower.setPower(1);
        }
        /*if (gamepad1.left_trigger){ //thrower reverse
            motorThrower.setPower(-.5);
        }*/
        //arm
        if (gamepad1.right_bumper){ //arm out

        }
        //if (gamepad1.right_trigger){} //arm in




        /*------------------------Secondary----------------------*/



    }



    public void stop(){

    }
}
