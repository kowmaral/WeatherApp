package com.weatherapp.weatherapp;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
    private LinearLayout forecastLayout;
    private final int LINEAR_LAYOUT_ID = 66;
    private final int DATE_TEXT_VIEW_ID = 1;
    private final int ICON_IMAGE_VIEW_ID = 2;
    private final int TEMP_TEXT_VIEW_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsSwitch = findViewById(R.id.switch1);
        et_location = findViewById(R.id.citiesInput);
        tv_city = findViewById(R.id.city);
        weatherData = findViewById(R.id.temperature);
        icon = findViewById(R.id.weatherIcon);
        forecastLayout = findViewById(R.id.forecastLayout);
        pickForecastTime(findViewById(R.id.radioButton1));

        Paper.init(this);
        gpsLocator = new GPSLocator(this);
    }

    @SuppressLint("ResourceType")
    private void buildOneDayView(float layoutWeight, int numberOfDay)
    {
        CardView cv = new CardView(this);
        cv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                layoutWeight
        ));
        cv.setCardBackgroundColor(Color.TRANSPARENT);
        cv.setId(numberOfDay);
        forecastLayout.addView(cv);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setId(LINEAR_LAYOUT_ID);
        cv.addView(ll);

        TextView date = new TextView(this);
        date.setText("");
        date.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        date.setGravity(Gravity.CENTER);
        date.setId(DATE_TEXT_VIEW_ID);
        ll.addView(date);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.i01d);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2));
        imageView.setId(ICON_IMAGE_VIEW_ID);
        ll.addView(imageView);

        TextView temperature = new TextView(this);
        temperature.setText("");
        temperature.setId(TEMP_TEXT_VIEW_ID);
        temperature.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        temperature.setGravity(Gravity.CENTER);
        ll.addView(temperature);
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
        if(position.equals(""))
        {
            return;
        }
        tv_city.setText(position);

        AsyncWeatherRequest task = new AsyncWeatherRequest();
        task.execute(position);
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
        et_location.setEnabled(!gpsSwitch.isChecked());
        if(gpsSwitch.isChecked())
        {
            onLocationChange();
        }
    }

    public void checkLocationAndCondSave(View view) {
        et_location.setText(gpsLocator.findCountryCodeFromCity(String.valueOf(et_location.getText())));
        if(String.valueOf(et_location.getText()).equals("")) {
            et_location.setFocusable(true);
            Toast.makeText(this, "Nie można zlokalizować podanego miasta", Toast.LENGTH_LONG).show();
        }
        else
        {
            onLocationChange();
        }
    }

    public void pickForecastTime(View view) {

        float MAX_NUMBER_OF_DAYS_TO_FORECAST = 5.0f;
        forecastLayout.removeAllViews();
        int numOfDays = Integer.parseInt(((RadioButton)view).getText().toString());
        for(int i=1 ; i< numOfDays+1; ++i)
        {
            buildOneDayView(MAX_NUMBER_OF_DAYS_TO_FORECAST/numOfDays, i);
        }
        //TODO: fill forecast from api(applyForecastData and getActuallyPosition) example below
        applyForecastData(1,"24.03.2019", R.drawable.i04d, "32C");
    }

    @SuppressLint("ResourceType")
    private void applyForecastData(int numOfDay, String date, @DrawableRes int icon, String temp)
    {
        LinearLayout ll = forecastLayout.findViewById(numOfDay).findViewById(LINEAR_LAYOUT_ID);
        ((TextView)ll.findViewById(DATE_TEXT_VIEW_ID)).setText(date);
        ((ImageView)ll.findViewById(ICON_IMAGE_VIEW_ID)).setImageResource(icon);
        ((TextView)ll.findViewById(TEMP_TEXT_VIEW_ID)).setText(temp);
    }

    public void refreshWeather(View view) {
        onLocationChange();
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

            Paper.book().write("City", weather.location.getCity());
            Paper.book().write("Icon", mDrawableName);
            Paper.book().write("Temperature", String.valueOf((int)(weather.temperature.getTemp() - 273)));
            refreshWidgets();
        }
    }
}