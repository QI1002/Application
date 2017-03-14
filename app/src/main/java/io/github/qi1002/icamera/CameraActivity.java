/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.qi1002.icamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.VideoView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CameraActivity extends Activity implements SensorEventListener {

    // Storage Permissions variables
    private static final int REQUEST_ACCESS= 1;
    private static String[] PERMISSIONS_REQUESTS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private Camera2BasicFragment fragment;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            fragment = Camera2BasicFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }

        DetectionROI roi = new DetectionROI(1920, 1440);
        roi.detectROI(null, null, null);
        roi.release();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // request camera access right
        verifyCameraPermissions();

        // get the sensors information
        setupSensors();

        // get the video player
        Handler handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                setupVideoPlayer();
            }
        };

        Message message = new Message();
        message.arg1 = 0;
        handler.sendMessage(message);
    }

    private void setupSensors()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        String[] SensorStrings = {
                "",
                Sensor.STRING_TYPE_ACCELEROMETER,
                Sensor.STRING_TYPE_MAGNETIC_FIELD,
                Sensor.STRING_TYPE_ORIENTATION,
                Sensor.STRING_TYPE_GYROSCOPE,
                Sensor.STRING_TYPE_LIGHT,
                Sensor.STRING_TYPE_PRESSURE,
                Sensor.STRING_TYPE_TEMPERATURE,
                Sensor.STRING_TYPE_PROXIMITY,
                Sensor.STRING_TYPE_GRAVITY,
                Sensor.STRING_TYPE_LINEAR_ACCELERATION,
                Sensor.STRING_TYPE_ROTATION_VECTOR,
                Sensor.STRING_TYPE_RELATIVE_HUMIDITY,
                Sensor.STRING_TYPE_AMBIENT_TEMPERATURE,
                Sensor.STRING_TYPE_MAGNETIC_FIELD_UNCALIBRATED,
                Sensor.STRING_TYPE_GAME_ROTATION_VECTOR,
                Sensor.STRING_TYPE_GYROSCOPE_UNCALIBRATED,
                Sensor.STRING_TYPE_SIGNIFICANT_MOTION,
                Sensor.STRING_TYPE_STEP_DETECTOR,
                Sensor.STRING_TYPE_STEP_COUNTER,
                Sensor.STRING_TYPE_GEOMAGNETIC_ROTATION_VECTOR,
                Sensor.STRING_TYPE_HEART_RATE,
        };

        for (int i = Sensor.TYPE_ACCELEROMETER; i<= Sensor.TYPE_HEART_RATE; i++ ) {
            Sensor mSensor = mSensorManager.getDefaultSensor(i);
            if (mSensor == null)
                Log.d("camerademo", "There is no " + SensorStrings[i]);
            else
                Log.d("camerademo", "There is a " + SensorStrings[i] + "(" + mSensor.getName() +
                        "," + mSensor.getVendor() + "," + mSensor.getVersion() + ")");
        }

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void setupVideoPlayer()
    {
        if (fragment == null || fragment.videoView == null) {
            Message message = new Message();
            message.arg1 = 0;
            Handler handler=new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    setupVideoPlayer();
                }
            };
            handler.sendMessage(message);
            return;
        }

        try {
            VideoView videoView = fragment.videoView;

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });

            videoView.setVideoPath("/sdcard/testfile.mp4");
            videoView.requestFocus();
            videoView.start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean verifyCameraPermissions() {
        // Check if we have read or write permission
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_REQUESTS,
                    REQUEST_ACCESS
            );

            return true;
        }

        return false;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (fragment != null && fragment.textinfo1 != null)
                fragment.textinfo1.setText(getString(R.string.x_prompt) + nf.format(event.values[0]));
            if (fragment != null && fragment.textinfo2 != null)
                fragment.textinfo2.setText(getString(R.string.y_prompt) + nf.format(event.values[1]));
            if (fragment != null && fragment.textinfo3 != null)
                fragment.textinfo3.setText(getString(R.string.z_prompt) + nf.format(event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // Distance from object
            if (fragment != null && fragment.textinfo4 != null)
                fragment.textinfo4.setText(getString(R.string.distance_prompt) + nf.format(event.values[0]));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Sensor.manager.SENSOR_DELAY_FASTEST	  0ms
        //Sensor.manager.SENSOR_DELAY_GAME	  20ms
        //Sensor.manager.SENSOR_DELAY_UI	  60ms
        //Sensor.manager.SENSOR_DELAY_NORMAL	  200ms
        if (mAccelerometer != null)
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (mProximity != null)
            mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
