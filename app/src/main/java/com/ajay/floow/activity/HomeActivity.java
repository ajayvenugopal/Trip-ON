package com.ajay.floow.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ajay.floow.R;
import com.ajay.floow.database.DatabaseHandler;
import com.ajay.floow.model.Journey;
import com.ajay.floow.service.MyService;
import com.ajay.floow.utils.CommonCalls;
import com.ajay.floow.utils.Constants;
import com.ajay.floow.utils.PreferencesUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleMap googleMap;


    Marker mCurrLocationMarker;
    private ArrayList<LatLng> points; //added
    Polyline line; //added

    DatabaseHandler db;
    FrameLayout mapContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mapContainer = (FrameLayout) findViewById(R.id.container);
        db = new DatabaseHandler(getApplicationContext());
        points = new ArrayList<LatLng>();
        if (PreferencesUtils.getData(Constants.tracking, getApplicationContext(), "true").equals("true")) {


            points = db.getLocations(PreferencesUtils.getData(Constants.trackingID, getApplicationContext(), "0"));

        }


        initViews();


        /*
        *
        * Broadbast for service to communicate with Activity
        * */
        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationChange, new IntentFilter("LOCATION_CHANGE")


        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                locationChange);
    }

    BroadcastReceiver locationChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String lat = intent.getStringExtra("lat");
            String log = intent.getStringExtra("log");


            LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(log));

            onLocationChanged(location);


        }
    };
/*
*
* Init VIews
*
* */
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);


        SwitchCompat trackSwitch = (SwitchCompat) hView.findViewById(R.id.tracking);

        if (PreferencesUtils.getData(Constants.tracking, getApplicationContext(), "false").equals("true")) {
            trackSwitch.setChecked(true);
        } else {
            trackSwitch.setChecked(false);
        }
        trackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {

                    PreferencesUtils.saveData(Constants.tracking, "true", getApplicationContext());


                    startJouney();

                } else {
                    PreferencesUtils.saveData(Constants.tracking, "false", getApplicationContext());


                    endJourney();
                }
            }
        });


    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    Journey journey = new Journey();
/*
*
*
* start Journey and save to local db
* */
    void startJouney() {

        Toast.makeText(this, getString(R.string.start_trip), Toast.LENGTH_SHORT).show();
        journey.setName("Trip");
        journey.setStart_time(GetTime());
        long id = db.insertJourney(journey);
        PreferencesUtils.saveData(Constants.trackingID, String.valueOf(id), getApplicationContext());
    }
/*
*
*
* end journey and update data on db
* */
    void endJourney() {

        journey.setEnd_time(GetTime());
        db.updateJourney(PreferencesUtils.getData(Constants.trackingID, getApplicationContext(), "0"), journey);


        CommonCalls.Print("data", db.getAllJourneyLIst().toString());

        stopService(new Intent(this, MyService.class));

        googleMap.addPolyline(new PolylineOptions());
        googleMap.clear();
        Toast.makeText(this, getString(R.string.end_trip), Toast.LENGTH_SHORT).show();
    }

/*
* Get Current Time
* */
    private String GetTime() {
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(presentTime_Date);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.jlist) {
            Intent jList = new Intent(getApplicationContext(), ListJourney.class);
            startActivity(jList);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted

                googleMap.setMyLocationEnabled(true);
                drawPath();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {

            googleMap.setMyLocationEnabled(true);
            drawPath();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission())

        {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                checkLocation();
                loadMap();
                startService(new Intent(this, MyService.class));


            }
        }

    }

    /*
    *
    * check location enabled or not in device
    *
    * */
    private void checkLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        if (lm.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        if (lm.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }


        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
            dialog.show();
        }
    }

    /*
    * CHECKING RUNTIME PERMISSOIN FOR LOCATION ACCESS
    *
    * */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        checkLocation();
                        Log.e("permission ", ":granted");

                        loadMap();
                    }

                } else {
                    Log.e("permission ", ":denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
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


    /*
    *
    * disable app memory usage by stopping the service if not enabled tracking
    *
    * to save battery
    *
    *
    * */
    @Override
    public void onPause() {
        super.onPause();
        if (PreferencesUtils.getData(Constants.tracking, getApplicationContext(), "false").equals("false"))
            stopService(new Intent(this, MyService.class));
    }


    public void onLocationChanged(LatLng latLng) {

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


        if (PreferencesUtils.getData(Constants.tracking, getApplicationContext(), "true").equals("true")) {

            points.add(latLng); //added
//            db.insertLocatoin(PreferencesUtils.getData(Constants.trackingID,getApplicationContext(),""),String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
            drawPath(); //added

        }
    }

    /*
    *
    * Draw path using polyline
    * */
    private void drawPath() {

        googleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }

        line = googleMap.addPolyline(options); //add Polyline
    }


}
