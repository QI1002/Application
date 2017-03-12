package io.github.qi1002.icamera;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}

//TODO01: test other phone's sensors
//TODO02: adjust screen size to full size (J4 and other camera)
//TODO03: detect the TV box area
//TODO04: review the camera code
//TODO05: play file from http
//TODO6: know the way to move videoview
//TODO7: boot from landscape mode will not show sensor data

/* SS J7 sensor list

E/SensorManager: nativeGetSensorAtIndex: name, vendor - 0, K2HH Acceleration , STM
E/SensorManager: nativeGetSensorAtIndex: name, vendor - 1, STK3013 Proximity Sensor, SENSORTEK
E/SensorManager: nativeGetSensorAtIndex: name, vendor - 2, SX9310 Grip Sensor, SEMTECH
E/SensorManager: nativeGetSensorAtIndex: name, vendor - 3, Screen Orientation Sensor, Samsung Electronics
D/camerademo: There is a android.sensor.accelerometer(K2HH Acceleration ,STM,1)
D/camerademo: There is no android.sensor.magnetic_field
D/camerademo: There is no android.sensor.orientation
D/camerademo: There is no android.sensor.gyroscope
D/camerademo: There is no android.sensor.light
D/camerademo: There is no android.sensor.pressure
D/camerademo: There is no android.sensor.temperature
D/camerademo: There is a android.sensor.proximity(STK3013 Proximity Sensor,SENSORTEK,1)
D/camerademo: There is no android.sensor.gravity
D/camerademo: There is no android.sensor.linear_acceleration
D/camerademo: There is no android.sensor.rotation_vector
D/camerademo: There is no android.sensor.relative_humidity
D/camerademo: There is no android.sensor.ambient_temperature
D/camerademo: There is no android.sensor.magnetic_field_uncalibrated
D/camerademo: There is no android.sensor.game_rotation_vector
D/camerademo: There is no android.sensor.gyroscope_uncalibrated
D/camerademo: There is no android.sensor.significant_motion
D/camerademo: There is no android.sensor.step_detector
D/camerademo: There is no android.sensor.step_counter
D/camerademo: There is no android.sensor.geomagnetic_rotation_vector
D/camerademo: There is no android.sensor.heart_rate

 */