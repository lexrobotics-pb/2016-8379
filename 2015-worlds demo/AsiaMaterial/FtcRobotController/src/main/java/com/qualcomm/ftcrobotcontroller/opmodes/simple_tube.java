package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Kara on 4/14/2015.
 * Last Updated: 4/16/15
 * For now the values remain as those from RobotC
 */
public class simple_tube extends OpMode{
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
    Servo arm;
    Servo holder;
    Servo trigger;

    UltrasonicSensor USfront;
    UltrasonicSensor USback;

    GyroSensor gyro;

    public simple_tube()
    {}

    @Override
    public void start(){
       //telemetry.addData("Robot", "Constructor start");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorBackRight.setDirection(DcMotor.Direction.REVERSE); //reverses back right motor
        motorThrower= hardwareMap.dcMotor.get("motorThrower");
        motorLift= hardwareMap.dcMotor.get("motorLift");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE); //reverse front left motor
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");

        gyro = hardwareMap.gyroSensor.get("gyro");

        grabber = hardwareMap.servo.get("grabber");
        arm = hardwareMap.servo.get("arm");
        hood = hardwareMap.servo.get("hood");
        holder = hardwareMap.servo.get("holder");
        trigger = hardwareMap.servo.get("trigger");

        grabber.setPosition(1);
        arm.setPosition(0.51);
        hood.setPosition(0.235);
        trigger.setPosition(0.714);
        holder.setPosition(0.51);
        switchAllToRead();
    }

    @Override
    public void run(){
        //Test movement
        switchAllToWrite();

        motorBackRight.setPower(1.00);
        motorFrontRight.setPower(1.00);
        motorFrontLeft.setPower(1.00);
        motorBackLeft.setPower(1.00);
//        wait1Msec(1000);
//        motorBackRight.setPower(0.00);
//        motorFrontRight.setPower(0.00);
//        motorFrontLeft.setPower(0.00);
//        motorBackLeft.setPower(0.00);
        //switchAllToRead();
        /* Test thrower
        motorThrower.setPower(-1.00);
        wait1Msec(2000);
        motorThrower.setPower(0.00);
        wait1Msec(1000);
        motorThrower.setPower(1.00);*/

        //wait1Msec(2000);

        /*//Movement
        mecJustMove(60, 0, 0);
        wait1Msec(3500);
        Stop();
        wait1Msec(250);

        mecMove(-78, 90, 0,  52.0);//strafe left
        wait1Msec(250);

        mecMove(78, 0, 0, 150.0);//forward toward goal
        double grabberDelayTime = 3.0;
        wait1Msec(grabberDelayTime*1000.0);
        setGrabber(150);

        wait1Msec(500);
        mecMove(-78, 0, 0, 10.0);//back a bit
        wait1Msec(100);
        setHood(130); //hood in place
        mecMove(-78, 90, 0, 10.0);//side shift a bit

        //turnMecGyro(-60.0,155.0);//turn toward the PK
        motorThrower.setPower(-1.0); //start thrower motor
        mecMove(-78.0, 0, 0, 240.0);//**length: move pass the kick stand
        wait1Msec(250);
        turnMecGyro(-60.0,180.0);//turn inside pz
        wait1Msec(250);
        mecMove(78.0, 90, 0, 120.0);//**right strafe significantly pz
        wait1Msec(15000);*/
    }

    @Override
    public void stop(){
        setHood(60);
        hoodHolderHold();
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
     * @param speed [-100,100]
     * @param degrees angle/direction of the robot relative to the front in degrees, positive = clockwise
     * @param speedRotation [-100,100], the speed the robot rotates on the spot
     *                      speed and degrees should be left as 0 if this para != 0
     */
    public void mecJustMove(double speed, double degrees, double speedRotation)
    {
        switchAllToWrite();
        speed/=100.0;
        speedRotation/=100.0;
        double radians = toRadians(degrees);
        motorFrontLeft.setPower(1.00);
        motorFrontRight.setPower(1.00);
        motorBackLeft.setPower(1.00);
        motorBackRight.setPower(1.00);
        /*motorFrontLeft.setPower(speed * Math.sin(radians + Math.PI/4) + speedRotation);
        motorFrontRight.setPower(speed * Math.cos(radians + Math.PI/4) - speedRotation);
        motorBackLeft.setPower(speed * Math.cos(radians + Math.PI/4) + speedRotation);
        motorBackRight.setPower(speed * Math.sin(radians + Math.PI/4) -  speedRotation);*/
        switchAllToRead();
    }

    /**
     * Robot moves in a certain direction at a certain speed
     * @param speed [-100,100]
     * @param degrees angle/direction of the robot relative to the front in degrees, positive = clockwise
     * @param speedRotation [-100,100], the speed the robot rotates on the spot
     *                      speed and degrees should be left as 0 if this para != 0
     * @param distance in centimeters
     */
    public void mecMove(double speed, double degrees, double speedRotation, double distance)
    {
        switchAllToWrite();
        speed/=100.0;
        speedRotation/=100.0;
        double radians=toRadians(degrees);
        resetEncoders();
        double min = 0.0;
        if (Math.cos(radians) == 0.0 || Math.sin(radians) == 0.0)
        {
            min = 1.0;
        }
        else if (Math.abs(1.0/Math.cos(radians))<= Math.abs(1.0 / Math.sin(radians)))
        {
            min = 1.0/Math.cos(radians);
        }
        else
        {
            min = 1.0/Math.sin(radians);
        }

        double scaled = Math.abs(encoderScale * (distance * min / wheelCircumference));
        mecJustMove(speed, degrees, speedRotation);
        while(Math.abs(motorFrontLeft.getCurrentPosition())<scaled
                && Math.abs(motorFrontRight.getCurrentPosition()) <scaled
                && Math.abs(motorBackLeft.getCurrentPosition())< scaled
                && Math.abs(motorBackRight.getCurrentPosition()) < scaled){}
        Stop();

        resetEncoders();
        switchAllToRead();
        wait1Msec(10);
    }

    public void switchAllToRead(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
    }

    public void switchAllToWrite(){
        motorFrontLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorFrontRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackLeft.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        motorBackRight.setDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
    }

    /**
     * Moves until the ultrasonic sensors detects something or
     * moves until the object is out of the range
     * @param speed [-100,100]
     * @param degrees angle/direction of the robot relative to the front in degrees, positive = clockwise
     * @param speedRotation [-100,100], the speed the robot rotates on the spot
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
     * @param speed [-100,100]
     * @param degrees angle/direction of the robot relative to the front in degrees, positive = clockwise
     * @param speedRotation [-100,100], the speed the robot rotates on the spot
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
        switchAllToWrite();
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        resetEncoders();
        switchAllToRead();
    }

    /**
     * turn the robot on the spot
     * @param speedrotation [-100,100]
     * @param degrees angle in degree not in radians
     */
    public void turnMecGyro(double speedrotation, double degrees) {
        double delTime = 0;
        double curRate = 0;
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
            //  wait1Msec(5);
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

  /*  public void armOut(){
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
        while(armIn.time()<2){}
        //motor[arm] = 0;
    }*/

    private double toRadians (double degrees)
    {
        return degrees/180.0*Math.PI;
    }

    private void resetEncoders(){
        switchAllToWrite();
        motorFrontLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorFrontRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorBackLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorBackRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        wait1Msec(50);
        switchAllToRead();
    }

    /**
     * @param position [0, 255]
     */
    public void setGrabber(double position)
    {
        position = position/255.0;
        grabber.setPosition(position);
    }

    /**
     * @param position [0, 255]
     */
    public void setHood(double position)
    {
        position = position/255.0;
        hood.setPosition(position);
    }

    public void hoodHolderRelease()
    {
        holder.setPosition(0.39);
        //wait1Msec(200.00);
        holder.setPosition(0.5);
    }

    public void hoodHolderHold()
    {
        holder.setPosition(1);
        //wait1Msec(200.00);
        holder.setPosition(0.5);
    }

    /**
     * @param speed [-100,100]
     */
    public void setThrower(double speed)
    {
        speed/=100.0;
        switchAllToWrite();
        motorThrower.setPower(speed);
        switchAllToRead();
    }



}
