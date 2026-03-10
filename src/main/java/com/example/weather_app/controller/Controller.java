package com.example.weather_app.controller;

import com.example.weather_app.dto.WeatherForCast;
import com.example.weather_app.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class Controller {

    @Autowired
    private WeatherService service;

//    @GetMapping("/{city}")
//    public String getWeatherData(@PathVariable String city){
//        return service.test();
//    }

    @GetMapping("/forecast/{city}")
    public WeatherForCast getForecast(@PathVariable String city, @RequestParam int days){
        return service.getForecast(city,days);
    }

}
