package com.aqkassignment.com.weatherapp.controller;

import com.aqkassignment.com.weatherapp.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    public Map<String, Object> getForecast(@RequestParam String zipCode) {
        return weatherService.getForecast(zipCode);
    }
    @GetMapping("/forecast-by-address")
    public Map<String, Object> getForecastByAddress(@RequestParam String address) {
        return weatherService.getForecastByAddress(address);
    }
}


