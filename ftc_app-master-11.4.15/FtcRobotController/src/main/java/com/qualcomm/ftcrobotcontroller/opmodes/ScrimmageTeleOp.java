package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by khushisoni1 on 10/28/15.
 * Edited by Kara on 11/14/15, set for meet 1
 */
public class ScrimmageTeleOp extends OpMode {

final static double DEADZONE= 0.1;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;

    Servo skirts;

Telemetry telemetry = new Telemetry();


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

        skirts=hardwareMap.servo.get("skirts");
    }

    @Override
    public void loop()
    {
        double skirtsPos = 0.5;
        if (Math.abs(gamepad1.left_stick_x) < DEADZONE*100){
            gamepad1.left_stick_x = 0;
        }
        if (Math.abs(gamepad1.left_stick_y) < DEADZONE*100){
            gamepad1.left_stick_y = 0;
        }
        if (Math.abs(gamepad1.right_stick_x) < DEADZONE){
            gamepad1.right_stick_x = 0;
        }
        if (Math.abs(gamepad1.right_stick_y) < DEADZONE){
            gamepad1.right_stick_y = 0;
        }
        if (gamepad1.a){
            skirtsPos = 0.3;
        }

        if (gamepad1.y){
            skirtsPos = 0.7;
        }

              /*---------------------Movement----------------------------*/
        motorFrontLeft.setPower(gamepad1.left_stick_y*0.9);
        motorBackLeft.setPower(gamepad1.left_stick_y * 0.9);
        motorFrontRight.setPower(gamepad1.right_stick_y*0.9);
        motorBackRight.setPower(gamepad1.right_stick_y * 0.9);
        skirts.setPosition(skirtsPos);
    }

    @Override
    public void stop()
    {

    }

    public void my_wait(double seconds)
    {
        double current = this.time;
        while ((this.time - current) < seconds) {
        }
    }

}
// created by Khushi
