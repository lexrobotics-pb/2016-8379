package com.qualcomm.ftcrobotcontroller.BetsyCode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Meow extends OpMode {

    //define a list of movements
        Action movement1 = new MecMove(78.0, 0.0, 0.0, 20.0);
    LinkedList list = new LinkedList();
    static Queue<Action> actions=new LinkedList<Action>();
    static RobotState state=new RobotState();
    static boolean isWrite; //false=should be in read mode, true=should be in write mode


    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo grabber;
    Servo hood;
    Servo trigger;


    public Meow() {

    }

    @Override
    public void start()
    {
        telemetry.addData("Robot", "Constructor start");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower = hardwareMap.dcMotor.get("motorThrower");
        motorLift = hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        trigger = hardwareMap.servo.get("trigger");

        grabber.setPosition(1);
        hood.setPosition(0.3);
        trigger.setPosition(0.714);

        telemetry.addData("Robot","Construction end");

        isWrite=true;

        actions.add(movement1);
    }

    @Override
    public void run(){
        if(actions.isEmpty()) return;
        Action curAction=actions.peek();
        if(!isWrite && curAction.isDEVModeWrite()) switchAllToRead();//might change each individual mode or change the all together?
        if(isWrite && curAction.isDEVModeRead()) switchAllToWrite();

        if(curAction.isDEVModeRead()){
//            state.updateState(); //senses things
            if(curAction.update(state)){ //updates action variables and robotState variables
                isWrite=true; //set mode to WRITE for action*
            }
            else if(curAction.isFinished(state)){ //requires READMODE
                actions.poll();
                state.updateState();
                isWrite=true; //set mode to WRITE for action*
            }
        }
        else if(curAction.isDEVModeWrite()){ //REQUIRE WRITEMODE
            curAction.doAction(state);
            isWrite=false; //set mode to READ to check for action finish*
        }
    }

    @Override
    public void stop()
    {

    }

    public void switchAllToRead(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
    }

    public void switchAllToWrite(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
    }
}
