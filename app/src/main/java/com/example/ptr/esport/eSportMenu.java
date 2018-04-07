package com.example.ptr.esport;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class eSportMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Spinner sp1;
    DBSqlite database;
    TextView tx1,tx2,tx3,tx4,tx5,tx6,tx7,tx8;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiver2;
    private BroadcastReceiver broadcastReceiver3;
    StopWatch_Service mService;
    private StepsCounter steps;
    private GPS_Service gps;
    boolean mBound = false;
    boolean mBound2 = false;
    boolean mBound3 = false;
    boolean isRunning;
    String sport;
    int ktory;
    Button bt1,bt2;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sport_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database=new DBSqlite(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tx1=(TextView)findViewById(R.id.textView101);
        tx2=(TextView)findViewById(R.id.textView102);
        tx3=(TextView)findViewById(R.id.textView103);
        tx4=(TextView)findViewById(R.id.textView104);
        tx5=(TextView)findViewById(R.id.textView105);
        tx6=(TextView)findViewById(R.id.textView106);
        tx7=(TextView)findViewById(R.id.textView107);
        tx8=(TextView)findViewById(R.id.textView108);
        bt1=(Button)findViewById(R.id.b8);
        bt2=(Button)findViewById(R.id.b7);
        tx1.setText("00:00:00:00");
        tx2.setText("0 km");
        tx3.setText("0");
        tx4.setText("0");
        tx5.setText("0");
        tx7.setText("0");
        tx8.setText("0");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-LL-yyyy HH:mm");
        tx6.setText(df.format(c.getTime()));

        sp1=(Spinner)findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Chód");
        list.add("Bieg");
        list.add("Rolki");
        list.add("Rower");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(dataAdapter);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dane = adapterView.getSelectedItem().toString();
                int k=i;
                setSport(dane,k);

                switch(k){
                    case 0:
                    case 1: tx7.setEnabled(true);
                        break;
                    default: tx7.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.d("dane","onCreate");
        if(database.isRunning()){
            isRunning=true;
            Cursor res=database.getSport();
            String st = null;
            int k=0;
            while (res.moveToNext()) {
                st = res.getString(0);
                k = res.getInt(1);
            }
            setSport(st,k);
            bt1.setEnabled(!isRunning);
            sp1.setEnabled(!isRunning);

        }else{isRunning=false;
            bt1.setEnabled(!isRunning);
            sp1.setEnabled(!isRunning);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("dane","onResume");

        sp1.setSelection(getIndex(sp1, sport));
        if(sport!=null) {
            switch (sport) {
                case "Chód":
                case "Bieg":
                    tx7.setEnabled(true);

                    if(broadcastReceiver2 == null){
                        broadcastReceiver2 = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String dane=intent.getExtras().get("Accel").toString();
                                if(dane==null)
                                    tx7.setText("0");
                                else
                                    tx7.setText(dane);
                            }
                        };
                    }
                    registerReceiver(broadcastReceiver2,new IntentFilter("Accelerometer"));
                    break;
                default:
                    tx7.setEnabled(false);
            }
        }
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String dane=intent.getExtras().get("stopwatch").toString();
                    tx1.setText(dane);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("timer"));

        if(broadcastReceiver3 == null){
            broadcastReceiver3 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    double result = intent.getDoubleExtra("elevation", 0);
                    if (result > 0) {
                        tx5.setText(new DecimalFormat("###.##").format(result));
                    }
                    double sum = intent.getDoubleExtra("sum", 0);
                    if (sum > 0) {
                        if (sum < 1.0) {
                            double sum2 = sum * 1000;
                            tx2.setText(new DecimalFormat("###.##").format(sum2));
                        } else {
                            tx2.setText(new DecimalFormat("##.##").format(sum));
                        }
                    }
                    double speed = intent.getDoubleExtra("Speed", 0);
                    if (speed > 0)
                        tx3.setText(new DecimalFormat("###.##").format(speed));
                    double calories = intent.getDoubleExtra("cal", 0);
                    if (calories > 0)
                        tx4.setText(new DecimalFormat("###.##").format(calories));
                    Cursor sp = database.avgSpeed();
                    if (sp.getCount() != 0 && sp != null) {
                        while (sp.moveToNext()) {
                            double xxx = sp.getDouble(0);
                           tx8.setText(new DecimalFormat("###.##").format(xxx));
                        }
                    }

                }

            };
        }
        registerReceiver(broadcastReceiver3,new IntentFilter("loc"));


    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        unbindService(mConnection2);
        unbindService(mConnection3);
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
        if(broadcastReceiver2!=null){
            unregisterReceiver(broadcastReceiver2);
            broadcastReceiver2=null;
        }
        if(broadcastReceiver3!=null){
            unregisterReceiver(broadcastReceiver3);
            broadcastReceiver3=null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.e_sport_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
        } /*else if (id == R.id.nav_slideshow) {
        } */else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_sendf) {
        }else if (id == R.id.nav_sends) {
        }else if (id == R.id.nav_sendi) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, StopWatch_Service.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Intent intent2 = new Intent(this, StepsCounter.class);
        bindService(intent2, mConnection2, Context.BIND_AUTO_CREATE);
        Intent intent3 = new Intent(this, GPS_Service.class);
        bindService(intent3, mConnection3, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StopWatch_Service.LocalBinder binder = (StopWatch_Service.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private ServiceConnection mConnection2 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StepsCounter.LocalBinder binder = (StepsCounter.LocalBinder) service;
            steps = binder.getService();
            mBound2 = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound2= false;
        }
    };

    private ServiceConnection mConnection3 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPS_Service.LocalBinder binder = (GPS_Service.LocalBinder) service;
            gps = binder.getService();
            mBound3 = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound3= false;
        }
    };
    public void clicked(MenuItem item) {
        boolean test=database.deleteUser();
        if(test==true) {
            Toast.makeText(eSportMenu.this, "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(eSportMenu.this, Login.class);
            startActivity(inten);
        }else
            Toast.makeText(eSportMenu.this, "błąd wylogowania", Toast.LENGTH_SHORT).show();
    }

    public void MapClick(View view) {
        Intent inten=new Intent(eSportMenu.this,eSportMap1.class);
        startActivity(inten);
    }

    public void wybor(MenuItem item) {
        Intent inten=new Intent(eSportMenu.this,eSportMap2.class);
        startActivity(inten);
    }

    public void loading(MenuItem item) {
        Intent inten=new Intent(eSportMenu.this,Load.class);
        startActivity(inten);
    }

    public void delete(MenuItem item) {
        boolean test=database.deleteFile();
        if(test) {
            Toast.makeText(getBaseContext(), "mapa zostala usunieta", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Blad usuwania", Toast.LENGTH_LONG).show();
        }
    }

    public void start(View view) {
        if(!database.isRunning()) {
            if (database.insertIsRunning(sport, ktory)) {
                Toast.makeText(getBaseContext(), "Dane zaladowanie", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Blad ladowania", Toast.LENGTH_LONG).show();
            }
        }
        if (mBound) {
            mService.startTime();
            bt1.setEnabled(false);
            bt2.setEnabled(false);
            sp1.setEnabled(false);
        }
        if (mBound2) {
            steps.startAccel();
        }
        if (mBound3) {
            gps.getStart();
        }
    }

    public void stop(View view) {

        if (mBound) {
           mService.stopTime();
            bt1.setEnabled(true);
            bt2.setEnabled(true);
        }
        if (mBound2) {
            steps.stopAccel();
        }
        if (mBound3) {
            gps.getStop();
        }
    }

    public void restart(View view) {
        if(database.deleteIsRunning()) {
            Toast.makeText(getBaseContext(), "Dane usunieto", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Blad ladowania", Toast.LENGTH_LONG).show();
        }
        tx5.setText("0");
        tx2.setText("0 km");
        tx3.setText("0");
        tx4.setText("0");
        tx8.setText("0");
        if (mBound) {
            mService.restartTime();
            sp1.setEnabled(true);
        }
        if (mBound2) {
            steps.RestartAccel();
        }
            boolean test=database.deleteAllLoc();
            boolean test2=database.deleteAll();
            if(test && test2) {
                Toast.makeText(getBaseContext(), "mapa zostala usunieta", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getBaseContext(), "Blad usuwania", Toast.LENGTH_LONG).show();

        }
    }
    public void setSport(String s,int k){
        sport=s;
        ktory=k+1;
    }

    public void hist(MenuItem item) {
        Intent inten=new Intent(eSportMenu.this,history_activity.class);
        startActivity(inten);
    }

    public void stat(MenuItem item) {
        Intent inten=new Intent(eSportMenu.this,Statistic.class);
        startActivity(inten);
    }

//sport TEXT,czas_sum NUMERIC,odl_sum NUMERIC,l_krok_sum NUMERIC,kalorie NUMERIC,predkosc NUMEIC, data_wyk
    public void saveData(View view) {
        Cursor res = database.showNameUser();
        String dane=null;
        int id_u = 0;
        while(res.moveToNext())
        {
            dane=res.getString(0);
        }
        if(dane!=null) {
            Cursor res2 = database.getInfoUser(dane);
            while (res2.moveToNext()) {
                id_u = res2.getInt(0);
            }
            Log.d("username",String.valueOf(id_u));
        }

        Log.d("czy_zap",String.valueOf(database.insertAllSport(id_u,sport,tx1.getText().toString(),tx2.getText().toString(),tx7.getText().toString(),tx4.getText().toString(),tx3.getText().toString(),tx6.getText().toString())));

        Cursor res3=database.selectAllData();
        if(res3.getCount()!=0){
            while (res3.moveToNext()) {
                Log.d("Calk_zap",String.valueOf(database.insertSportStorage(id_u,res3.getDouble(0),res3.getString(1),res3.getDouble(2),res3.getDouble(3),res3.getDouble(4),tx6.getText().toString())));
            }
        }else{
            Log.d("error","pusto");
        }
        Cursor res4=database.getAllLocalization();
        if(res4.getCount()!=0){
            while (res4.moveToNext()) {
                Log.d("Calk_zap",String.valueOf(database.insertLocalizationStorage(id_u,res4.getDouble(1),res4.getDouble(2),res4.getDouble(3),tx6.getText().toString())));
            }
        }else{
            Log.d("error","pusto");
        }
    }


    /****************************************************/
    public void checkedmap(View view) throws ParseException {
      /*  Cursor res2=database.checkAll2();
        if(res2.getCount()!=0){
            while (res2.moveToNext()) {
                Log.d("id_user",String.valueOf(res2.getInt(0)));
                Log.d("npm",res2.getString(1));
                Log.d("stoper",res2.getString(2));
                Log.d("odl",res2.getString(3));
                Log.d("l_krok",res2.getString(4));
                Log.d("pred",res2.getString(5));
                Log.d("data",res2.getString(6));

            }
        }else{
            Log.d("error","pusto");
        }*/
//"create table IF NOT EXISTS " + TABLE_NAME2 + "(id_tren INTEGER PRIMARY KEY AUTOINCREMENT,id_user INTEGER,sport TEXT,czas_sum NUMERIC,odl_sum NUMERIC,l_krok_sum NUMERIC,kalorie NUMERIC,predkosc NUMEIC, data_wyk TEXT, FOREIGN KEY('id_user') REFERENCES " + TABLE_NAME1 + "(id_user))";

        Cursor res=database.checkAll();
        if(res.getCount()!=0){
            while (res.moveToNext()) {
                Log.d("id_user",String.valueOf(res.getInt(0)));
                Log.d("sport",res.getString(1));
                Log.d("czas_sum",res.getString(2));
                Log.d("odl_sum",res.getString(3));

                Log.d("l_krok_sum",res.getString(4));
                Log.d("kalorie",res.getString(5));
                Log.d("predkosc",res.getString(6));
                Log.d("data",res.getString(7));
            }
        }else{
            Log.d("error","pusto");
        }
        Cursor res2=database.checkAll3();
        if(res2.getCount()!=0){
            while (res2.moveToNext()) {
                Log.d("id_user",String.valueOf(res2.getInt(0)));
                Log.d("lat",res2.getString(1));
                Log.d("lng",res2.getString(2));
                Log.d("npm",res2.getString(3));
                Log.d("data",res2.getString(4));
            }
        }else{
            Log.d("error","pusto");
        }
    }

    public void dell(View view) {
        boolean test=database.deleteAll();
        boolean test2=database.deleteAllSp();
        boolean test3=database.deleteAllSp2();
        boolean test4=database.deleteAllSp3();
        if(test && test2 && test3) {
            Toast.makeText(getBaseContext(), "usunieto", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(),"blad" , Toast.LENGTH_LONG).show();
        }
    }

    public void facebook(MenuItem item) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Test app in android studio");

        startActivity(Intent.createChooser(shareIntent, "Share your training"));
    }

    public void twitter(MenuItem item) {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("wyslesz");
        builder.show();
    }

    public void instagram(MenuItem item) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage("com.instagram.android");
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "Test app in android studio");
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
