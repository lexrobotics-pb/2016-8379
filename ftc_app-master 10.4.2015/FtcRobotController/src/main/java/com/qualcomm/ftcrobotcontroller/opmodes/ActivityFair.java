package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by zht on 9/24/2015.
 */
public class ActivityFair extends OpMode {

    final static double DEADZONE= 0.1;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    int frontback;
    Telemetry telemetry = new Telemetry();


    @Override
    public void init()
    {
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        frontback = 1;

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
        motorFrontLeft.setPower(frontback * ((gamepad1.left_stick_y + (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));
        motorBackLeft.setPower(frontback * ((gamepad1.left_stick_y - (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));
        motorFrontRight.setPower(frontback * ((gamepad1.right_stick_y - (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));
        motorBackRight.setPower(frontback * ((gamepad1.right_stick_y + (gamepad1.right_stick_x + gamepad1.left_stick_x) / 2)/2));


        telemetry.addData("*************************", "Working");
    }

    @Override
    public void stop()
    {

    }

}
