package com.example.ptr.esport;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ptr on 03.09.17.
 */

public class GPS_Service extends Service {
    int Seconds, Minutes, MilliSeconds,Hours;
    private LocationListener listener;
    private LocationManager locationManager;
    public DBSqlite sql = new DBSqlite(this);
    private final IBinder mBinder = new GPS_Service.LocalBinder();
    String url="https://maps.googleapis.com/maps/api/elevation/json?locations=";
    String key="AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A";
    boolean save=false;
    float saveTime=0;
    JSONParse jsp;
    LatLng StartP=null;
    double currTime;
    float StartTime = SystemClock.uptimeMillis();
    double sum=0;
    double sumCal=0;
   String time;
    public class LocalBinder extends Binder {
        GPS_Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return GPS_Service.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        jsp=new JSONParse();
        saveTime=10000;
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(save) {
                    float tempTime=(SystemClock.uptimeMillis()-StartTime);
                    float newTime= tempTime/(1000*3600);
                    Intent i = new Intent("loc");

                    String result=url+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude())+"&key="+key;
                    String output = null;
                    double data = 0;
                    try {
                        output = new checkURL().execute(result).get();
                        data=jsp.elevation(output);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sql.insertLocalization(location.getLatitude(),location.getLongitude(),data);
                    double speed=0;
                    LatLng EndLocation=new LatLng(location.getLatitude(),location.getLongitude());
                    double usr=dataUser();
                    if(StartP!=null) {
                        sum+=CalculationByDistance(StartP, EndLocation);
                        speed=CalculationByDistance(StartP, EndLocation)/(newTime-currTime);
                        sumCal+=(usr/24)*5*(newTime-currTime);
                    }

                    if(tempTime>=saveTime){
                        Log.d("dane","zapis");
                        Seconds = (int) (tempTime / 1000);
                        Minutes = Seconds / 60;

                        Seconds = Seconds % 60;

                        Hours = Minutes / 60;
                        MilliSeconds = (int) (tempTime % 1000);
                        time= String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":"
                                + String.format("%02d", Seconds) + ":"
                                + String.format("%03d", MilliSeconds);
                        Log.d("zapis",String.valueOf(sql.insertSport(data,time,sum,sumCal,speed)));
                        saveTime+=10000;
                    }
                    i.putExtra("elevation",data);
                    i.putExtra("sum",sum);
                    i.putExtra("Speed",speed);
                    i.putExtra("cal",sumCal);
                    StartP=EndLocation;
                    currTime=newTime;
                    sendBroadcast(i);
                }else{
                    Intent i = new Intent("loc");
                    i.putExtra("param1",location.getLatitude());
                    i.putExtra("param2",location.getLongitude());
                    sendBroadcast(i);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }

    public void getStart()
    {
        save=true;
    }
    public void getStop()
    {
        save=false;
    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        double meter=valueResult*1000;
        return km;
    }

    private double dataUser(){
        Cursor res = sql.showNameUser();
        StringBuffer buffer=new StringBuffer();
        String dane=null;
        double wzrost = 0,waga = 0;
        String data_ur=null;
        String plec=null;
        buffer.append(res);
        while(res.moveToNext())
        {
            dane=res.getString(0);

        }
        if(dane!=null) {
            Cursor res2 = sql.getInfoUser(dane);
            StringBuffer buffer2 = new StringBuffer();
            buffer.append(res2);
            while (res2.moveToNext()) {
                waga=res2.getDouble(1);
                wzrost=res2.getDouble(2);
                data_ur=res2.getString(3);
                plec=res2.getString(4);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal2=Calendar.getInstance();
        Date dtend=null;
        try{
            dtend = format.parse(data_ur);
            cal2.setTime(dtend);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int currentDayend= cal2.get(Calendar.DAY_OF_MONTH);
        int currentMonend=cal2.get(Calendar.MONTH);
        int currentyearend=cal2.get(Calendar.YEAR);
        int age=getAge(currentyearend,currentMonend,currentDayend);
        return calculateBMR(waga,wzrost,age,plec);

    }
    private double calculateBMR(double waga,double wzrost,int wiek,String plec)
    {   double bmr;
        if(plec=="Mezczyzna"){
            bmr=(13.75*waga)+(5*wzrost)-(6.76*wiek)+66;
        }else{
            bmr=(9.56 * waga) + (1.85 * wzrost) - (4.68 * wiek) + 655;
        }
        return bmr;
    }
    private int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }

}