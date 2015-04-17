package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.LegacyModule;
/**
 * Created by zht on 4/16/2015.
 */
public class simple_center_goal extends OpMode{


    private boolean isUp = false;
    int counter = 0;

    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;

    GyroSensor gyro;

    Servo trigger;
    Servo arm;


    @Override
    public void start(){

        telemetry.addData("Robot", "Constructor start");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorLift = hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");

        trigger = hardwareMap.servo.get("trigger");

        trigger.setPosition(0.714);
    }

    @Override
    public void run(){


    }

    @Override
    public void stop(){}


    public boolean alignRecursiveT()//true = we are all set, false = nope not even touching now and need to realign
    {
//        if (TSreadState(TOUCHfront) == 1 && TSreadState(TOUCHback) == 1)// if both of them are touching
        {
            //playSound(soundUpwardTones);
            //robot.wait1Msec(1000);
            //return true;
        }
        if (counter >= 10){
            //playSound(soundDownwardTones);
            robot.wait1Msec(1000);
            return false;
        }

        counter++;
        boolean result;
 /*       if (TSreadState(TOUCHfront) == 1 || TSreadState(TOUCHback) == 1)//run if at least one of them is touching, else... it is just unfortunate
        {

            //		nxtDisplayCenteredTextLine(2, "%d, %d", TSreadState(TOUCHfront), TSreadState(TOUCHback));
            //mecMove(10, 90, 0, 2);
            short reading1 = TSreadState(TOUCHfront), reading2 = TSreadState(TOUCHback);
            short direction = TSreadState(TOUCHfront)? 1:-1;//if only the front sensor is active, move forward
            int tempspeed = 10*direction;//positive speed = forward, negative = backward
            mecJustMove(tempspeed, 0, 0);
            while(TSreadState(TOUCHfront) == reading1 && TSreadState(TOUCHback) == reading2)//stop if at least one of them is different from the beginning
            {
                if (counter>=10)
                    break;
                nxtDisplayCenteredTextLine(2, "%d, %d", TSreadState(TOUCHfront), TSreadState(TOUCHback));
            }
            robot.Stop();
            result = alignRecursiveT();
            //		result==true?	playSound(soundUpwardTones): playSound(soundDownwardTones);
            return result;
        }*/

        //       else
        {
            robot.moveTillTouch(0.1, 90, 0, true);
            result = alignRecursiveT();
            //		result==true?	playSound(soundUpwardTones): playSound(soundDownwardTones);
            return result;
        }
    }



}
