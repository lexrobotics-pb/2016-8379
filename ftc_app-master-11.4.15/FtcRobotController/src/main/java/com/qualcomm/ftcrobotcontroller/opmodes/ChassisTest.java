package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by zht on 9/24/2015.
 */
public class ChassisTest extends OpMode {

    final static double DEADZONE= 0.1;
    DcMotor motorRight;
    DcMotor motorLeft;


    @Override
    public void init()
    {
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
    }

    @Override
    public void loop()
    {

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
        motorLeft.setPower(gamepad1.left_stick_y*0.9);
        motorRight.setPower(gamepad1.right_stick_y*0.9);
        telemetry.addData("*************************", "Working");
    }

    @Override
    public void stop()
    {

    }

}
