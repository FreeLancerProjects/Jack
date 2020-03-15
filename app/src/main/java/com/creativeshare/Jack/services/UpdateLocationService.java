package com.creativeshare.Jack.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.creativeshare.Jack.models.LocationError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

public class UpdateLocationService extends Service implements LocationListener ,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Handler handler;
    private Runnable runnable;
    private int accuracy;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }




    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest(accuracy);
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,new LocationCallback()
                {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                }, Looper.myLooper());
    }

    private void initLocationRequest(final int accuracy) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000*5*60);
        locationRequest.setFastestInterval(1000*5);
        locationRequest.setPriority(accuracy);


        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                if (accuracy == LocationRequest.PRIORITY_HIGH_ACCURACY)
                {
                    EventBus.getDefault().post(new LocationError(0));

                }else if (accuracy == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                {
                    EventBus.getDefault().post(new LocationError(1));

                }else if ( accuracy == LocationRequest.PRIORITY_LOW_POWER)
                {
                    EventBus.getDefault().post(new LocationError(2));

                }
                handler.removeCallbacks(runnable);
            }
        };
        handler.postDelayed(runnable,5000);



    }

    @Override
    public void onConnectionSuspended(int i) {

        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("lat",location.getLatitude()+"");
        EventBus.getDefault().post(location);
        handler.removeCallbacks(runnable);
        stopSelf();

        if (googleApiClient!=null)
        {
            googleApiClient.disconnect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accuracy = intent.getIntExtra("accuracy",LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.e("accc",accuracy+"");
        initGoogleApiClient();

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        stopSelf();
        if (googleApiClient!=null)
        {
            googleApiClient.disconnect();
        }

        super.onDestroy();

    }
}
