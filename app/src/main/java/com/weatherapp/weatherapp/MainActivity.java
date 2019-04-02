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
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import org.json.JSONException;
import java.util.List;
import io.paperdb.Paper;
import static com.weatherapp.weatherapp.GPSLocator.REQ_CODE;

public class MainActivity extends AppCompatActivity {

    private TextView tv_city, weatherData;
    private Switch gpsSwitch;
    private EditText et_location;
    private GPSLocator gpsLocator;
    private ImageView icon;
    private LinearLayout forecastLayout;
    private int LINEAR_LAYOUT_ID = 66;
    private final int DATE_TEXT_VIEW_ID = 123456;
    private final int ICON_IMAGE_VIEW_ID = 234567;
    private final int TEMP_TEXT_VIEW_ID = 345678;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastLayout = findViewById(R.id.forecastLayout);

        gpsLocator = new GPSLocator(this);
        Paper.init(this);

        gpsSwitch = findViewById(R.id.switch1);
        et_location = findViewById(R.id.citiesInput);
        tv_city = findViewById(R.id.city);
        weatherData = findViewById(R.id.temperature);
        icon = findViewById(R.id.weatherIcon);

        ((AdView)findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());
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
        imageView.setImageResource(R.drawable.cast_mini_controller_progress_drawable);
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
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        System.exit(-1);
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

        AsyncWeatherRequest task = new AsyncWeatherRequest();
        task.execute(position);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        pickForecastTime(radioButton);
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
        String position = getActuallyPosition();
        if(position.equals(""))
        {
            return;
        }
        AsyncForecastRequest task = new AsyncForecastRequest(numOfDays);
        task.execute(position);
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    private void applyForecastData(int numOfDay, String date, @DrawableRes int icon, float temp)
    {
        LinearLayout ll = forecastLayout.findViewById(numOfDay).findViewById(LINEAR_LAYOUT_ID);
        ((TextView)ll.findViewById(DATE_TEXT_VIEW_ID)).setText(date);
        ((ImageView)ll.findViewById(ICON_IMAGE_VIEW_ID)).setImageResource(icon);
        ((TextView)ll.findViewById(TEMP_TEXT_VIEW_ID)).setText(String.valueOf((int) temp) + "°C");
    }

    public void refreshWeather(View view) {
        onLocationChange();
    }

    private int convertKelvin2Celsius(double kelvinVal)
    {
        return ((int)kelvinVal - 273);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncForecastRequest extends AsyncTask<String, Void, List<Weather>> {

        private final String HOUR_TO_TAKE_FORECAST_PARAMS = "12:00:00";
        private int numOfForecastDay;

        AsyncForecastRequest(int numOfDay)
        {
            super();
            numOfForecastDay = numOfDay;
        }

        @Override
        protected List<Weather> doInBackground(String... params) {
            List<Weather> weather;
            String data = (new WeatherHttpRequester()).getForecastData(params[0]);
            if(data == null)
            {
                return null;
            }
            try {
                weather = JSONWeatherParser.getForecast(data);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return weather;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(List<Weather> weatherList) {
            if(weatherList == null)
            {
                return;
            }
            super.onPostExecute(weatherList);

            for(int i=weatherList.size()-1; i >= 0; --i)
            {
                Weather weather = weatherList.get(i);
                if(!weather.date.split(" ")[1].equals(HOUR_TO_TAKE_FORECAST_PARAMS))
                {
                    weatherList.remove(weather);
                }
            }
            Resources res = getResources();
            for(int i = 0; i< numOfForecastDay; ++i)
            {
                Weather weather = weatherList.get(i);
                String mDrawableName = "i" + weather.currentCondition.getIcon();
                int resID = res.getIdentifier(mDrawableName , "drawable", getPackageName());
                applyForecastData(
                        i + 1,
                        weather.date.split(" ")[0],
                        resID,
                        convertKelvin2Celsius(weather.temperature.getTemp()));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncWeatherRequest extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather;
            String data = (new WeatherHttpRequester()).getWeatherData(params[0]);
            if(data == null)
            {
                return null;
            }
            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return weather;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Weather weather) {
            if(weather == null)
            {
                return;
            }
            super.onPostExecute(weather);
            tv_city.setText(weather.location.getCity() + " ");
            weatherData.setText(convertKelvin2Celsius(weather.temperature.getTemp()) + "°C");

            Resources res = getResources();
            String mDrawableName = "i" + weather.currentCondition.getIcon();
            int resID = res.getIdentifier(mDrawableName , "drawable", getPackageName());
            icon.setImageResource(resID);

            Paper.book().write("City", weather.location.getCity());
            Paper.book().write("Icon", mDrawableName);
            Paper.book().write("Temperature", String.valueOf(convertKelvin2Celsius(weather.temperature.getTemp())));
            refreshWidgets();
        }
    }
}