package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Eula on 10/8/2015.
 * Use this class to configure all robot parts and store main movement of the robot
 * Last Update: 2015.10.8
 * Message: Just created
 */
public class Robot extends OpMode {

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    GyroSensor gyro;

    ColorSensor color;

    @Override
    public void init(){
        //.initialize all hardware parts here


    }

    public void wait1Msec(double time)
    {
        ElapsedTime waitTime = new ElapsedTime();
        waitTime.reset();
        waitTime.startTime();
        while (waitTime.time() * 1000 < time){}
    }
    /**
     * Tells the robot to move in a certain direction
     * @param speed [-1,1]
     * @param degrees angle/direction of the robot relative to the front in degrees, positive = clockwise
     * @param speedRotation [-1,1], the speed the robot rotates on the spot
     *                      speed and degrees should be left as 0 if this para != 0
     */
    public void mecJustMove(double speed, double degrees, double speedRotation)
    {
//        switchAllToWrite();
        double radians = toRadians(degrees);
        motorFrontLeft.setPower(speed);
        motorFrontRight.setPower(speed;
        motorBackLeft.setPower(speed);
        motorBackRight.setPower(speed);

//        switchAllToRead();
    }

    public void Stop()
    {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        resetEncoders();
    }

    public void resetEncoders()
    {

    }

    private double toRadians (double degrees)
    {
        double radians=degrees/180.0*Math.PI;
        return radians;
    }


    /**
     * turn the robot on the spot
     * @param speedrotation [-100,100]
     * @param degrees angle in degree not in radians
     */
    public void turnMecGyro(double speedrotation, double degrees) {
        double delTime = 0;
        double curRate;
        double currHeading = 0;
        ElapsedTime Time1 = new ElapsedTime();
        //no gyro initialization?
        wait1Msec(200);
        Stop();
        mecJustMove (0, 0, speedrotation);//+ = right   - = turn left
        while (Math.abs(currHeading) < Math.abs(degrees)) {
            Time1.startTime();
            curRate = gyro.getRotation();
            if (Math.abs(curRate) > 3) {
                currHeading += curRate * delTime; //Approximates the next heading by adding the rate*time.
                if (currHeading > 360) currHeading -= 360;
                else if (currHeading < -360) currHeading += 360;
            }
            wait1Msec(5);
            delTime = ((double)Time1.time()) / 1000000; //set delta (zero first time around)
        }
        Stop();
    }

    @Override
    public void loop(){}

    @Override
    public void stop(){}
}
