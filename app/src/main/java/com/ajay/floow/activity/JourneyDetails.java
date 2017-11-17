package com.ajay.floow.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ajay.floow.R;
import com.ajay.floow.database.DatabaseHandler;
import com.ajay.floow.model.Journey;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class JourneyDetails extends AppCompatActivity implements OnMapReadyCallback {


    TextView startTime,endTime,name;
    DatabaseHandler db;
    private ArrayList<LatLng> points; //added
    Polyline line; //added
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_details);
        getSupportActionBar().setTitle("Trip Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        loadMap();
        db = new DatabaseHandler(getApplicationContext());

       Journey journey= db.getJourney(getIntent().getStringExtra("id"));

        startTime.setText("Start Time: "+journey.start_time);

        endTime.setText("End  Time: "+journey.end_time);
        name.setText(journey.jname);


    }

    void initView(){
        startTime =(TextView)findViewById(R.id.startTime);
        endTime =(TextView)findViewById(R.id.endTime);
        name =(TextView)findViewById(R.id.title);
    }
    /*
    *
    * show  Map
    *
    * */
    private void loadMap() {

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


         /*
   *
   * Draw path using polyline
   * */

points = db.getLocations(getIntent().getStringExtra("id"));


            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int i = 0; i < points.size(); i++) {
                LatLng point = points.get(i);
                options.add(point);
            }

            line = googleMap.addPolyline(options); //add Polyline
        if (points.size()>1)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(1), 15));
    }



}
