package com.weatherapp.weatherapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class WeatherHttpRequester {

    private static String Weather_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String Forecast_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    private static String APPID = "&APPID=";

    private String getData(String location, String type)
    {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            con = (HttpURLConnection) ( new URL(type + location + APPID)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ( (line = br.readLine()) != null ) {
                buffer.append(line).append("rn");
            }

            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable ignored) {}
            try { con.disconnect(); } catch(Throwable ignored) {}
        }
        return null;
    }

    String getWeatherData(String location) {
        return getData(location, Weather_URL);
    }

    String getForecastData(String location) {
        return getData(location, Forecast_URL);
    }
}