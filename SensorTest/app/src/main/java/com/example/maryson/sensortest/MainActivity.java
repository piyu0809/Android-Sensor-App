package com.example.maryson.sensortest;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

    private long now = 0;
    private long timeDiff = 0;
    private long lastUpdate = 0;
    private long lastShake = 0;

    private float lastX, lastY, lastZ;
    private float lastgX, lastgY, lastgZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private boolean flashLightStatus = false;

    private float x  = 0;
    private float y  = 0;
    private float z  = 0;
    private float gx  = 0;
    private float gy  = 0;
    private float gz  = 0;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private int count = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float deltagX = 0;
    private float deltagY = 0;
    private float deltagZ = 0;

    private ImageView imageFlashlight;

    private float vibrateThreshold = 1.0f;


    private static int interval = 1000;


    private TextView currentX, currentY, currentZ, currentgX, currentgY, currentgZ,maxX, maxY, maxZ, countview;

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        imageFlashlight = (ImageView) findViewById(R.id.imageFlashlight);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            //vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
       /*
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        currentgX = (TextView) findViewById(R.id.currentgX);
        currentgY = (TextView) findViewById(R.id.currentgY);
        currentgZ = (TextView) findViewById(R.id.currentgZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    */
        countview = (TextView) findViewById(R.id.count);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {



    }

    @Override
    public void onSensorChanged(SensorEvent event) {



        // clean current values
        displayCleanValues();

        now = event.timestamp;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {

            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];


            }

        if (lastUpdate == 0) {
            lastUpdate = now;
            lastShake = now;
            lastX = x;
            lastY = y;
            lastZ = z;
            lastgX = gx;
            lastgY = gy;
            lastgZ = gz;



        }
        else {
            timeDiff = now - lastUpdate;

            if (timeDiff > 0) {

                deltaX = Math.abs(lastX - x);
                deltaY = Math.abs(lastY - y);
                deltaZ = Math.abs(lastZ - z);

                deltagX = Math.abs(lastgX - gx);
                deltagY = Math.abs(lastgY - gy);
                deltagZ = Math.abs(lastgZ - gz);


                if (deltaX < 0.9)
                    deltaX = 0;
                if (deltaY < 0.3)
                    deltaY = 0;
                if (deltaZ < 0.8)
                    deltaZ = 0;
                if (deltagX < 0.2)
                    deltagX = 0;
                if (deltagY < 0.2)
                    deltagY = 0;
                if (deltagZ < 0.2)
                    deltagZ = 0;




                if (Float.compare(deltaZ, 0.9f) > 0 && Float.compare(deltaZ, 1.2f) < 0  ) {

                    if (now - lastShake >= interval) {
                        lastShake = now;
                        count = count + 1;

                        if(count == 1) {
                            v.vibrate(50);
                            interval = 5000;
                            starttorch();
                        }
                        else
                        {
                            count = 0;
                            interval = 500;
                        }

                        displayMaxValues();

                    } else {


                    }

                }
                lastX = x;
                lastY = y;
                lastZ = z;
                lastgX = gx;
                lastgY = gy;
                lastgZ = gz;
                lastUpdate = now;
            } else {

            }
        }

        // get the change of the x,y,z values of the accelerometer


        // if the change is below 2, it is just plain noise



        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values

          }

    public void displayCleanValues() {
        //currentX.setText("0.0");
        //currentY.setText("0.0");
        //currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        //currentX.setText(Float.toString(lastX));
       // currentY.setText(Float.toString(lastY));
       // currentZ.setText(Float.toString(lastZ));

       // currentgX.setText(Float.toString(lastgX));
       // currentgY.setText(Float.toString(lastgY));
      //  currentgZ.setText(Float.toString(lastgZ));

       // countview.setText(Float.toString(now));
    }

    public void starttorch()
    {
        {
            //  ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
            Random rnd = new Random();
            CameraManager cameraManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            }
            int color = Color.argb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            LinearLayout bgElement = (LinearLayout) findViewById(R.id.container) ;
            //bgElement.setBackgroundColor(color);


            Toast.makeText(this, "Tap detected", Toast.LENGTH_SHORT).show();
            // CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if(flashLightStatus == false) {
                try {
                    String cameraId = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cameraId = cameraManager.getCameraIdList()[0];
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, true);
                        flashLightStatus = true;
                        countview.setText("ON");
                        imageFlashlight.setImageResource(R.drawable.btn_switch_on);
                    }


                } catch (CameraAccessException e) {
                }
            }
            else {
                try {
                    String cameraId = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cameraId = cameraManager.getCameraIdList()[0];
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false);
                        flashLightStatus = false;
                        countview.setText("OFF");
                        imageFlashlight.setImageResource(R.drawable.btn_switch_off);
                    }


                } catch (CameraAccessException e) {
                }

            }
            //   camera = Camera.open();
            //   Camera.Parameters parameters = camera.getParameters();
            //   parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            //   camera.setParameters(parameters);


        }
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
         {
            deltaXMax = lastX;
           // maxX.setText(Float.toString(deltaXMax));
        }
         {
            deltaYMax = lastY;
           // maxY.setText(Float.toString(deltaYMax));
        }
        {
            deltaZMax = lastZ;
          //  maxZ.setText(Float.toString(deltaZMax));
        }
    }
}

