package com.example.ptr.esport;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import org.junit.rules.Stopwatch;

import java.util.Random;

/**
 * Created by ptr on 03.09.17.
 */

public class StopWatch_Service extends Service{
   DBSqlite sql;
    private final IBinder mBinder = new LocalBinder();
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds,Hours;
    private boolean mIsRunning;
    int ktory;

    /********/
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    private int numStopSteps;
    @Override
    public void onCreate() {
        handler = new Handler();
        sql=new DBSqlite(this);

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        StopWatch_Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return StopWatch_Service.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public Runnable runnable = new Runnable() {

        public void run() {
            if (!mIsRunning) {
                return; // stop when told to stop
            } else {
                MillisecondTime = SystemClock.uptimeMillis() - StartTime;

                UpdateTime = TimeBuff + MillisecondTime;

                Seconds = (int) (UpdateTime / 1000);

                Minutes = Seconds / 60;

                Seconds = Seconds % 60;

                Hours = Minutes / 60;
                MilliSeconds = (int) (UpdateTime % 1000);
                Intent i = new Intent("timer");
                i.putExtra("stopwatch", "" + String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":"
                        + String.format("%02d", Seconds) + ":"
                        + String.format("%03d", MilliSeconds));
                sendBroadcast(i);


                handler.postDelayed(this, 0);
            }
        }
    };

    public void startTime(){
        Cursor res=sql.getSport();
        String st = null;
        int k=0;
        while (res.moveToNext()) {
            st = res.getString(0);
            ktory = res.getInt(1);
        }
        mIsRunning=true;
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }
    public void stopTime(){
        mIsRunning=false;
        TimeBuff += MillisecondTime;
        handler.removeCallbacks(runnable);
        Log.d("czy koniec","tak");
    }
    public void restartTime(){

        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;

        Intent i = new Intent("timer");
        i.putExtra("stopwatch",""+String.format("%02d",0)+":" +String.format("%02d", 0) + ":"
                + String.format("%02d", 0) + ":"
                + String.format("%03d", 0));
        sendBroadcast(i);
    }
    public int getKtory(){
        return ktory;
    }
}
