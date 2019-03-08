package com.weatherapp.weatherapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.weatherapp.weatherapp.GPSLocator.REQ_CODE;

public class MainActivity extends AppCompatActivity {

    TextView tv_latitude, tv_longitude;
    GPSLocator gpsLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_latitude = findViewById(R.id.latitude);
        tv_longitude = findViewById(R.id.longitude);

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

    public void onLocationChange()
    {
        tv_longitude.setText(String.valueOf(gpsLocator.getLongitude()));
        tv_latitude.setText(String.valueOf(gpsLocator.getLatitude()));
    }
}
