package com.example.ptr.esport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class eSportMap3 extends AppCompatActivity implements OnMapReadyCallback {
    private DismissOverlayView mDismissOverlay;
    private GoogleMap mMap;
    String output = null;
    EditText et1;
    List<LatLng> markerPoint;
    JSONParse jsp = new JSONParse();
    private static String type = "advantage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sport_map3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        markerPoint = new ArrayList<LatLng>();
        et1 = (EditText) findViewById(R.id.et11);
        Button bt = (Button) findViewById(R.id.draw);
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

        // Obtain the DismissOverlayView and display the introductory help text.
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.intro_text);
        mDismissOverlay.showIntroIfNecessary();

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.e_sport_map3, menu);
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
        } else if (id == R.id.action_basic_map) {
            return true;
        } else if (id == R.id.action_save2) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void click11(MenuItem item) {
        Intent inten = new Intent(eSportMap3.this, eSportMenu.class);
        startActivity(inten);
    }

    public void click22(MenuItem item) {
        Intent inten = new Intent(eSportMap3.this, eSportMap2.class);
        startActivity(inten);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set the long click listener as a way to exit the map.
        example();
    }

    public void click44(MenuItem item) {
        mMap.clear();
    }

    private String getDirectionsUrl() {
        // Origin of route
        LatLng origin = markerPoint.get(0);
        LatLng destination = null;
        //= markerPoint.get(markerPoint.size()-1);
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        //
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        for (int i = 1; i < markerPoint.size(); i++) {
            LatLng point = (LatLng) markerPoint.get(i);
            if (i ==2) {
                waypoints = "waypoints=";
                waypoints += point.latitude + "," + point.longitude;
            }else if(i==markerPoint.size()-1){
                destination=markerPoint.get(i);
            }else{
                waypoints += "|"+point.latitude + "," + point.longitude;
            }
        }
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&mode=walking&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A";

        return url;
    }

    public void searching(View view) throws IOException {
        String name = et1.getText().toString();
        name=name.replaceAll("\\s", "%20");
        if(!name.isEmpty()) {
            String URLaddress = "http://maps.google.com/maps/api/geocode/json?address=" + name + "&sensor=false";
            String output2 = null;
            try {
                output2 = new checkURL().execute(URLaddress).get();
                //output=new checkURL().execute("https://maps.googleapis.com/maps/api/directions/json?origin=Poznan%25Piotrowo&destination=Poznan%25Baraniaka&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A"
                //).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (output2 != null) {
                Log.d("output", output2);
                JSONObject jsonObject = new JSONObject();
                LatLng my_City;
                try {
                    jsonObject = new JSONObject(output2);

                    double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");

                    my_City = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(my_City).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_City, 15.0f));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,"Brak Polaczenia z siecią",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(eSportMap3.this, "Nie podano miasta", Toast.LENGTH_SHORT).show();
        }
    }

    public void example() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                if (markerPoint.size() >= 20) {
                    return;
                } else {
                    markerPoint.add(latLng);
                    MarkerOptions options = new MarkerOptions();
                    for(int i=0;i<markerPoint.size();i++) {

                    options.position(markerPoint.get(i));



                    if (i == 0) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else  if(i==markerPoint.size()-1)
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    else
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                        mMap.addMarker(options);
                    }

                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                markerPoint.clear();
            }
        });
    }

    public void top(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void down(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void drawclick(View view) {
        if(markerPoint.size()>=2) {
            List<List<HashMap<String, String>>> routes = null;
            if (markerPoint.size() >= 1) {

                String result = getDirectionsUrl();
                Log.d("URL", result);
                try {
                    output = new checkURL().execute(result).get();
                    //output=new checkURL().execute("https://maps.googleapis.com/maps/api/directions/json?origin=Poznan%25Piotrowo&destination=Poznan%25Baraniaka&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A"
                    //).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Log.d("output", output);
                JSONObject jObject;
                try {
                    jObject = new JSONObject(output);
                    routes = jsp.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("json", String.valueOf(routes));
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.RED);
                }

                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
        }else{
            Toast.makeText(eSportMap3.this, "Nie wyznaczono punktow", Toast.LENGTH_SHORT).show();
        }
    }

    public void save(MenuItem item) {
        if(output==null) {
            Toast.makeText(eSportMap3.this, "Nie wyznaczono trasy", Toast.LENGTH_SHORT).show();
        }else {
            Intent inten = new Intent(eSportMap3.this, Save.class);
            inten.putExtra("route2",output);
            startActivity(inten);
        }
    }
    public static String getType(){
        return type;
    }
}
