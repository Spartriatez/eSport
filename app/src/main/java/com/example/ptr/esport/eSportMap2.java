package com.example.ptr.esport;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.ExecutionException;

import Modules.Route;

public class eSportMap2 extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener
{
    private DismissOverlayView mDismissOverlay;
    private GoogleMap mMap;
    List<Route> routes;
    String output=null;
    EditText et1,et2;
    String URLaddress="https://maps.googleapis.com/maps/api/directions/json?";
    String key="AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A";
    JSONParse jsp=new JSONParse();
    private String type_map;
    private static String type = "basic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sport_map2);

        type_map="basic";
        et1=(EditText)findViewById(R.id.et1);
        et2=(EditText)findViewById(R.id.et2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        getMenuInflater().inflate(R.menu.e_sport_map2, menu);
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
        }else if(id==R.id.action_advanced_map){
            return true;
        }else if(id==R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void clicked(MenuItem item) {
        Intent inten=new Intent(eSportMap2.this,eSportMenu.class);
        startActivity(inten);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);
        // Add a marker in Sydney, Australia and move the camera.
        /*LatLng my_City = new LatLng(52.403439, 16.9527173);
        mMap.addMarker(new MarkerOptions().position(my_City).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(my_City));*/
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Display the dismiss overlay with a button to exit this activity.
        mDismissOverlay.show();
    }
    public void search(View view) throws JSONException {
        String origin=et1.getText().toString();
        String destination=et2.getText().toString();
        if(!origin.isEmpty() && !destination.isEmpty()) {
            origin = origin.replaceAll("\\s", "%20");
            destination = destination.replaceAll("\\s", "%20");
            String result = URLaddress + "origin=" + origin + "&destination=" + destination + "&key=" + key;

            Log.d("output", result);
            try {
                output = new checkURL().execute(result).get();
                //output=new checkURL().execute("https://maps.googleapis.com/maps/api/directions/json?origin=Poznan%25Piotrowo&destination=Poznan%25Baraniaka&key=AIzaSyDxG2Xo0PyEDGE-0R2G6y-uE6cAJpZAQ1A"
                //).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (output != null) {
                Log.d("output", output);
                jsp.checkJSON(output);
                routes = jsp.getRoutes();
                for (Route route : routes) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title(route.startAddress)
                            .position(route.startLocation));
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title(route.endAddress)
                            .position(route.endLocation));

                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.BLUE).
                            width(10);

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    mMap.addPolyline(polylineOptions);
                }
            } else {
                Toast.makeText(this,"Brak Polaczenia z siecią",Toast.LENGTH_SHORT).show();
            }
        }else{

            Toast.makeText(eSportMap2.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
        }
    }


    public void top(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void down(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void click2(MenuItem item) {
        Intent inten=new Intent(eSportMap2.this,eSportMap3.class);
        startActivity(inten);
    }

    public void click4(MenuItem item) {
        mMap.clear();
        routes.clear();
    }
    public static String getType(){
        return type;
    }

    public void save(MenuItem item) {
        if(output==null || output.isEmpty()) {
            Toast.makeText(eSportMap2.this, "Nie wyznaczono trasy", Toast.LENGTH_SHORT).show();
        }else {
            Intent inten = new Intent(eSportMap2.this, Save.class);
            inten.putExtra("route", output);
            startActivity(inten);
        }
    }
}
