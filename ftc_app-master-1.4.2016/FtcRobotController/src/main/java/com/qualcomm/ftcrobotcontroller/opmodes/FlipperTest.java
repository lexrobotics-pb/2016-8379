package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by zht on 2/2/2016.
 */
public class FlipperTest extends OpMode {
    DcMotor Flipper;
   Servo conveyor;

    Servo LeftTrigger;
    Servo RightTrigger;
    Servo dump;
    Servo push;
@Override
    public void init()
    {
        Flipper = hardwareMap.dcMotor.get("Flipper");
        push = hardwareMap.servo.get("push");
        dump = hardwareMap.servo.get("dump");
        LeftTrigger = hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hardwareMap.servo.get("RightTrigger");
        conveyor=hardwareMap.servo.get("conveyor");
        RightTrigger.setPosition(0.1);
        LeftTrigger.setPosition(0.98);
        dump.setPosition(0.5);
        push.setPosition(0.5);
        conveyor.setPosition(0.5);
    }
@Override
    public void loop(){
        Flipper.setPower(0.9);
        conveyor.setPosition(0.9);
    }
@Override
    public void stop(){
        conveyor.setPosition(0.5);

    }

}
