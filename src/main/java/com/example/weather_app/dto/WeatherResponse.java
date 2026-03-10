package com.example.weather_app.dto;

public class WeatherResponse {

    private String city;
    private String condition;
    private double temperature;
    private String region;
    private String country;
    private String icon;

    public WeatherResponse(String city, String condition, double temperature, String region, String country, String icon) {
        this.city = city;
        this.condition = condition;
        this.temperature = temperature;
        this.region = region;
        this.country = country;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public WeatherResponse() {
    }
}
