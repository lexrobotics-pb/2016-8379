package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Contributor: Kara Luo
 * Last Modified: 4/30/15
 * Note: Refers to pseudo code framework by Betsy Pu
 * Implements abstract methods in Action to control general servo movement
 */

/**
 * Contributor: eula
 * Last Modified: 5/4/2015
 * Note: deleted servo_1 and removed isDEVModeWrite and isDEVModeRead because it is not compiling correctly
 * constructor also pass in a servo so it can control another servo from the RobotState class
 */

public class ServoMove extends Action {
    Servo servo;
    double moveTo = 0.0;

    /**
     * Last Edit: Kara Luo 4/30/15
     * Constructor for ServoMove class
     * @param position desired position of servo
     */
    ServoMove(Servo s,double position){
        servo=s;//a servo from RobotState class
        moveTo = position;
    }

    /**
     * Last Edit: Kara Luo 4/30/15
     * @param state
     * @return true if servo has achieved desired position, false otherwise
     */

    @Override
    boolean isFinished(RobotState state) {
//        return servo.getPosition()== moveTo;
        return true;// once the is set to move, we don't need to check whether it has reached there or not (not for continuous
    }

    /**
     * Last Edit: Kara Luo 4/30/15
     * Moves servo to desired position
     * @param state
     */
    @Override
    void doAction(RobotState state) {
        servo.setPosition(moveTo);
    }//eula: changed because we don't have servo_1 on the robot

    /**
     * @param state
     * @return
     */
    @Override
    boolean update(RobotState state) {
        return true;
    }

}
