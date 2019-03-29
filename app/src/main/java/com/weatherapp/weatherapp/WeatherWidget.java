package com.weatherapp.weatherapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;

import io.paperdb.Paper;

public class WeatherWidget extends AppWidgetProvider {


    private static final String WidgetLayout = "widgetLayout";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Paper.init(context);

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, WeatherWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            remoteViews.setTextViewText(R.id.appwidget_city, (CharSequence) Paper.book().read("City"));
            remoteViews.setTextViewText(R.id.appwidget_temp,Paper.book().read("Temperature") + "°C");
            Resources res = context.getResources();
            String mDrawableName =  Paper.book().read("Icon");
            int resID = res.getIdentifier(mDrawableName , "drawable", context.getPackageName());
            remoteViews.setImageViewResource(R.id.appwidget_icon, resID);

            remoteViews.setOnClickPendingIntent(R.id.widgetLayout, getPendingSelfIntent(context, WidgetLayout));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);//add this line
        if (WidgetLayout.equals(intent.getAction())) {
            //Intent myIntent = new Intent(context, MainActivity.class);
            //context.startActivity(myIntent);

            AsyncWeatherRequest request = new AsyncWeatherRequest(context, intent);
            request.execute(Paper.book().read("City").toString());

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncWeatherRequest extends AsyncTask<String, Void, Weather> {

        private Context context;
        private Intent intent;
        public AsyncWeatherRequest(Context context, Intent intent) {
            super();
            this.context = context;
            this.intent = intent;
        }

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

            String mDrawableName = "i" + weather.currentCondition.getIcon();

            Paper.book().write("City", weather.location.getCity());
            Paper.book().write("Icon", mDrawableName);
            Paper.book().write("Temperature", String.valueOf((int)(weather.temperature.getTemp())-273));


            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);



        }
    }
}

