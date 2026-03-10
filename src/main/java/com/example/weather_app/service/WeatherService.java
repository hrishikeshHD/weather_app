package com.example.weather_app.service;

import com.example.weather_app.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final RestTemplate template = new RestTemplate();

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.forcast.url}")
    private String apiForecast;

    public WeatherResponse getData(String city) {
        String url = apiUrl + "?key=" + apiKey + "&q=" + city;
        Root response = callWeatherApi(url);

        if (response == null || response.getLocation() == null || response.getCurrent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid weather provider response");
        }

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setCity(response.getLocation().getName());
        weatherResponse.setRegion(response.getLocation().getRegion());
        weatherResponse.setCountry(response.getLocation().getCountry());
        weatherResponse.setTemperature(response.getCurrent().getTemp_c());

        Condition condition = response.getCurrent().getCondition();
        weatherResponse.setCondition(condition != null ? condition.getText() : "N/A");
        weatherResponse.setIcon(condition != null ? normalizeIcon(condition.getIcon()) : null);

        return weatherResponse;
    }

    private String normalizeIcon(String icon) {
        if (icon == null || icon.isBlank()) {
            return null;
        }
        if (icon.startsWith("http://") || icon.startsWith("https://")) {
            return icon;
        }
        if (icon.startsWith("//")) {
            return "https:" + icon;
        }
        return "https://" + icon;
    }

    public WeatherForCast getForecast(String city, int days) {
        WeatherForCast foreCast = new WeatherForCast();
        WeatherResponse weatherResponse = getData(city);
        List<DayTemp> dayTempList = new ArrayList<>();

        String url = apiForecast + "?key=" + apiKey + "&q=" + city + "&days=" + days;
        Root apiresponse = callWeatherApi(url);

        if (apiresponse != null
                && apiresponse.getForecast() != null
                && apiresponse.getForecast().getForecastday() != null) {

            for (ForecastDay myforecastDay : apiresponse.getForecast().getForecastday()) {
                if (myforecastDay == null || myforecastDay.getDay() == null) {
                    continue;
                }

                DayTemp dayTemp = new DayTemp();
                dayTemp.setDate(myforecastDay.getDate());
                dayTemp.setMinTemp(myforecastDay.getDay().getMintemp_c());
                dayTemp.setAvgTemp(myforecastDay.getDay().getAvgtemp_c());
                dayTemp.setMaxTemp(myforecastDay.getDay().getMaxtemp_c());
                dayTempList.add(dayTemp);
            }
        }

        foreCast.setWeatherResponse(weatherResponse);
        foreCast.setDayTemp(dayTempList);
        return foreCast;
    }

    private Root callWeatherApi(String url) {
        try {
            return template.getForObject(url, Root.class);
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Weather provider error: " + ex.getStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Weather provider timeout", ex);
        }
    }
}
