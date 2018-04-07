package com.example.ptr.esport;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by ptr on 03.09.17.
 */

public class StepsCounter extends Service implements SensorEventListener, StepListener {
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    private int numStopSteps;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        StepsCounter getService() {
            // Return this instance of LocalService so clients can call public methods
            return StepsCounter.this;
        }
    }
    @Override
    public void onCreate() {
        numStopSteps=0;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(StepsCounter.this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        numStopSteps=numSteps;
        Intent i = new Intent("Accelerometer");
        i.putExtra("Accel",numSteps);
        sendBroadcast(i);
    }
    public void startAccel(){
        numSteps = 0+numStopSteps;
        sensorManager.registerListener(StepsCounter.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

    }
    public void stopAccel(){
        sensorManager.unregisterListener(StepsCounter.this);
    }
    public void RestartAccel(){
        numSteps=0;
        numStopSteps=0;
        Intent i = new Intent("Accelerometer");
        i.putExtra("Accel",numSteps);
        sendBroadcast(i);
    }

}
