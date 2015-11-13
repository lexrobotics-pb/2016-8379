package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by eula from Betsy's template pseudo code in Movement.java on 4/18
 * Last Update date: 4/18/2015
 * Purpose: This class extends from Meow.java and (hopefully) function the same way as the
 * old MecMove in RobotC using encoders
 * Status: not completed
 * Future implementation: use gyro and/or compass sensors (in the update function
 * in this class) to know if the robot deviates from the planned path
 */
public class MecMove extends Action {

    final double encoderScale=1120.0;
    final double wheelRadius=((9.7)/2);
    final double wheelCircumference=Math.PI * 2 * wheelRadius;
    double speed, radians, speedRotation, distance, min, scaled;
    double[] speedList = new double[4];//0: front left  1: front right  2: back left  3: back right


    MecMove(double Mspeed, double Mdegrees, double MspeedRotation, double Mdistance){
        speed = Mspeed;
        radians = Mdegrees/180.0*Math.PI;
        speedRotation = MspeedRotation;
        distance = Mdistance;

        if (Math.cos(radians) == 0.0 || Math.sin(radians) == 0.0)
            min = 1.0;
        else if (Math.abs(1.0/Math.cos(radians))<= Math.abs(1.0 / Math.sin(radians)))
            min = 1.0/Math.cos(radians);
        else
            min = 1.0/Math.sin(radians);

        scaled = Math.abs(encoderScale * (distance * min / wheelCircumference));

        speedList[0] = speed * Math.sin(radians + Math.PI/4) + speedRotation;//front left
        speedList[1] = speed * Math.cos(radians + Math.PI/4) - speedRotation; //front right
        speedList[2] = speed * Math.cos(radians + Math.PI/4) + speedRotation; //back left
        speedList[3] = speed * Math.sin(radians + Math.PI/4) -  speedRotation;//back right
    }


    /**
     * check if the robot has travelled a certain amount of distance
     * scaled: defined in the constructor
     * @dparam
     * @return true if reached, false means that the robot should keep running
     */

    @Override
    boolean isFinished(RobotStateFix state) {
        DbgLog.msg("*********************************************************MecMove*"+Math.abs(state.motorBackLeft.getCurrentPosition()));
        if (Math.abs(state.motorBackLeft.getCurrentPosition())< scaled
                && Math.abs(state.motorFrontRight.getCurrentPosition()) < scaled
                && Math.abs(state.motorFrontLeft.getCurrentPosition())< scaled //changed motorBackLeft to motorFrontLeft - Kara 4/30/15
                && Math.abs(state.motorBackRight.getCurrentPosition()) < scaled)
        {
            resetEncoders(state);
            return true;
        }
        return false;
    }

    /**
     * created by eula on 4/28/2015
     * tell the robot/the four wheels to move according to a list of speed under write mode
     * @param state
     */

    @Override
    void doAction(RobotStateFix state) {
        DbgLog.msg("************ DOACTION MECMOVE");
        //DbgLog.msg("********************************************MecMove***speedList[0]="+speedList[0]+", speedList[1]="+speedList[1]);
        state.motorFrontLeft.setPower(speedList[0]);
        state.motorFrontRight.setPower(speedList[1]);
        state.motorBackLeft.setPower(speedList[2]);
        state.motorBackRight.setPower(speedList[3]);

        //Note by Kara: Does RunMode require one loop to implement like DeviceMode?
        // If so, can we just change the code in Meow such that RunMode is RUN_USING_ENCODERS by default?

        /*state.motorFrontLeft.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);//assuming that this turns on the encoders
        state.motorFrontRight.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        state.motorBackRight.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        state.motorBackLeft.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);*/
    }

    /**
     * not implemented yet
     * check if the robot is at the place of where it should be;
     * if no, update SpeedList
     * @param state
     * @return return true if the path should be changed
     */
    @Override
    boolean update(RobotStateFix state) {
        return false;
    }//implement gyro or compass to detect obstacles or change in the path



    /**
     * written  by eula on 4/28
     * reset encoders under write mode
     * @param state  hardware variables package
     */
    private void resetEncoders(RobotStateFix state){
        state.motorFrontLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorFrontRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorBackLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorBackRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

}
