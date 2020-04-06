package com.example.direction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.direction.DirectionHelpers.FetchURL;
import com.example.direction.DirectionHelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    private MarkerOptions place1,place2;
    Button getDirection;
    private Polyline currentPolyline;

    List<MarkerOptions> markerOptionsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDirection = findViewById(R.id.btnGetDirect);
        place1 = new MarkerOptions().position(new LatLng(21.208491,72.781436)).title("Location1");
        place2 = new MarkerOptions().position(new LatLng(21.201010,72.789989)).title("Location2");
        markerOptionsList.add(place1);
        markerOptionsList.add(place2);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchURL(MainActivity.this)
                        .execute(getURL(place1.getPosition(),place2.getPosition(),"driving"),"driving");
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(place1);
        mMap.addMarker(place2);

        showallmarker();
    }
    private void showallmarker()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions m: markerOptionsList)
        {
            builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int hieght = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,width,hieght,padding);
        mMap.animateCamera(cameraUpdate);
    }

    private String getURL(LatLng origin, LatLng destination, String DirectionMode){
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=" + DirectionMode;
        String parameter = str_origin + "&" + str_dest + "&" + mode;
        String format = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?" + parameter + "&key=ENTER_YOUR_KEY_HERE";
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
        {
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions)values[0]);
    }
}
