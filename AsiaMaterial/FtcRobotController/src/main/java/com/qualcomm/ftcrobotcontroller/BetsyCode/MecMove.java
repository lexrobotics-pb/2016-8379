package com.qualcomm.ftcrobotcontroller.BetsyCode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by eula on 4/18/2015 from
 *
 */
public class MecMove extends Action {

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
        return Math.abs(state.motorBackLeft.getCurrentPosition())<scaled
                && Math.abs(state.motorFrontRight.getCurrentPosition()) <scaled
                && Math.abs(state.motorBackLeft.getCurrentPosition())< scaled
                && Math.abs(state.motorBackRight.getCurrentPosition()) < scaled;
    }

    @Override
    void doAction(RobotState state) {
    }

    @Override
    boolean update(RobotState state) {
        return true;
    }//implement gyro or compass to detect obstacles or change in

    /**
     *
     * @return
     */
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

    /**
     * written  by eula on 4/28
     * reset encoders under write mode
     * @param state  hardware variables package
     */
    private void resetEncoders(RobotState state){
        state.motorFrontLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorFrontRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorBackLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorBackRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

}
