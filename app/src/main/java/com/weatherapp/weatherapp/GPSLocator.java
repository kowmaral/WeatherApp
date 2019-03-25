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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPSLocator {

    static final int REQ_CODE = 1000;
    private LocationRequest gpsReq;
    private LocationCallback gpsCallback;
    private double latitude, longitude;
    private MainActivity mainActiv;

    public String getCity() {
        return getAddressFromLocation().getLocality();
    }

    String getCountryCode() {
        return getAddressFromLocation().getCountryCode();
    }

    private Address getAddressFromLocation()
    {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(mainActiv, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Toast.makeText(mainActiv, "We have problem with localize your position", Toast.LENGTH_LONG).show();
            return new Address(null);
        }
        return addresses.get(0);
    }

    String findCountryCodeFromCity(String city)
    {
        if(Geocoder.isPresent()){
            try {
                Geocoder gc = new Geocoder(mainActiv);
                List<Address> addresses = gc.getFromLocationName(city, 1);
                if(addresses.isEmpty())
                {
                    return "";
                }
                gc = new Geocoder(mainActiv, new Locale("en"));
                Address address = addresses.get(0);
                if(address.hasLatitude() && address.hasLongitude()){
                    address = gc.getFromLocation(address.getLatitude(), address.getLongitude(), 1).get(0);
                    return (address.getLocality()+","+address.getCountryCode());
                }
            } catch (IOException ignored) {}
        }
        return "";
    }

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

    @SuppressLint("RestrictedApi")
    private void buildLocationReq() {

        gpsReq = new LocationRequest();
        gpsReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gpsReq.setInterval(10);//Params to gps
        gpsReq.setFastestInterval(7);
        gpsReq.setSmallestDisplacement((float) 0.000001);
    }
}
