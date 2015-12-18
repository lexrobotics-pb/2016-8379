package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by khushisoni1 on 10/28/15.
 * Edited by Kara on 11/14/15, set for meet 1, skirt and climber button implemented
 */
public class Meet2TeleOp extends OpMode {

    final static double DEADZONE = 0.1;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor Flipper;
    DcMotor Lift;
    DcMotor Paddle;

    Servo push;
    Servo LeftTrigger;
    Servo RightTrigger;
    Servo dump;

    double paddlePower;
    double current;
    double liftPower;


    @Override
    public void init() {
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorFrontRight.setDirection(DcMotor.Direction.REVERSE); //forwards back left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorBackLeft.setDirection(DcMotor.Direction.FORWARD); //forwards back left motor
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.FORWARD); //forwards front left motor
        Flipper = hardwareMap.dcMotor.get("Flipper");
        Lift = hardwareMap.dcMotor.get("Lift");
        Paddle = hardwareMap.dcMotor.get("Paddle");

        push = hardwareMap.servo.get("push");
        dump = hardwareMap.servo.get("dump");
        LeftTrigger = hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hardwareMap.servo.get("RightTrigger");

        RightTrigger.setPosition(0.95);
        LeftTrigger.setPosition(0.15);
        dump.setPosition(0.3);
        push.setPosition(0.5);
    }

    @Override
    public void loop() {
        //===================Movement==================================================
        if (Math.abs(gamepad1.left_stick_x) < DEADZONE) {
            gamepad1.left_stick_x = 0;
        }
        if (Math.abs(gamepad1.left_stick_y) < DEADZONE) {
            gamepad1.left_stick_y = 0;
        }
        if (Math.abs(gamepad1.right_stick_x) < DEADZONE) {
            gamepad1.right_stick_x = 0;
        }
        if (Math.abs(gamepad1.right_stick_y) < DEADZONE) {
            gamepad1.right_stick_y = 0;
        }
        motorFrontLeft.setPower(gamepad1.left_stick_y * 0.75);
        motorBackLeft.setPower(gamepad1.left_stick_y * 0.75);
        motorFrontRight.setPower(gamepad1.right_stick_y * 0.75);
        motorBackRight.setPower(gamepad1.right_stick_y * 0.75);


        if(gamepad1.y)
            Flipper.setPower(0.95);

        if(gamepad1.a)
            Flipper.setPower(0.0);

        if(gamepad1.x)
            Flipper.setPower(-0.8);

        if(gamepad1.left_bumper)
            LeftTrigger.setPosition(0.15);

        else if(gamepad1.left_trigger > 0.3)
            LeftTrigger.setPosition(0.9);

        if(gamepad1.right_bumper)
            RightTrigger.setPosition(0.95);

        else if(gamepad1.right_trigger > 0.3)
            RightTrigger.setPosition(0.2);

        //************************ Gamepad 2 ***********************************
        paddlePower = 0.0;
        liftPower = 0.0;
        current = gamepad2.left_stick_x;

        if (Math.abs(current) > 0.5)
            paddlePower = -Math.abs(current) / current * 0.1;

        if (gamepad2.a)
            liftPower = -0.3;

        if (gamepad2.y)
            liftPower = 0.3;

        Paddle.setPower(paddlePower);
        Lift.setPower(liftPower);

        if (gamepad2.left_trigger > 0.3)
            dump.setPosition(0.3);

        if (gamepad2.left_bumper)
            dump.setPosition(0.85);
    }

    @Override
    public void stop() {
        motorFrontLeft.setPower(0.0);
        motorBackLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
        motorBackRight.setPower(0.0);
        Lift.setPower(0.0);
        Paddle.setPower(0.0);
        Flipper.setPower(0.0);
    }

}