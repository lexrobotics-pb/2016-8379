package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

public abstract class Action {
    abstract boolean isFinished(RobotStateFix state); //requires READ_ONLY, returns true if action is done
    abstract boolean update(RobotStateFix state); //requires READ_ONLY, reads from devices, updates robotState, returns true if an update is required
    abstract void doAction(RobotStateFix state); //requires WRITE_ONLY
}
