package com.example.ptr.esport;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ptr on 03.09.17.
 */

public class ContainerMap extends Service {
    String out,out2,out3;
    int signal;
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        ContainerMap getService() {
            // Return this instance of LocalService so clients can call public methods
            return ContainerMap.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public void LoadDate(String o,String o2,String o3,int sign){
        out=o;
        out2=o2;
        out3=o3;
        int signal=sign;
        Intent i = new Intent("maps");
        i.putExtra("sygn",signal);
        i.putExtra("out1",out);
        i.putExtra("out2",out2);
        i.putExtra("out3",out3);
        sendBroadcast(i);
    }
    public void DeleteDate(int sign) {
        signal=sign;
        out=out2=out3=null;
        Intent i = new Intent("maps");
        i.putExtra("sygn",signal);
        sendBroadcast(i);
    }
}
