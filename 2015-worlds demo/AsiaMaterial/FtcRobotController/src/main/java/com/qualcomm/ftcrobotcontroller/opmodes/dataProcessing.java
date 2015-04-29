/**
 * Contributor: Kara Luo
 * Date Last Modified: 4/8/2015
 * Class Description: [Sensors]
 * Contains methods to process the data collected from sensors
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.opmodes.Sensors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class dataProcessing {

    /**
     *
     * @param cls indetifies sensor type: "UltrasonicSensor", "IRSeekerSensor", "GyroSensor", "LightSensor", etc.
     * @param typeName string, specifies sensor type: "US", "IR", "Light", "Compass", "Voltage"
     * @param sensorName string, specifies the name of the sensor
     * @param loop double, specifies number or readings to average
     * @return double, average value of sensor readings
     */
    public double average(Class<?> cls, String typeName, Class<?> sensorName, double loop){
        double returnValue=0.0;
        String methodName="get"+typeName;
        java.lang.reflect.Method method=null;
        for(int i=0; i<loop; i++){
            try {
                method = cls.getMethod(methodName, cls);
            } catch (NoSuchMethodException e) {
                System.out.println("No such method, please verify specified sensor type");
            }
            try {
              method.invoke(cls, sensorName);
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e){
            }
        }
        return returnValue;
    }

}

