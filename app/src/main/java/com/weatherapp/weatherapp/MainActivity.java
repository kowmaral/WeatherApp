package com.weatherapp.weatherapp;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.io.Console;

import io.paperdb.Paper;

import static com.weatherapp.weatherapp.GPSLocator.REQ_CODE;

public class MainActivity extends AppCompatActivity {

    TextView tv_city;
    GPSLocator gpsLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_city = findViewById(R.id.city);

        Paper.init(this);
        gpsLocator = new GPSLocator(this);


        String city = "Kraków,PL";
        AsyncWeatherRequest task = new AsyncWeatherRequest();
        task.execute(new String[]{city});

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
        //TODO: SEND TO WEATHER API
        //gpsLocator.getLongitude();
        //gpsLocator.getLatitude();
        tv_city.setText(gpsLocator.getAddress());

        Paper.book().write("City", gpsLocator.getAddress());
        Intent intent = new Intent(this, WeatherWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), WeatherWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public void switchGps(View view) {
        Switch gpsSwitch = (Switch)view;
        findViewById(R.id.citiesInput).setEnabled(!gpsSwitch.isChecked());
    }

    private class AsyncWeatherRequest extends AsyncTask<String, Void, WeatherForecast> {

        @Override
        protected WeatherForecast doInBackground(String... params) {
            WeatherForecast weather = new WeatherForecast();
            String data = ( (new WeatherHttpRequester()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }




        @Override
        protected void onPostExecute(WeatherForecast weather) {
            super.onPostExecute(weather);


            //tutaj mozesz ustawic wszystkie pola z danych z Dżesiki
            tv_city.setText(weather.toString());

        }







    }
}
