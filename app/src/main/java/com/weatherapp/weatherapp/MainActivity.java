package com.weatherapp.weatherapp;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import io.paperdb.Paper;

import static com.weatherapp.weatherapp.GPSLocator.REQ_CODE;

public class MainActivity extends AppCompatActivity {

    private TextView tv_city, weatherData;
    private Switch gpsSwitch;
    private EditText et_location;
    private GPSLocator gpsLocator;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsSwitch = findViewById(R.id.switch1);
        et_location = findViewById(R.id.citiesInput);
        tv_city = findViewById(R.id.city);
        weatherData = findViewById(R.id.temperature);
        icon = findViewById(R.id.weatherIcon);

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

    private String getActuallyPosition()
    {
        if(!gpsSwitch.isChecked())
        {
            return String.valueOf(et_location.getText());
        }
        return (gpsLocator.getCity() + "," + gpsLocator.getCountryCode());
    }

    @SuppressLint("SetTextI18n")
    public void onLocationChange()
    {
        String position = getActuallyPosition();
        tv_city.setText(position);

        AsyncWeatherRequest task = new AsyncWeatherRequest();
        task.execute(position);

        Paper.book().write("City", position);
        refreshWidgets();
    }

    private void refreshWidgets()
    {
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

    public void checkLocationAndCondSave(View view) {
        et_location.setText(gpsLocator.findCountryCodeFromCity(String.valueOf(et_location.getText())));
        if(et_location.getText().equals("")) {
            et_location.setFocusable(true);
            Toast.makeText(this, "Nie można zlokalizować podanego miasta", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(WeatherForecast weather) {
            super.onPostExecute(weather);
            tv_city.setText(weather.location.getCity() + " ");
            weatherData.setText((int) (weather.temperature.getTemp() -273) + "°C");

            Resources res = getResources();
            String mDrawableName = "i" + weather.currentCondition.getIcon();
            int resID = res.getIdentifier(mDrawableName , "drawable", getPackageName());
            icon.setImageResource(resID);


        }
    }
}