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

//TODO01: detect the TV box area
//TODO02: review the camera code
//TODO03: play video file from http
//TODO04: boot from landscape mode will not show sensor data
//TODO05: detect the ROI from YUV420
//TODO06: show other sensors' information

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

/* HTC (htc_a50cml_dtul)

03-12 23:52:23.520 9306-9306/? D/camerademo: There is a android.sensor.accelerometer(BOSCH Acceleration Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.525 9306-9306/? D/camerademo: There is a android.sensor.magnetic_field(BOSCH Magnetic Field Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.529 9306-9306/? D/camerademo: There is a android.sensor.orientation(BOSCH Orientation Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.531 9306-9306/? D/camerademo: There is no android.sensor.gyroscope
03-12 23:52:23.533 9306-9306/? D/camerademo: There is a android.sensor.light(CM36686 Light sensor,Capella Microsystems,1)
03-12 23:52:23.534 9306-9306/? D/camerademo: There is no android.sensor.pressure
03-12 23:52:23.535 9306-9306/? D/camerademo: There is no android.sensor.temperature
03-12 23:52:23.537 9306-9306/? D/camerademo: There is a android.sensor.proximity(CM36686 Proximity sensor,Capella Microsystems,1)
03-12 23:52:23.538 9306-9306/? D/camerademo: There is a android.sensor.gravity(BOSCH Gravity Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.540 9306-9306/? D/camerademo: There is a android.sensor.linear_acceleration(BOSCH Linear Acceleration Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.542 9306-9306/? D/camerademo: There is a android.sensor.rotation_vector(BOSCH Rotation Vector Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.544 9306-9306/? D/camerademo: There is no android.sensor.relative_humidity
03-12 23:52:23.546 9306-9306/? D/camerademo: There is no android.sensor.ambient_temperature
03-12 23:52:23.547 9306-9306/? D/camerademo: There is a android.sensor.magnetic_field_uncalibrated(BOSCH Magnetic Field Uncalibrated Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.548 9306-9306/? D/camerademo: There is no android.sensor.game_rotation_vector
03-12 23:52:23.550 9306-9306/? D/camerademo: There is no android.sensor.gyroscope_uncalibrated
03-12 23:52:23.551 9306-9306/? D/camerademo: There is no android.sensor.significant_motion
03-12 23:52:23.553 9306-9306/? D/camerademo: There is no android.sensor.step_detector
03-12 23:52:23.554 9306-9306/? D/camerademo: There is no android.sensor.step_counter
03-12 23:52:23.557 9306-9306/? D/camerademo: There is a android.sensor.geomagnetic_rotation_vector(BOSCH Geomagnetic Rotation Vector Sensor,Bosch Sensortec GmbH,3060102)
03-12 23:52:23.559 9306-9306/? D/camerademo: There is no android.sensor.heart_rate

 */

/*  kibo+

01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.accelerometer(ACCELEROMETER,MTK,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.magnetic_field(MAGNETOMETER,MTK,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.orientation(ORIENTATION,MTK,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.gyroscope(GYROSCOPE,MTK,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.light(LIGHT,MTK,1)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.pressure
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.temperature
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.proximity(PROXIMITY,MTK,1)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.gravity(Gravity Sensor,AOSP,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.linear_acceleration(Linear Acceleration Sensor,AOSP,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.rotation_vector(Rotation Vector Sensor,AOSP,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.relative_humidity
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.ambient_temperature
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.magnetic_field_uncalibrated
01-07 04:07:56.878 28652-28652/? D/camerademo: There is a android.sensor.game_rotation_vector(Game Rotation Vector Sensor,AOSP,3)
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.gyroscope_uncalibrated
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.significant_motion
01-07 04:07:56.878 28652-28652/? D/camerademo: There is no android.sensor.step_detector
01-07 04:07:56.879 28652-28652/? D/camerademo: There is no android.sensor.step_counter
01-07 04:07:56.879 28652-28652/? D/camerademo: There is a android.sensor.geomagnetic_rotation_vector(GeoMag Rotation Vector Sensor,AOSP,3)
01-07 04:07:56.879 28652-28652/? D/camerademo: There is no android.sensor.heart_rate

 */