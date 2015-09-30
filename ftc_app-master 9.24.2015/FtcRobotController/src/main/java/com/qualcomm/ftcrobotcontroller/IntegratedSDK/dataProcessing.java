/**
 * Created by: Kara Luo
 * Date Last Modified: 5/11/2015
 * Class Description: [Sensors]
 * Contains methods to process the data collected from sensors
 * Work in Progress
 */

package com.qualcomm.ftcrobotcontroller.IntegratedSDK;

import java.lang.reflect.Method;

public class dataProcessing {

    /**
     *
     * //@param cls identifies sensor type: "UltrasonicSensor", "IRSeekerSensor", "GyroSensor", "LightSensor", etc.
     * @param typeName string, specifies sensor type: "US", "IR", "Light", "Compass", "Voltage"
     * @param sensorName string, specifies the name of the sensor
     * @param loop double, specifies number or readings to average
     * @return double, average value of sensor readings
     */
    public double average(String typeName, String sensorName, double loop){
        double returnValue=0.0;
        String methodName="get"+typeName;
        Method method=null;
      /*  try {
          //  method = Sensors.class.getMethod(methodName, sensorName.getClass());
        } catch (SecurityException e) {
            // ...
        } catch (NoSuchMethodException e) {
            // ...
        }*/
        /*try {
            method.invoke(Sensors, sensorName);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e){
        }*/

        for(int i=0; i<loop; i++){
        }
        return returnValue;
    }

}

