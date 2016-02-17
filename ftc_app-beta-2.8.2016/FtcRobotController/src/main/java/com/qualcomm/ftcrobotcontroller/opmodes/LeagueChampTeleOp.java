package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Eula on 1/11/16.
 * Edited by Eula on 1/11/16, drafted for Meet 3
 */
public class LeagueChampTeleOp extends OpMode {

    final static double DEADZONE = 0.1;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor Flipper;
    DcMotor Box;

    Servo conveyor;
    Servo LeftTrigger;
    Servo RightTrigger;
    Servo dump;
    Servo push;
    Servo Conveyor;

    double conveyorPower;
    double current;
    double boxPower;
    double dumpPosition;
    double pushPosition;
    double speedFactor = 0.9;
    double flipperPower;

    boolean speed = true;
    boolean direction = true;


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
        Box = hardwareMap.dcMotor.get("Box");
        Conveyor = hardwareMap.servo.get("conveyor");

        conveyor = hardwareMap.servo.get("conveyor");
        push = hardwareMap.servo.get("push");
        dump = hardwareMap.servo.get("dump");
        LeftTrigger = hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hardwareMap.servo.get("RightTrigger");

        RightTrigger.setPosition(0.15);
        LeftTrigger.setPosition(0.95);
        dump.setPosition(0.5);
        push.setPosition(0.5);
        conveyor.setPosition(0.5);
    }

    @Override
    public void loop() {
//===================Movement==================================================
        if (Math.abs(gamepad1.left_stick_x) < DEADZONE) {gamepad1.left_stick_x = 0;}
        if (Math.abs(gamepad1.left_stick_y) < DEADZONE) {gamepad1.left_stick_y = 0;}
        if (Math.abs(gamepad1.right_stick_x) < DEADZONE) {gamepad1.right_stick_x = 0;}
        if (Math.abs(gamepad1.right_stick_y) < DEADZONE) {gamepad1.right_stick_y = 0;}

        if (direction){
            motorFrontLeft.setPower(gamepad1.left_stick_y * speedFactor);
            motorBackLeft.setPower(gamepad1.left_stick_y * speedFactor);
            motorFrontRight.setPower(gamepad1.right_stick_y * speedFactor);
            motorBackRight.setPower(gamepad1.right_stick_y * speedFactor);}
        else{
            motorFrontRight.setPower(gamepad1.left_stick_y * speedFactor*-1);
            motorBackRight.setPower(gamepad1.left_stick_y * speedFactor*-1);
            motorFrontLeft.setPower(gamepad1.right_stick_y * speedFactor*-1);
            motorBackLeft.setPower(gamepad1.right_stick_y * speedFactor*-1);
        }

        if (gamepad1.y)
            direction = true; //forward
        if (gamepad1.a)
            direction = false; //backward

//============push====================
        pushPosition = 0.5;
        if (gamepad1.left_bumper)    pushPosition = 0.3;
        if (gamepad1.left_trigger>0.3)  pushPosition = 0.85;
        push.setPosition(pushPosition);

//==========Flipper=====================
        flipperPower = 0;
        if(gamepad1.right_bumper)  flipperPower = 0.95;
        if(gamepad1.right_trigger>0.3)  flipperPower = -0.8;
        Flipper.setPower(flipperPower);


//************************ Gamepad 2 ***********************************
//========conveyor=============================
        conveyorPower = Math.abs(gamepad2.left_stick_x * 0.5-0.5);
        conveyor.setPosition(conveyorPower);

        conveyor.setPosition(conveyorPower);

//==========Box==============================
        boxPower = 0.0;
        if (gamepad2.right_stick_y < -0.5) boxPower = -0.6;//in
        if (gamepad2.right_stick_y > 0.5) boxPower = 0.6;//out

        Box.setPower(boxPower);

//===========dump=====================
        dumpPosition = 0.5;
        if (gamepad2.y) dumpPosition = 0.3;
        if (gamepad2.a) dumpPosition = 0.85;

        dump.setPosition(dumpPosition);

////===========gate=====================
//        if (gamepad2.x)
//            gate.setPosition(0.0);//up
//
//        if (gamepad2.b)
//            gate.setPosition(0.6);//down

//===========Triggers============================
        if(gamepad2.right_bumper)//because the robot gets up the ramp in the opposite direction
            LeftTrigger.setPosition(0.15);
        else if(gamepad2.right_trigger > 0.3)
            LeftTrigger.setPosition(0.95);

        if(gamepad2.left_bumper)
            RightTrigger.setPosition(0.95);
        else if(gamepad2.left_trigger > 0.3)
            RightTrigger.setPosition(0.15);
    }

    @Override
    public void stop() {
        motorFrontLeft.setPower(0.0);
        motorBackLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
        motorBackRight.setPower(0.0);
        Flipper.setPower(0.0);
        Box.setPower(0.0);
        dump.setPosition(0.5);
        push.setPosition(0.5);
        conveyor.setPosition(0.5);
    }
}