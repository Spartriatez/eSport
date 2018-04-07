package com.example.ptr.esport;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class History_Map extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener
{
    List<LatLng> list=new ArrayList<LatLng>();
    DBSqlite sql;
    private DismissOverlayView mDismissOverlay;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history__map);

        sql=new DBSqlite(this);

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;
        Cursor test = sql.isEmpty();
        int id_u=0;
        String data=null;
        if (test.getCount() != 0) {
            Cursor returnDate=sql.returnDateHistory();
            if(returnDate.getCount() != 0){
                while (returnDate.moveToNext()) {
                    id_u=returnDate.getInt(0);
                    data=returnDate.getString(1);
                }
            }
            if(id_u!=0 && data!=null){
                Cursor returnLocalization=sql.returnSaveMap(id_u,data);
                if(returnDate.getCount() != 0){
                    while (returnLocalization.moveToNext()) {
                        list.add(new LatLng(returnLocalization.getDouble(0),returnLocalization.getDouble(1)));
                    }
                }
                googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                        .title("First position")
                        .position(list.get(0)));
                googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title("lastPosition")
                        .position(list.get(list.size() - 1)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(list.size() - 1), 15.0f));
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.RED).
                        width(10);

                for (int i = 0; i < list.size(); i++)
                    polylineOptions.add(list.get(i));

                googleMap.addPolyline(polylineOptions);
            }
        }else {
            LatLng my_City = new LatLng(52.40389, 16.95483);
            googleMap.addMarker(new MarkerOptions().position(my_City).title("My Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_City, 15.0f));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    public void top(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void down(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void clicked10(View view) {
        Intent inten=new Intent(History_Map.this,history_activity.class);
        startActivity(inten);
    }
}
