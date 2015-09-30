package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import android.widget.TextView;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.LinkedList;
import java.util.Queue;
import android.util.Log;
import android.widget.*;

/**
 * Created by Betsy and Eula on 4/18
 * Last Update date: 5/14/2015
 * Last Update by:
 * Purpose: the main class of the new looping system. Classes that are implemented in this class
 * includes RobotState, Action, and all of the movement classes.It uses a queue object to sequent
 * the actions that need to the accomplished in order. a new movement class is required if threading
 * is involved (or potentially have a class just for threading in this framework?)
 * Status: inherited HardwareMap from OpMode; in mecmove, encoder works, somehow
 */

public class Meow extends OpMode { /*Betsy 4-28*/

    //define a list of movements
    static Queue<Action> actions=new LinkedList<Action>();
    //static RobotState state=new RobotState();
    RobotStateFix state;
//    static boolean isWrite; //false=should be in read mode, true=should be in write mode

    @Override
    public void init()
    {
        DbgLog.msg("********Calling constructor");
        state = new RobotStateFix(this.hardwareMap);
        DbgLog.msg("**** Start");
//        isWrite=true;
        actions.add(new MecMove(0.78, 0.0, 0.0, 2.0));//add actions to the queue
    }

    @Override
    public void loop(){
        if(actions.isEmpty()) return;
        Action curAction=actions.peek();
        if(curAction.update(state)){ //updates action variables and RobotState variables
//            isWrite=true; //set mode to WRITE for action*
            curAction.doAction(state);
        }
        else if(curAction.isFinished(state)){ //requires READMODE
            actions.poll();
            //state.updateState();
//            isWrite=true; //set mode to WRITE for action*
        }
        /*if(!isWrite && state.isDEVModeWrite())
        {
           // DbgLog.msg("**************************************Meow*******DevMode1 = " + state.motorFrontRight.getDeviceMode());
            state.switchAllToRead();//might change each individual mode or change the all together?
        }
        if(isWrite && state.isDEVModeRead())
        {
            //DbgLog.msg("**************************************Meow*******DevMode2 = " + state.motorFrontRight.getDeviceMode());
            state.switchAllToWrite();
        }

        if(state.isDEVModeRead()){
//            DbgLog.msg("**************************************Meow*******DevMode3 = " + state.motorFrontRight.getDeviceMode());
//            state.updateState(); //senses things
            if(curAction.update(state)){ //updates action variables and RobotState variables
                isWrite=true; //set mode to WRITE for action*
            }
            else if(curAction.isFinished(state)){ //requires READMODE
                actions.poll();
                //state.updateState();
                isWrite=true; //set mode to WRITE for action*
            }
        }
        else if(state.isDEVModeWrite()){ //REQUIRES WRITEMODE
            curAction.doAction(state);//including the current action or updated action
            isWrite=false; //set mode to READ to check for action finish*
        }*/
    }

    @Override
    public void stop(){
    }
}
