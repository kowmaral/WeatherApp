package com.weatherapp.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.RemoteViews;
import io.paperdb.Paper;

public class WeatherWidget extends AppWidgetProvider {

    private static final String WidgetLayout = "widgetLayout";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Paper.init(context);

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

        super.onReceive(context, intent);
        if (WidgetLayout.equals(intent.getAction())) {
            Intent myIntent = new Intent(context, MainActivity.class);
            context.startActivity(myIntent);

            //TODO: it should be at some timer
//            Intent myIntent = new Intent(context, MainActivityBackground.class);
//            context.startActivity(myIntent);
        }
    }
}

