package com.qualcomm.ftcrobotcontroller.BetsyCode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by zht on 4/18/2015.
 */
public class MecMove extends Action {

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;


    final double encoderScale=1120.0;
    final double wheelRadius=((9.7)/2);
    final double wheelCircumference=Math.PI*2*wheelRadius;
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


    @Override
    boolean isFinished(RobotState state) {
        return Math.abs(state.EBackLeft)<scaled
                && Math.abs(motorFrontRight.getCurrentPosition()) <scaled
                && Math.abs(motorBackLeft.getCurrentPosition())< scaled
                && Math.abs(motorBackRight.getCurrentPosition()) < scaled;
    }

    @Override
    void doAction(RobotState state) {
    }

    @Override
    boolean update(RobotState state) {
        return true;
    }

    @Override
    boolean isDEVModeWrite()
    {
        return true;
    }

    @Override
    boolean isDEVModeRead()
    {
        return true;
    }

    private void resetEncoders(){
        motorFrontLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorFrontRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorBackLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorBackRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

}
