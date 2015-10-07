package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by zht on 10/6/2015.
 */
public class BallCollectTest extends OpMode {

    Servo rack;
    Telemetry telemetry = new Telemetry();

    @Override
    public void init()
    {
        rack = hardwareMap.servo.get("rack");
    }

    @Override
    public void loop()
    {
        rack.setPosition(1.0);
        Log.d("hello", rack.getPosition() + "**********************************");

    }

    @Override
    public void stop()
    {

    }
}
