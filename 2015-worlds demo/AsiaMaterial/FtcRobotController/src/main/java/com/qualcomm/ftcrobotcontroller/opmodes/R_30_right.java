package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by EULA on 4/7/2015.
 * For now the values remain as those from RobotC
 */
public class R_30_right extends OpMode{

    Telemetry telemetry = new Telemetry();
    Robot robot = new Robot();

    public void start(){
        //telemetry.addData("*","initialization start");
        robot.setGrabber(255);
        robot.setTrigger(220);
        robot.setHood(60);
        //robot.hoodHolderRelease();
        //telemetry.addData("*","Initialization done");
    }

    public void run(){
        telemetry.addData("*","begin movement");
        robot.mecJustMove(60, 0, 0);
        robot.wait1Msec(3500);
        telemetry.addData("*","wait done, stop");
        robot.Stop();
        robot.wait1Msec(250);

        robot.mecMove(-78, 90, 0,  52.0);//strafe left
        robot.wait1Msec(250);

        robot.mecMove(78, 0, 0, 150.0);//forward toward goal
        robot.setGrabber(150);
        robot.wait1Msec(500);
        robot.mecMove(-78, 0, 0, 10.0);//back a bit
        robot.wait1Msec(100);
        robot.setHood(130);//hood in place
        robot.mecMove(-78, 90, 0, 10.0);//side shift a bit

        robot.setThrower(-100.0); //start thrower motor
        robot.mecMove(-78.0, 0, 0, 240.0);//**length: move pass the kick stand
        robot.wait1Msec(250);
        robot.turnMecGyro(-60.0,180.0);//turn inside pz
        robot.wait1Msec(250);
        robot.mecMove(78.0, 90, 0, 120.0);//right strafe significantly pz
    }

    public void stop(){
        robot.setHood(60);
        robot.hoodHolderHold();
    }

}
