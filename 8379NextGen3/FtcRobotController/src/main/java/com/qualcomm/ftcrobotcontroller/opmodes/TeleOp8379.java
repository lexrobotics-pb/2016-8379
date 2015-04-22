package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.robocol.Telemetry;


/**
 * Created by May Tomic on 3/25/2015.
 */
public class TeleOp8379 extends OpMode{

    final static double DEADZONE= 0.1;

    double posGrabber;

    double posHood;

    double posArm;

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
    Servo arm;
    Servo holder;//nothing for continuous servo
    Servo trigger;

    //Telemetry telemetry = new Telemetry();

    ElapsedTime totaltime = new ElapsedTime();

    //private TeleOpThreads threads = new TeleOpThreads();
    public TeleOp8379(){

    }

    @Override
    public void start (){
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower= hardwareMap.dcMotor.get("motorThrower");
        motorLift= hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");
        arm = hardwareMap.servo.get("arm");

        /*initialization*/
        posGrabber = 1;
        posHood = 0.4;
        posTrigger = 0.714;
        posHolder = 0.5;
        posArm = 0.1; // NO LONGER CONTINUOUS....but 0.00 ccw top speed, 1.00 cw top speed, .511 stop speed for continuous
        frontback = -1;

        grabber.setPosition(posGrabber);
        hood.setPosition(posHood);
        trigger.setPosition(posTrigger);
        holder.setPosition(posHolder);
        arm.setPosition(posArm);


    }

    @Override
    public void loop (){

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
        if (gamepad1.back){     //thrower reverse
            motorThrower.setPower(0.5);
        }
        if (gamepad1.b){     //thrower forward
            motorThrower.setPower(-1);
        }
        //arm-----------------------------------------------------
        if (gamepad1.right_bumper){     //arm out
            arm.setPosition(.87);//ccw rotation
        }
        if (gamepad1.right_trigger>=0.1){     //arm in
            arm.setPosition(0.1);//cw rotation
        }
        //change direction----------------------------------------
       /* if (gamepad1.a){      //grabber front
            frontback = -1;
        }
        if (gamepad1.y){      // flipper front
            frontback = 1;
        }*/

        /*------------------------Secondary----------------------*/

        //grabber-------------------------------------------------
        if (gamepad1.y) {   //grabber up
            grabber.setPosition(1.0);
        }
        if (gamepad1.a) {   //grabber down
            grabber.setPosition(0.588);
        }

        //hood---------------------------------------------------
        if (gamepad1.left_bumper){    //hood in
            //holder.setPosition(0.176);
            hood.setPosition(0.34);
        }
        if (gamepad1.left_trigger>=0.1){     //hood out
            //holder.setPosition(0.392);
            hood.setPosition(0.5);
        }
        //lift--------------------------------------------------
        /*while (gamepad1.dpad_up){   //lift up
           motorLift.setPower(0.50);
           // while (gamepad2.right_bumper){}
           // motorLift.setPower(0.00);

        }
        while (gamepad1.dpad_down){   //lift down
            motorLift.setPower(-0.50);
            //while(gamepad2.right_trigger>=0.1)
            //{}
            //motorLift.setPower(0.00);
        }

        if (gamepad2.b){    //trigger down
           trigger.setPosition(0.111);
        }*/
    }


    @Override
    public void stop(){}
}