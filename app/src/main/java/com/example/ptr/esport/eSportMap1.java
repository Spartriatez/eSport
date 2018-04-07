package com.example.ptr.esport;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.wearable.view.DismissOverlayView;
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
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Modules.Route;

public class eSportMap1 extends AppCompatActivity  implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener{
    boolean first=true;
    private DismissOverlayView mDismissOverlay;
    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver;
    List<LatLng> list=new ArrayList<LatLng>();
    private TextView textView;
    JSONParse jsp;
    List<Route> routes;
    int signals;
    DBSqlite database;
    String filename=null;
    String type=null;
    String temp;
    int length;
    LatLng firstPoint=null;
    ContainerMap mService;
    boolean mBound = false;
    private BroadcastReceiver broadcastReceiver2;
    String o1,o2,o3;
    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Cursor res = database.loadLocalization();
                    Log.d("dane", String.valueOf(res.getCount()));
                    if (res.getCount() != 0) {
                        StringBuffer buffer = new StringBuffer();
                        list.clear();
                        buffer.append(res);
                        while (res.moveToNext()) {

                            list.add(new LatLng(res.getDouble(0), res.getDouble(1)));
                        }

                        mMap.clear();
                        allMethod();
                        if (signals > 1) {
                            if (o1 != null && o2 != null && o3 != null)
                                getObjectsSports();
                        }
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .title("First position")
                                .position(list.get(0)));
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .title("lastPosition")
                                .position(list.get(list.size() - 1)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(list.size() - 1), 15.0f));
                        PolylineOptions polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.RED).
                                width(10);

                        for (int i = 0; i < list.size(); i++)
                            polylineOptions.add(list.get(i));

                        mMap.addPolyline(polylineOptions);
                    }else{
                        double p1=intent.getDoubleExtra("param1",0);
                        double p2=intent.getDoubleExtra("param2",0);
                        if(p1!=0 && p2!=0){
                            LatLng sydney = new LatLng(p1, p2);
                            mMap.clear();
                            allMethod();
                            if (signals > 1) {
                                if (o1 != null && o2 != null && o3 != null)
                                    getObjectsSports();
                            }
                            mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        }
                    }
                }
        };
    }
    registerReceiver(broadcastReceiver,new IntentFilter("loc"));
        /*
        }else {

        }*/
        if(broadcastReceiver2 == null) {
            broadcastReceiver2 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    signals=intent.getIntExtra("sygn",0);
                    Log.d("sygn", String.valueOf(signals));
                    if(signals>1) {
                        o1 = intent.getExtras().get("out1").toString();
                        o2 = intent.getExtras().get("out2").toString();
                        o3 = intent.getExtras().get("out3").toString();
                        if (o1 != null && o2 != null && o3 != null)
                            getObjectsSports();
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver2,new IntentFilter("maps"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sport_map1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jsp=new JSONParse();
        database=new DBSqlite(this);
        final FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Set the system view insets on the containers when they become available.
        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Call through to super implementation and apply insets
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                // Add Wearable insets to FrameLayout container holding map as margins
                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        boolean test=database.isloaded();
        if(test){
            Cursor cur=database.getLoadFile();
            while (cur.moveToNext()){
                filename=cur.getString(0);
                type=cur.getString(1);
                temp=cur.getString(2);
            }
            switch(temp){
                case "100": length=100;
                    break;
                case "200": length=200;
                    break;
                case "300": length=300;
                    break;
                case "500": length=500;
                    break;
                default: length=0;
            }
            try {
                String dir=getCacheDir().getCanonicalPath();
                String res=dir+"/"+type+"_map";
                File f = new File(res, filename+".json");
                String contents = new Scanner(f).useDelimiter("\\A").next();
                jsp.checkJSON(contents);
                routes=jsp.getRoutes();
                Log.d("dane", contents);
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        mBound = true;
        Intent intent = new Intent(this, ContainerMap.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.e_sport_map1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void clicked(MenuItem item) {
        Intent inten=new Intent(eSportMap1.this,eSportMenu.class);
        startActivity(inten);
    }
    public void clicked10(View view) {
        Intent inten=new Intent(eSportMap1.this,eSportMenu.class);
        startActivity(inten);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
    double sum=0.0,sum2=0.0;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);

        allMethod();
    }
    public void top(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void down(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
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
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);

        return Radius * c;
    }

    public void show(MenuItem item) {
        String url = "https://maps.googleapis.com/maps/api/place/search/json?types=stadium&rankby=distance&location=52.4009821%2C16.9455809&sensor=false&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A";
        String url2 = "https://maps.googleapis.com/maps/api/place/search/json?types=park&rankby=distance&location=52.4009821%2C16.9455809&sensor=false&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A";
        String url3 = "https://maps.googleapis.com/maps/api/place/search/json?types=gym&rankby=distance&location=52.4009821%2C16.9455809&sensor=false&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A";

        String out = null;
        String out2 = null;
        String out3 = null;
        try {
            out = new checkURL().execute(url).get();
            out2 = new checkURL().execute(url2).get();
            out3 = new checkURL().execute(url3).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mBound) {
            mService.LoadDate(out,out2,out3,2);
        }
    }
    public void delete(MenuItem item) {
        mMap.clear();
        mService.DeleteDate(1);
        o1=o2=o3=null;
        allMethod();
        if(list!=null & list.size()>0){
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("First position")
                    .position(list.get(0)));
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("lastPosition")
                    .position(list.get(list.size()-1)));
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(10);

            for (int i = 0; i < list.size(); i++)
                polylineOptions.add(list.get(i));

            mMap.addPolyline(polylineOptions);
        }
    }
    public void allMethod() {
        if (routes != null && length == 0) {

            for (Route route : routes) {
                firstPoint = route.startLocation;
                //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(route.startAddress)
                        .position(route.startLocation));

                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(route.endAddress)
                        .position(route.points.get(route.points.size() - 1)));
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);
                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i));
                }
                mMap.addPolyline(polylineOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15.0f));
            }
        } else if (routes != null && length > 0) {

            for (Route route : routes) {
                firstPoint = route.startLocation;
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(route.startAddress)
                        .position(route.startLocation));

                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(route.endAddress)
                        .position(route.points.get(route.points.size() - 1)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);
                double tempsum = 0;
                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i));

                    if (i > 0) {
                        double distance = CalculationByDistance(route.points.get(i - 1), route.points.get(i));
                        sum += distance;
                    }
                }
                for (int i = 0; i < route.points.size(); i++) {
                    if (i > 0) {
                        double distance = CalculationByDistance(route.points.get(i - 1), route.points.get(i));
                        sum2 += distance;
                        tempsum += distance;
                        if (tempsum > 0.5 && (sum2 < sum - 0.5)) {
                            tempsum = 0.0;
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                    .title(route.endAddress)
                                    .position(route.points.get(i)));
                        }
                    }
                }
                mMap.addPolyline(polylineOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15.0f));
            }
            ;
        }

    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ContainerMap.LocalBinder binder = (ContainerMap.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    public void getObjectsSports(){
        List<HashMap<String, String>> places = jsp.parse2(o1);
        List<HashMap<String, String>> places2 = jsp.parse2(o2);
        List<HashMap<String, String>> places3 = jsp.parse2(o3);
        Log.d("Dane", String.valueOf(places));
        if (places != null && places2 != null && places != null) {
            for (int i = 0; i < places.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = places.get(i);

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.s));

                mMap.addMarker(markerOptions);
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
               // mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
            for (int i = 0; i < places2.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = places2.get(i);

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.p));

                mMap.addMarker(markerOptions);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
            for (int i = 0; i < places3.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = places3.get(i);

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.g));

                mMap.addMarker(markerOptions);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
               // mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
        }else{
            Toast.makeText(this, "ERROR: Błąd ładowania", Toast.LENGTH_SHORT).show();
        }
    }

    public void weigth(MenuItem item) {
        Intent inten=new Intent(eSportMap1.this,Weigth.class);
        startActivity(inten);
    }
}
