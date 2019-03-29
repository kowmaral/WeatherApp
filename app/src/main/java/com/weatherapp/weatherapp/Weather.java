package com.weatherapp.weatherapp;

import android.support.annotation.NonNull;

public class Weather {

    Location location;
    CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    Wind wind = new Wind();
    private Rain rain = new Rain();
    private Snow snow = new Snow()	;
    Clouds clouds = new Clouds();
    String date;

    @NonNull
    @Override
    public String toString() {
        return "Weather{" +
                "location=" + location +
                ", currentCondition=" + currentCondition +
                ", temperature=" + temperature +
                ", wind=" + wind +
                ", rain=" + rain +
                ", snow=" + snow +
                ", clouds=" + clouds +
                '}';
    }

    public  class CurrentCondition {
        private int weatherId;
        private String condition;
        private String descr;
        private String icon;


        private float pressure;
        private float humidity;

        void setWeatherId(int weatherId) {
            this.weatherId = weatherId;
        }

        void setCondition(String condition) {
            this.condition = condition;
        }

        void setDescr(String descr) {
            this.descr = descr;
        }
        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        void setPressure(float pressure) {
            this.pressure = pressure;
        }
        void setHumidity(float humidity) {
            this.humidity = humidity;
        }

        @NonNull
        @Override
        public String toString() {
            return "CurrentCondition{" +
                    "weatherId=" + weatherId +
                    ", condition='" + condition + '\'' +
                    ", descr='" + descr + '\'' +
                    ", icon='" + icon + '\'' +
                    ", pressure=" + pressure +
                    ", humidity=" + humidity +
                    '}';
        }
    }

    public  class Temperature {
        private float temp;
        private float minTemp;
        private float maxTemp;

        float getTemp() {
            return temp;
        }
        void setTemp(float temp) {
            this.temp = temp;
        }

        void setMinTemp(float minTemp) {
            this.minTemp = minTemp;
        }

        void setMaxTemp(float maxTemp) {
            this.maxTemp = maxTemp;
        }

        @NonNull
        @Override
        public String toString() {
            return "Temperature{" +
                    "temp=" + temp +
                    ", minTemp=" + minTemp +
                    ", maxTemp=" + maxTemp +
                    '}';
        }
    }

    public  class Wind {
        private float speed;
        private float deg;
        void setSpeed(float speed) {
            this.speed = speed;
        }

        @NonNull
        @Override
        public String toString() {
            return "Wind{" +
                    "speed=" + speed +
                    ", deg=" + deg +
                    '}';
        }
    }

    public  class Rain {
        private String time;
        private float amount;
        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }

        @NonNull
        @Override
        public String toString() {
            return "Rain{" +
                    "time='" + time + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    public  class Snow {
        private String time;
        private float amount;

        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }

        @NonNull
        @Override
        public String toString() {
            return "Snow{" +
                    "time='" + time + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    public  class Clouds {
        private int perc;

        void setPerc(int perc) {
            this.perc = perc;
        }

        @NonNull
        @Override
        public String toString() {
            return "Clouds{" +
                    "perc=" + perc +
                    '}';
        }
    }
}