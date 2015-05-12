package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by: Kara Luo
 * Last Modified: 4/30/15
 * Note: Refers to pseudo code framework by Betsy Pu
 * Implements abstract methods in Action to control sensor-based wheel movement
 */

public class MecMoveSensor extends Action {

    double speed, radians, speedRotation, moveTo, scaled;
    double[] speedList = new double[4];//0: front left  1: front right  2: back left  3: back right


    MecMoveSensor(double Mspeed, double Mdegrees, double MspeedRotation, double threshold) {
        speed = Mspeed;
        radians = Mdegrees / 180.0 * Math.PI;
        speedRotation = MspeedRotation;
        moveTo = threshold;

        speedList[0] = speed * Math.sin(radians + Math.PI / 4) + speedRotation;//front left
        speedList[1] = speed * Math.cos(radians + Math.PI / 4) - speedRotation; //front right
        speedList[2] = speed * Math.cos(radians + Math.PI / 4) + speedRotation; //back left
        speedList[3] = speed * Math.sin(radians + Math.PI / 4) - speedRotation;//back right
    }


    /**
     * Last Edit: Kara Luo 4/30/15
     * @param state
     * @return true if readings are below threshold reached and robot should stop, false means that the robot should keep running
     */

    @Override
    boolean isFinished(RobotStateFix state) {
        return state.USback.getUltrasonicLevel() < moveTo ||
                state.USfront.getUltrasonicLevel() < moveTo;
    }

    /**
     * created by eula on 4/28/2015
     * tell the robot/the four wheels to move according to a list of speed under write mode
     *
     * @param state
     */

    @Override
    void doAction(RobotStateFix state) {
        state.motorFrontLeft.setPower(speedList[0]);
        state.motorFrontRight.setPower(speedList[1]);
        state.motorBackLeft.setPower(speedList[2]);
        state.motorBackRight.setPower(speedList[3]);
    }

    /**
     * @param state
     * @return return true if the path should be changed
     */
    @Override
    boolean update(RobotStateFix state) {
        return true;
    }//implement gyro or compass to detect obstacles or change in the path

    /**
     * written  by eula on 4/28
     * reset encoders under write mode
     * @param state hardware variables package
     */
    private void resetEncoders(RobotStateFix state) {
        state.motorFrontLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorFrontRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorBackLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        state.motorBackRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }
}