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

    int frontback;

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
        posGrabber = 1;
        posHood = -0.606;
        posTrigger = 0.433;
        posHolder = 0;//how does a continuous servo works? was 127 meaning stop
        frontback = 1;

        grabber.setPosition(posGrabber);
        hood.setPosition(posHood);
        trigger.setPosition(posTrigger);
        holder.setPosition(posHolder);


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
        motorFrontLeft.setPower(frontback * ((gamepad1.left_stick_y + (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));
        motorBackLeft.setPower(frontback * ((gamepad1.left_stick_y - (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));
        motorFrontRight.setPower(frontback * ((gamepad1.right_stick_y - (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));
        motorBackRight.setPower(frontback * ((gamepad1.right_stick_y + (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));

        /*------------------------Primary------------------------*/
        //Thrower
        if (gamepad1.x){ //thrower stop
            motorThrower.setPower(0);
        }
        if (gamepad1.left_bumper){     //thrower reverse
            motorThrower.setPower(0.5);
        }
        if (gamepad1.left_trigger>=0.1){     //thrower forward
            motorThrower.setPower(-1);
        }
        //arm-----------------------------------------------------
        if (gamepad1.right_bumper){     //arm out

        }
        if (gamepad1.right_trigger>=0.1){     //arm in

        }
        //change direction----------------------------------------
        if (gamepad1.a){      //grabber front
            frontback = -1;
        }
        if (gamepad1.y){      // flipper front
               frontback = 1;
        }

        /*------------------------Secondary----------------------*/

        //grabber-------------------------------------------------
        if (gamepad2.y) {   //grabber up
            grabber.setPosition(1.0);
        }
        if (gamepad2.a) {   //grabber down
            grabber.setPosition(0.588);
        }

        //hood---------------------------------------------------
        if (gamepad2.left_bumper){    //hood in
            hood.setPosition(-0.92);
            holder.setPosition(-.5); // I put a number in here so it wouldn't freak out IT IS A RANDOM NUMBER!!!!!
        }
        if (gamepad2.left_trigger>=0.1){     //hood out
            holder.setPosition(.5); // I put a number in here so it wouldn't freak out IT IS A RANDOM NUMBER!!!!!
            hood.setPosition(.016);
        }
        //lift--------------------------------------------------
        if (gamepad2.right_bumper){   //lift up

        }
        if (gamepad2.right_trigger>=0.1){   //lift down

        }
        if (gamepad2.b){    //trigger down

        }

    }



    public void stop(){

    }
}
