package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.GyroSensor;
/**
 * Created by Eula Zhong on 4/1/2015.
 * This class holds basic functions of the robot of FTC team 8379 for the
 * 2014 season. Sequential movement functions such as EndSequence() and
 * alignRecursive() that are specific for a certain series of
 * autonomous program are not included in this class.
 */
public class Robot extends OpMode{

    public double posGrabber;

    public double posHood;

    public double posTrigger;

    public double posHolder;

    // everything is in cm
    final double encoderScale=1120.0;
    final double wheelRadius=((9.7)/2);
    final double wheelCircumference=Math.PI*2*wheelRadius;

    DcMotor motorFrontRight;
    DcMotor motorBackRight;
    DcMotor motorThrower;
    DcMotor motorLift;
    DcMotor motorBackLeft;
    DcMotor motorFrontLeft;

    Servo grabber;
    Servo hood;
    Servo USbackservo;
    Servo holder;//nothing for continuous servo
    Servo trigger;

    UltrasonicSensor USfront;
    UltrasonicSensor USback;

    GyroSensor gyro;

    public void Robot()
    {
        motorFrontRight = hardwareMap.dcMotor.get("frontright");
        motorBackRight = hardwareMap.dcMotor.get("backright");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower= hardwareMap.dcMotor.get("thrower");
        motorLift= hardwareMap.dcMotor.get("lift");
        motorFrontLeft = hardwareMap.dcMotor.get("frontleft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("backleft");

        USfront = hardwareMap.ultrasonicSensor.get("USfront");
        USback = hardwareMap.ultrasonicSensor.get("USback");

        gyro = hardwareMap.gyroSensor.get("gyro");

        grabber = hardwareMap.servo.get("grabber");
        hood = hardwareMap.servo.get("hood");
        USbackservo = hardwareMap.servo.get("USbackservo");
        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

        grabber.setPosition(1);
        hood.setPosition(0.235);
        trigger.setPosition(0.714);
        holder.setPosition(0);

        motorBackLeft.setPower(0.0);
        motorBackRight.setPower(0.0);
        motorFrontLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
        motorLift.setPower(0.0);
        motorThrower.setPower(0.0);
    }

    /**
     * Delays the robot's next action for a period of time
     * @param time in miliseconds or 1/1000 of a second
     */
    public void wait1Msec(double time)
    {
        ElapsedTime waitTime = new ElapsedTime();
        waitTime.startTime();
        while (waitTime.time() < time / 1000){}
    }
    /**
     * Tells the robot to move in a certain direction
     * @param speed -1 to 1
     * @param degrees angle/direction of the robot relative to the front in degrees
     * @param speedRotation: -1 to 1
     */
    public void mecJustMove(double speed, double degrees, double speedRotation)
    {
        degrees = toDegrees(degrees);
        motorFrontLeft.setPower(speed * Math.sin(degrees + 45) + speedRotation);
        motorFrontRight.setPower(speed * Math.cos(degrees + 45) - speedRotation);
        motorBackLeft.setPower(speed * Math.cos(degrees + 45) + speedRotation);
        motorBackRight.setPower(speed * Math.sin(degrees + 45) -  speedRotation);
    }

    /**
     * Robot moves in a certain direction at a certain speed
     * @param speed -1 to 1
     * @param degrees angle/direction of the robot relative to the front in degrees
     * @param speedRotation -1 to 1, the speed the robot rotates on the spot
     *                      speed and degrees should be left as 0 if this para != 0
     * @param distance in centimeters
     */
    public void mecMove(double speed, double degrees, double speedRotation, double distance)
    { //speed [-100,100], degrees [0, 360] to the right, speedRotation [-100,100], distance cm
        degrees=toDegrees(degrees);
        resetEncoders();
        double min = 0.0;
        if (Math.cos(degrees) == 0.0 || Math.sin(degrees) == 0.0)
        {
            min = 1.0;
        }
        else if (Math.abs(1.0/Math.cos(degrees))<= Math.abs(1.0 / Math.sin(degrees)))
        {
            min = 1.0/Math.cos(degrees);
        }
        else
        {
            min = 1.0/Math.sin(degrees);
        }

        double scaled = Math.abs(encoderScale * (distance * min / wheelCircumference));
        mecJustMove(speed, degrees, speedRotation);
//        while((Math.abs(nMotorEncoder[FrontLeft])<scaled) && (abs(nMotorEncoder[FrontRight])<scaled) && (abs(nMotorEncoder[BackLeft])< scaled) && (abs(nMotorEncoder[BackRight])< scaled))
        {
            wait1Msec(5);
//		writeDebugStreamLine("%d, %d, %d, %d ", (nMotorEncoder[FrontLeft]), (nMotorEncoder[FrontRight]), (nMotorEncoder[BackLeft]), (nMotorEncoder[BackRight]));
        }
        Stop();
        resetEncoders();
        wait1Msec(10);
    }

    /**
     * Moves until the ultrasonic sensors detects something or
     * moves until the object is out of the range
     * @param speed -1 to 1
     * @param degrees angle/direction of the robot relative to the front in degrees
     * @param speedRotation -1 to 1, the speed the robot rotates on the spot
     *                      speed and degrees should be left as 0 if this para != 0
     * @param threshold 0 to 255; optimal range from 20 to 100 in centimeter;
     *                  becomes inaccurate when out of the ramge
     * @param till true = move until detects something; false = move until
     *             the object is not detected
     */

    public void moveTillUS(double speed, double degrees, double speedRotation, double threshold, boolean till)//if till = true, move until sees something; if till = false, move until not seeing something
    {
        mecJustMove(speed, degrees, speedRotation);
        if (till){
            while (USfront.getUltrasonicLevel() > threshold){}}
        else{
            while (USfront.getUltrasonicLevel() < threshold || USback.getUltrasonicLevel() < threshold){}}//should be ||, so stop when none of them is in the threshold
        Stop();
    }

    /**
     * Moves until one sensor is touched or moves until none of them is touched
     * @param speed -1 to 1
     * @param degrees angle/direction of the robot relative to the front in degrees
     * @param speedRotation -1 to 1, the speed the robot rotates on the spot
     *                      speed and degrees should be left as 0 if this para != 0
     * @param till true = move until one of them is touched; false = move until
     *             both sensors are not touched
     */
    public void moveTillTouch(double speed, double degrees, double speedRotation, boolean till)
    {
 /*       mecJustMove(speed, degrees, speedRotation);
        if (till){
            while ((!TSreadState(TOUCHFront)) && (!TSreadState(TOUCHBack))){
                nxtDisplayCenteredTextLine(2, "%d, %d", TSreadState(TOUCHfront), TSreadState(TOUCHback));
                //if(HTGYROreadRot(gyro)>5){break;}
                if (counter>=10)
                    break;
            }
        }
        else
        {
            while ((TSreadState(TOUCHFront)) || (TSreadState(TOUCHBack))){
                //if(HTGYROreadRot(gyro)>5){break;}
                if (counter>=10)
                    break;
            }
        }*/
        Stop();
    }

    /**
     * stops the robot; all four wheels' power set to 0
     */
    public void Stop()
    {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        resetEncoders();
    }

    /**
     * turn the robot on the spot
     * @param speedrotation the speed the robot turns
     * @param degrees angle in degree not in radians
     */
    public void turnMecGyro(double speedrotation, double degrees) {
        double delTime = 0;
        double curRate = 0;
        double currHeading = 0;
        ElapsedTime Time1 = new ElapsedTime();
        //no gyro initialization?
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

    /**
     * parallel the robot with a flat surface using ultrasonic sensors
     * doesn't work very well when it is extremely angled because the
     * surface deflect the signal
     */
    public void parallel()
    {
        double difference=USfront.getUltrasonicLevel() > USback.getUltrasonicLevel()? 20:-20;//so that the sensors doesn't have to detect twice; to save batteries
        mecJustMove(0, 0, difference);
        while(Math.abs(USfront.getUltrasonicLevel()-USback.getUltrasonicLevel())>5)
        {}
        Stop();
    }

    /**
     * read values ranging from 40 to 255 with filter at 40
     * @param S a 2 elements array; S[0] = front US, S[1] = back US
     */
    public void readUSavg(double[] S)
    {
        int f=0, b=0;
        double tfront, tback;
        double tcountF = 0.0, tcountB = 0.0;
        for (int i=0; i<30; i++)
        {
            tfront = USfront.getUltrasonicLevel();
            tback = USback.getUltrasonicLevel();
            if (tfront > 40)//95
            {
                tcountF++;
                f+=tfront;
            }
            if (tback > 40)//95
            {
                tcountB++;
                b+=tback;
            }
            wait1Msec(50);
        }
        S[0]=f/tcountF;
        S[1]=b/tcountB;
        wait1Msec(1000);
    }

    public void armOut(){
        ElapsedTime armOut = new ElapsedTime();
        armOut.startTime();
        //   motor[arm] = -50;
        while(armOut.time()<2){ }
        //motor[arm] = 0;
    }

    public void armIn(){
        ElapsedTime armIn = new ElapsedTime();
        armIn.startTime();
        //   motor[arm] = 50;
        while(armIn.time()<2){ }
        //motor[arm] = 0;
    }

    private double toDegrees (double angle) {
        return angle * (180.0 / Math.PI);
    }

    private void resetEncoders(){
//        nMotorEncoder[FrontLeft] = 0;
//        nMotorEncoder[FrontRight] = 0;
//        nMotorEncoder[BackLeft] = 0;
//        nMotorEncoder[BackRight] = 0;
        wait1Msec(50);
    }

    public void start(){}
    public void stop(){}
    public void run(){}
}
