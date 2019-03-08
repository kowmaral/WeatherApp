package com.weatherapp.weatherapp;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.paperdb.Paper;

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

        Paper.init(this);
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
        tv_city.setText(gpsLocator.getAddress());

        Paper.book().write("City", gpsLocator.getAddress());
        Intent intent = new Intent(this, WeatherWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), WeatherWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
