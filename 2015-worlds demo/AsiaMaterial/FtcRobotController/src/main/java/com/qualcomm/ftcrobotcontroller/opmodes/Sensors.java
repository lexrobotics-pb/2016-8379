/**
 * Contributor: Kara Luo
 * Date Last Modified: 4/8/2015
 * Class Description: [Sensors]
 * Contains methods to get sensor values, and methods to print sensor values using telemetry
 * Sensors include: US, IR, gyro, light, acceleration, compass, voltage
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.robocol.Telemetry;

public class Sensors {

    Telemetry telemetry = new Telemetry();

    public double getUS(UltrasonicSensor US) {
        return US.getUltrasonicLevel();
    }

    public double getIR(IrSeekerSensor IR){
        return IR.getAngle();
    }

    public double getGyro(GyroSensor gyro){
        return gyro.getRotation();
    }

    public double getLight (LightSensor light){
        return light.getLightLevel();
    }

    public AccelerationSensor.Acceleration getAcceleration (AccelerationSensor accel){
        return accel.getAcceleration();
    }

    public double getCompass(CompassSensor compass){
        return compass.getDirection();
    }

    public double getVoltage(VoltageSensor volt){
        return volt.getVoltage();
    }

    public void sendUS(UltrasonicSensor US){
        telemetry.addData("US", "US: " + US.toString());
    }

    public void sendIR(IrSeekerSensor IR){
        telemetry.addData("IR", "IR: " + IR.toString());
    }

    public void sendGyro(GyroSensor gyro){
        telemetry.addData("Gyro", "gyro: " + gyro.toString());
    }

    public void sendLight(LightSensor light){ telemetry.addData("light", "light: " + light.toString());}

    public void sendAcceleration (AccelerationSensor accel){ telemetry.addData("acceleration", "acceleration: " + accel.toString());}

    public void sendCompass(CompassSensor compass){ telemetry.addData("compass", "compass: " + compass.toString());}

    public void sendVoltage (VoltageSensor voltage){ telemetry.addData("voltage", "voltage: " + voltage.toString());}
}

