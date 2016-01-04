package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Eula on 12/29/2015.
 */
public class USTest extends LinearOpMode {

    AnalogInput US1;//left
    AnalogInput US2;//right
    DcMotor FR, BR, FL, BL;
    double USfactor = 1.2;//actual_distance / reading
    double minSpeed = 0.15;


    @Override
    public void runOpMode()throws InterruptedException {
        US1 = hardwareMap.analogInput.get("US1");
        US2 = hardwareMap.analogInput.get("US2");
        FR = hardwareMap.dcMotor.get("FR");
        BR = hardwareMap.dcMotor.get("BR");
        FL = hardwareMap.dcMotor.get("FL");
        BL = hardwareMap.dcMotor.get("BL");
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("hello", "hello");
        my_wait(5.0);

        waitForStart();
//        MoveTillUS(0.2, 50);
//        while(this.opModeIsActive())
//        {
//            telemetry.addData("US1",US1.getValue());
//            telemetry.addData("US2",US2.getValue());
//            my_wait(0.05);
//        }
//        parallel(0.2);
        telemetry.addData("US1",US1.getValue());
        telemetry.addData("US2",US2.getValue());
        ParallelRecursion(0, 0.25);
        telemetry.addData("US1",US1.getValue());
        telemetry.addData("US2",US2.getValue());


    }

    public void my_wait(double sec) {
        double current = this.time;
        while ((this.time - current) < sec) {
        }
    }

    public void JustMove(double speedRight, double speedLeft) {
        FL.setPower(speedLeft);
        BL.setPower(speedLeft);
        BR.setPower(speedRight);
        FR.setPower(speedRight);
    }
    public void Stop() {
        FL.setPower(0.0);
        BL.setPower(0.0);
        BR.setPower(0.0);
        FR.setPower(0.0);
    }

    /**
     *not very accurate? reading < actual?
     * @param speed [-1.0, 1.0]
     * @param threshold in cm, >20cm
     */

    public void MoveTillUS(double speed, double threshold)
    {
        int x = 0;
        double[] data = new double[20];
        String output = " ";
        JustMove(speed, speed);
        do
        {
            if (x % 2 == 0)
                 data[x/2] = US1.getValue()*USfactor;
            my_wait(0.05);
            x++;
        }while(this.opModeIsActive()&& (US1.getValue()*USfactor)> threshold);
        for (int y= 0; y< 20; y++)
            output = output + data[y] + " ";
        telemetry.addData("US1", output);
        Stop();
    }

    public void parallel(double speed)//non-recursion
    {
        double speedL = speed, speedR = speed*-1;//clock wise
        do
        {
            if (US1.getValue() > US2.getValue())
                JustMove(speedR, speedL);
            else
                JustMove(speedR*-1, speedL*-1);
            my_wait(0.05);
        }while(US1.getValue()!= US2.getValue());
        Stop();
    }

    /**
     * parallels the robot to the wall using recursion
     * @param x iteration counter
     * @param speed [0, 1] > minSpeed
     * @throws InterruptedException for sleep()
     */

    public void ParallelRecursion(int x, double speed) throws InterruptedException
    {
        //base case: max adjustment or parallel
        if (x >= 10)//limit only to
            return;
        double usL = 0, usR = 0;
        for (int y = 0; y < 5; y++)//sometimes they fluctuate
        {
            usL += US1.getValue();
            usR += US2.getValue();
           sleep(50);//break required between each reading
        }
        if (usL == usR)
            return;

        double speedL = speed, speedR = speed*-1;//clock wise
        boolean direction;//true = US1 > US2
        if (US1.getValue() > US2.getValue()) {
            JustMove(speedR, speedL);
            direction = true;
        }
        else if (US1.getValue() < US2.getValue()){
            JustMove(speedR * -1, speedL * -1);
            direction = false;
        }
        else
            direction = true;

        double now = this.time;
        while(this.opModeIsActive()&&((US1.getValue() > US2.getValue()) == direction) && (this.time - now) < 1.0-0.07*(10-x)){//limit each turn < 1 sec && if the it still requires turning
            sleep(50);
        }
        Stop();
        //sleep(200);
        x++;

        if (speed > minSpeed)
            ParallelRecursion(x, speed-=0.03);//getting slower for minor adjustments
        else
            ParallelRecursion(x, speed);
    }
}


