package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by khushisoni1 on 10/28/15.
 * Edited by Kara on 10/30/15, set for scrimmage
 */
public class ScrimmageTeleOp extends OpMode {

final static double DEADZONE= 0.1;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;

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
    }

    @Override
    public void loop()
    {

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
        motorFrontLeft.setPower(gamepad1.left_stick_y*0.9);
        motorBackLeft.setPower(gamepad1.left_stick_y*0.9);
        motorFrontRight.setPower(gamepad1.right_stick_y*0.9);
        motorBackRight.setPower(gamepad1.right_stick_y*0.9);

    }

    @Override
    public void stop()
    {

    }

}
// created by Khushi
