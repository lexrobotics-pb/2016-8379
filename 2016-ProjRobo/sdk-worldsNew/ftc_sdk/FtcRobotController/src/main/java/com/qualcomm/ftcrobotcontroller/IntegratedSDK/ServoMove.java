package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Contributor: Kara Luo
 * Last Modified: 4/30/15
 * Note: Refers to pseudo code framework by Betsy Pu
 * Implements abstract methods in Action to control general servo movement
 */

public class ServoMove extends Action {

    double moveTo = 0.0;

    /**
     * Last Edit: Kara Luo 4/30/15
     * Constructor for ServoMove class
     * @param position desired position of servo
     */
    ServoMove(double position){
        moveTo = position;
    }

    /**
     * Last Edit: Kara Luo 4/30/15
     * @param state
     * @return true if servo has achieved desired position, false otherwise
     */

    @Override
    boolean isFinished(RobotState state) {
        return state.servo_1.getPosition()== moveTo;
    }

    /**
     * Last Edit: Kara Luo 4/30/15
     * Moves servo to desired position
     * @param state
     */
    @Override
    void doAction(RobotState state) {
        state.servo_1.setPosition(moveTo);
    }

    /**
     * @param state
     * @return
     */
    @Override
    boolean update(RobotState state) {
        return true;
    }


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
}
