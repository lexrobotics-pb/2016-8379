package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by zht on 4/2/2015.
 */
public class CenterGoal extends OpMode {

    private Robot robot = new Robot();
    DcMotor motorLift;
    Servo trigger;
    public boolean isUp = false;

    private int counter = 0;

    public void CenterGoal() {

        trigger=hardwareMap.servo.get("trigger");
        motorLift= hardwareMap.dcMotor.get("lift");
    }

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

    public void liftUp()
    {
	/*nMotorEncoder[Lift]=0;
	motor[Lift]=-100;
	while(abs(nMotorEncoder[Lift])<encoderScale*13.5) //up ratio -38/(255-127) = -.297
	{
	}
	motor[Lift]=0;*/
       // nMotorEncoder[Lift]=0;
        motorLift.setPower(-1);
 //       while(Math.abs(nMotorEncoder[Lift])<encoderScale*10.7) //up ratio -38/(255-127) = -.297
        {
        }
        motorLift.setPower(0);

        ElapsedTime liftTime = new ElapsedTime();
        liftTime.startTime();
        motorLift.setPower(-1);
        while(liftTime.time()/1000<460){
        }
        motorLift.setPower(0);
        isUp = true;
    }

    public void liftDown()
    {
     //   nMotorEncoder[Lift]=0;
        motorLift.setPower(0.5);
        //	while(abs(nMotorEncoder[Lift])<encoderScale*9.0) //!!REMBER TO CHANGE TO THIS!!!
 //       while(abs(nMotorEncoder[Lift])<encoderScale*11.0)
        {
        }
        motorLift.setPower(0);
        robot.wait1Msec(5000);
    }

    public void kickstand()
    {
        robot.mecMove(0.7, 180, 0, 40);
        robot.turnMecGyro(-0.6, 82.0);
        robot.mecMove(-0.78, 0, 0, 25);
 //       armOut();
        robot.mecMove(-0.78, 0, 0, 80);
        robot.mecMove(0.78, 0, 0, 100);
   //     armIn();
    }

   public  void endSequence() //scores balls, lowers lift, and knocks kickstand
    {
        alignRecursiveT(); //aligns robot so both touch sensors hit
        robot.wait1Msec(500);
        robot.mecMove(-0.55, 0, 0, 10.8); //shift right to align lift/ramp with center goal
        robot.wait1Msec(500);
        robot.mecMove(0.6, 270, 0, 3); //shift back
        robot.wait1Msec(500);
        //robot.ballRelease(); //release balls with servo
        robot.wait1Msec(2000);
        robot.mecMove(0.6, 270, 0, 15);//move backwards
    }







    public void start(){}
    public void stop(){}
    public void run(){}
}
