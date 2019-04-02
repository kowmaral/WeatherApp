package com.weatherapp.weatherapp;

import android.os.Bundle;

public class MainActivityBackground extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moveTaskToBack(true);
        super.onCreate(savedInstanceState);
        finish();
    }
}
