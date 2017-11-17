package com.ajay.floow.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ajay.floow.database.DatabaseHandler;
import com.ajay.floow.utils.CommonCalls;
import com.ajay.floow.utils.Constants;
import com.ajay.floow.utils.PreferencesUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    String TAG = "GPS";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    DatabaseHandler db;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        CommonCalls.Print(TAG, "onConnected");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);

        if (PreferencesUtils.getData(Constants.tracking, getApplicationContext(), "false").equals("true"))

            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        else
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        CommonCalls.Print(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CommonCalls.Print(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {

        CommonCalls.Print(TAG, "onLocationChanged");


        if (PreferencesUtils.getData(Constants.tracking, getApplicationContext(), "true").equals("true")) {


            db.insertLocatoin(PreferencesUtils.getData(Constants.trackingID, getApplicationContext(), ""), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));


        }
        updateUI(location);
    }


    void updateUI(Location location) {

        Intent intent = new Intent("LOCATION_CHANGE");
        intent.putExtra("lat", String.valueOf(location.getLatitude()));
        intent.putExtra("log", String.valueOf(location.getLongitude()));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }


    protected synchronized void buildGoogleApiClient() {

        db = new DatabaseHandler(getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CommonCalls.Print(TAG, "onStartCommand");
//        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        CommonCalls.Print(TAG, "onCreate");
        buildGoogleApiClient();
    }


    @Override
    public void onDestroy() {
        CommonCalls.Print(TAG, "onDestroy");
//stop location updates when Activity is no longer active
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
    }


}