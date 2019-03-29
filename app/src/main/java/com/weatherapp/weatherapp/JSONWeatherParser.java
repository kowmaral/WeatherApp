package com.weatherapp.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

class JSONWeatherParser {

    static Weather getWeather(String data) throws JSONException  {
        Weather weather = new Weather();
        JSONObject jObj = new JSONObject(data);
        Location loc = new Location();

        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setSunrise(getInt("sunrise", sysObj));
        loc.setSunset(getInt("sunset", sysObj));
        loc.setCity(getString("name", jObj));
        weather.location = loc;

        JSONArray jArr = jObj.getJSONArray("weather");

        JSONObject JSONWeather = jArr.getJSONObject(0);
        weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
        weather.currentCondition.setDescr(getString("description", JSONWeather));
        weather.currentCondition.setCondition(getString("main", JSONWeather));
        weather.currentCondition.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        weather.currentCondition.setHumidity(getInt("humidity", mainObj));
        weather.currentCondition.setPressure(getInt("pressure", mainObj));
        weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weather.temperature.setTemp(getFloat("temp", mainObj));

        JSONObject wObj = getObject("wind", jObj);
        weather.wind.setSpeed(getFloat("speed", wObj));


        JSONObject cObj = getObject("clouds", jObj);
        weather.clouds.setPerc(getInt("all", cObj));


        return weather;
    }
    static List<Weather> getForecast(String data) throws JSONException {


        List<Weather> forecast = new ArrayList<>();

        JSONObject jObj = new JSONObject(data);

        JSONArray listArray = getArray("list", jObj);


        for (int i=0; i < listArray.length(); i++) {
            JSONObject arrayObj = listArray.getJSONObject(i);

            Weather weather = new Weather();

            weather.date = getString("dt_txt", arrayObj);

            JSONObject main = getObject("main", arrayObj);

            weather.currentCondition.setPressure(getFloat("pressure", main));
            weather.currentCondition.setHumidity(getFloat("humidity", main));
            weather.temperature.setMaxTemp(getFloat("temp_max", main));
            weather.temperature.setMinTemp(getFloat("temp_min", main));
            weather.temperature.setTemp(getFloat("temp", main));

            JSONArray jArr = arrayObj.getJSONArray("weather");
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
            weather.currentCondition.setDescr(getString("description", JSONWeather));
            weather.currentCondition.setCondition(getString("main", JSONWeather));
            weather.currentCondition.setIcon(getString("icon", JSONWeather));

            JSONObject wObj = getObject("wind", arrayObj);
            weather.wind.setSpeed(getFloat("speed", wObj));


            JSONObject cObj = getObject("clouds", arrayObj);
            weather.clouds.setPerc(getInt("all", cObj));

            forecast.add(weather);

        }


        return forecast;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        return jObj.getJSONObject(tagName);
    }
    
    private static JSONArray getArray(String tagName, JSONObject jObj) throws JSONException{
        return jObj.getJSONArray(tagName);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}