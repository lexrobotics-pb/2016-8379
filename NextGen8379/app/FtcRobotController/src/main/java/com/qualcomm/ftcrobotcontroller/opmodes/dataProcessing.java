/**
 * Contributor: Kara Luo
 * Date Last Modified: 4/8/2015
 * Class Description: [Sensors]
 * Contains methods to process the data collected from sensors
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.opmodes.Sensors;

import java.lang.reflect.Method;

public class dataProcessing {

    /**
     *
     * @param sensorType string, specifies sensor type: "US", "IR", "Light", "Compass", "Voltage"
     * @param sensorName string, specifies the name of the sensor
     * @param loop double, specifies number or readings to average
     * @return double, average value of sensor readings
     */
    public double average(String sensorType, String sensorName, double loop){
        double returnValue=0.0;
        String methodName="get"+sensorType;
        try {
            Method method = getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            System.out.println("No such method, please verify specified sensor type");
        }
        for(int i=0; i<loop; i++){
        }
        return returnValue;
    }

}

