package com.qualcomm.ftcrobotcontroller.BetsyCode;//package com.qualcomm.ftcrobotcontroller.BetsyCode;
//
//import com.qualcomm.robotcore.hardware.DcMotor;
//
///*example for Action subclass*/
//public class Movement extends Action{//
//    float destinationX,destinationY,speed;
//
//
//    Movement(float x,float y, float s){
//        destinationX=x;
//        destinationY=y;
//        speed=s;
//    }
//
//    @Override
//    boolean isFinished(RobotState state) {
//        return state.x==destinationX && state.y==destinationY;
//    }
//
//    @Override
//    void doAction(RobotState state) {
//        *reset encoders*
//        *set motors to correct direction/speed (do some math)*
//        motorx=1.0
//    }
//
//    @Override
//    boolean update(RobotState state) {
//        *check state and update destination/action variables if necessary*
//        if(an update has occurred) return true;
//        return false;
//    }
//
//    @Override
//    boolean isDEVModeWrite()
//    {
//        *check if all of the states of the motor/servo is in write mode (WRITE_ONLY/READ_ONLY)
//        return true;
//    }
//
//    @Override
//    boolean isDEVModeRead()
//    {
//        return true;
//    }
//
//
//    /*
//    @Override
//    boolean isFinished(RobotState state) {
//    }
//
//    @Override
//    void doAction(RobotState state) {
//    }
//
//    @Override
//    boolean update(RobotState state) {
//    }
//
//    @Override
//    boolean isDEVModeWrite()
//    {
//    }
//
//    @Override
//    boolean isDEVModeRead()
//    {
//    }
//     */
//}