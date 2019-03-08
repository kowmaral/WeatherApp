package com.weatherapp.weatherapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.weatherapp.weatherapp.GPSLocator.REQ_CODE;

public class MainActivity extends AppCompatActivity {

    TextView tv_latitude, tv_longitude, tv_city;
    GPSLocator gpsLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_latitude = findViewById(R.id.latitude);
        tv_longitude = findViewById(R.id.longitude);
        tv_city = findViewById(R.id.city);

        gpsLocator = new GPSLocator(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    }
                }
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void onLocationChange()
    {
        tv_longitude.setText(String.valueOf(gpsLocator.getLongitude()));
        tv_latitude.setText(String.valueOf(gpsLocator.getLatitude()));

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(gpsLocator.getLatitude(), gpsLocator.getLongitude(), 1);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "We have problem with localize your position", Toast.LENGTH_LONG).show();
            return;
        }
        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        tv_city.setText(city+", "+address.split(",")[0]);
    }
}
