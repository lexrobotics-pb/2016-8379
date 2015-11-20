package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by khushisoni1 on 10/28/15.
 * Edited by Kara on 11/14/15, set for meet 1, skirt and climber button implemented
 */
public class ScrimmageTeleOp extends OpMode {

final static double DEADZONE= 0.1;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;

    Servo skirts;
    Servo LeftTrigger;
    Servo RightTrigger;
    Servo push;

    double skirtsPos;
    double pushPos;


    @Override
    public void init()
    {
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.FORWARD); //forwards back right motor
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.FORWARD); // forwards front right motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE); //backwards back left motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //backwards front left motor

        skirts = hardwareMap.servo.get("skirts");
        LeftTrigger = hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hardwareMap.servo.get("RightTrigger");
        push = hardwareMap.servo.get("push");

        LeftTrigger.setPosition(0.0);
        RightTrigger.setPosition(1.0);
        push.setPosition(0.5);
        skirts.setPosition(0.5);
    }

    @Override
    public void loop()
    {
        //===================Movement==================================================
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
        motorFrontLeft.setPower(gamepad1.left_stick_y*0.9);
        motorBackLeft.setPower(gamepad1.left_stick_y * 0.9);
        motorFrontRight.setPower(gamepad1.right_stick_y*0.9);
        motorBackRight.setPower(gamepad1.right_stick_y * 0.9);

        skirtsPos = 0.5;
        pushPos = 0.5;

        if(gamepad2.x)
            pushPos = 0.7;

        if(gamepad2.b)
            pushPos = 0.3;

        if (gamepad2.a)
            skirtsPos = 0.3;

        if (gamepad2.y)
            skirtsPos = 0.7;

        if(gamepad2.left_trigger > 0.1)
            LeftTrigger.setPosition(0.95);

        if(gamepad2.left_bumper)
            LeftTrigger.setPosition(0.0);

        if(gamepad2.right_trigger > 0.1)
            RightTrigger.setPosition(0.0);

        if(gamepad2.right_bumper)
            RightTrigger.setPosition(0.95);

        push.setPosition(pushPos);
        skirts.setPosition(skirtsPos);
    }

    @Override
    public void stop()
    {}

}