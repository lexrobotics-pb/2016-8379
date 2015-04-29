package com.qualcomm.ftcrobotcontroller.BetsyCode;

import com.qualcomm.robotcore.hardware.DcMotorController;

public abstract class Action {
    abstract boolean isFinished(RobotState state); //requires READ_ONLY, returns true if action is done
    abstract boolean update(RobotState state); //requires READ_ONLY, reads from devices, updates robotState, returns true if an update is required
    abstract void doAction(RobotState state); //requires WRITE_ONLY
    abstract boolean isDEVModeWrite();
    abstract boolean isDEVModeRead();
//    abstract void setDeviceMode(DcMotorController.DeviceMode e);
}
