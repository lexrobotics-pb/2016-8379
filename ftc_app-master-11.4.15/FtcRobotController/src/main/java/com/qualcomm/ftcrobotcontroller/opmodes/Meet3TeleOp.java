package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Eula on 1/11/16.
 * Edited by Eula on 1/11/16, drafted for Meet 3
 */
public class Meet3TeleOp extends OpMode {

    final static double DEADZONE = 0.1;
    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor Flipper;
    DcMotor Box;
    DcMotor Conveyor;

    Servo LeftTrigger;
    Servo RightTrigger;
    Servo dump;
    Servo push;
    Servo gate;

    double conveyorPower;
    double current;
    double boxPower;
    double dumpPosition;
    double pushPosition;
    double speedFactor = 0.9;

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
        Conveyor = hardwareMap.dcMotor.get("Conveyor");

        push = hardwareMap.servo.get("push");
        dump = hardwareMap.servo.get("dump");
        LeftTrigger = hardwareMap.servo.get("LeftTrigger");
        RightTrigger = hardwareMap.servo.get("RightTrigger");
        gate = hardwareMap.servo.get("gate");

        RightTrigger.setPosition(0.15);
        LeftTrigger.setPosition(0.95);
        dump.setPosition(0.5);
        push.setPosition(0.5);
        gate.setPosition(0);
    }

    @Override
    public void loop() {
        //=================== Movement =======================
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

        if (direction) {
            motorFrontLeft.setPower(gamepad1.left_stick_y * speedFactor);
            motorBackLeft.setPower(gamepad1.left_stick_y * speedFactor);
            motorFrontRight.setPower(gamepad1.right_stick_y * speedFactor);
            motorBackRight.setPower(gamepad1.right_stick_y * speedFactor);
        } else {
            motorFrontRight.setPower(gamepad1.left_stick_y * speedFactor * -1);
            motorBackRight.setPower(gamepad1.left_stick_y * speedFactor * -1);
            motorFrontLeft.setPower(gamepad1.right_stick_y * speedFactor * -1);
            motorBackLeft.setPower(gamepad1.right_stick_y * speedFactor * -1);
        }

        //=========== Triggers ==================
        if (gamepad1.left_bumper)
            LeftTrigger.setPosition(0.15);
        else if (gamepad1.left_trigger > 0.3)
            LeftTrigger.setPosition(0.95);

        if (gamepad1.right_bumper) {
            RightTrigger.setPosition(0.95);
        }

        else if (gamepad1.right_trigger > 0.3) {
            RightTrigger.setPosition(0.15);
        }

        //=========== Direction =================
        if (gamepad1.right_stick_button) {
            if (direction)
                direction = false;
            else
                direction = true;
        }

        //=========== Speed ======================
        if (gamepad1.left_stick_button) {
            if (speed) {
                speed = false;
                speedFactor = 0.75;
            } else {
                speed = true;
                speedFactor = 0.9;
            }
        }

        //========== Flipper ==========================
        if (gamepad1.b) Flipper.setPower(0.95);
        if (gamepad1.a) Flipper.setPower(0.0);
        if (gamepad1.x) Flipper.setPower(-0.8);


//************************ Gamepad 2 ***********************************

        //======== conveyor ===========================
        conveyorPower = 0.0;
        current = gamepad2.left_stick_x;
        if (Math.abs(current) > 0.5)//right = going to the right when on the ramp
            conveyorPower = Math.abs(current) / current * 0.2;

        Conveyor.setPower(conveyorPower);

        //========== box ==============================
        boxPower = 0.0;
        if (gamepad2.a) boxPower = -0.2;
        if (gamepad2.y) boxPower = 0.2;

        Box.setPower(boxPower);

        //=========== dump ============================
        dumpPosition = 0.5;
        if (gamepad2.x) dumpPosition = 0.3;
        if (gamepad2.b) dumpPosition = 0.85;

        dump.setPosition(dumpPosition);

        //=========== gate ============================
        if (gamepad2.right_bumper)
            gate.setPosition(0.0);

        if (gamepad2.right_trigger > 0.3)
            gate.setPosition(0.3);

        //============ push ===========================
        pushPosition = 0.5;
        if (gamepad2.left_bumper) pushPosition = 0.3;
        if (gamepad2.left_trigger > 0.3) pushPosition = 0.85;
        push.setPosition(pushPosition);

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
    }

    public void my_wait(double sec) {
        double current = this.time;
        while ((this.time - current) < sec) {
        }
    }
}