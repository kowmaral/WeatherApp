package com.weatherapp.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSLocator {

    static final int REQ_CODE = 1000;
    private LocationRequest gpsReq;
    private LocationCallback gpsCallback;
    private double latitude, longitude;
    private MainActivity mainActiv;

    GPSLocator(AppCompatActivity activity) {
        mainActiv = (MainActivity) activity;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
        } else {
            buildLocationReq();
            buildLocationCallback();
            FusedLocationProviderClient fusedGpsProviderClient = LocationServices.getFusedLocationProviderClient(activity);

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
                return;
            }
            fusedGpsProviderClient.requestLocationUpdates(gpsReq, gpsCallback, Looper.myLooper());
        }
    }

    private void buildLocationCallback() {
        gpsCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mainActiv.onLocationChange();
                }
            }
        };
    }

    public String getAddress()
    {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(mainActiv, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Toast.makeText(mainActiv, "We have problem with localize your position", Toast.LENGTH_LONG).show();
            return "";
        }
        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        return (city+", "+address.split(",")[0]);
    }

    @SuppressLint("RestrictedApi")
    private void buildLocationReq() {

        gpsReq = new LocationRequest();
        gpsReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gpsReq.setInterval(10);//Params to gps
        gpsReq.setFastestInterval(7);
        gpsReq.setSmallestDisplacement((float) 0.000001);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
