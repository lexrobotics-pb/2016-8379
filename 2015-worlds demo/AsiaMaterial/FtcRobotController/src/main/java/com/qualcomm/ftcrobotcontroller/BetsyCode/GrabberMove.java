package com.qualcomm.ftcrobotcontroller.BetsyCode;

/**
 * Created by user on 4/28/2015.
 */
public class GrabberMove extends Action{

    @Override
    boolean isFinished(RobotState state) {
        return false;
    }

    @Override
    boolean update(RobotState state) {
        return false;
    }

    @Override
    void doAction(RobotState state) {

    }

    @Override
    boolean isDEVModeWrite() {
        return false;
    }

    @Override
    boolean isDEVModeRead() {
        return false;
    }
}
