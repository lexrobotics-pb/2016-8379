package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by zht on 4/2/2015.
 */
public class CenterGoal extends OpMode {

    private Robot robot = new Robot();
    DcMotor motorLift;
    Servo trigger;

    public void CenterGoal() {
    }


    public void start(){
        trigger = hardwareMap.servo.get("trigger");
    }
    public void stop(){}
    public void run(){}
}
