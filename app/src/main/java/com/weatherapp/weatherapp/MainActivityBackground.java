package com.weatherapp.weatherapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class MainActivityBackground extends MainActivity {

    public static long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moveTaskToBack(true);
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Widget Update", Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() - time > 1800000)
                {
                    time = System.currentTimeMillis();
                }
            }
        }, 5000);
        finish();
    }
}
