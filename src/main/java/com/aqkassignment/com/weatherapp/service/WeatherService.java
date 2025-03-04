package com.aqkassignment.com.weatherapp.service;

import com.aqkassignment.com.weatherapp.service.util.ResponseConverter;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aqkassignment.com.weatherapp.service.util.ZipCodeExtractor.extractZipCode;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Map<String, Object>> initialCache = new HashMap<>();

    @PostConstruct
    public void initializeCache() {
        initialCache.put("10001", Map.of("temperature", 22.0, "high", 28.0, "low", 18.0, "cached", true));
        initialCache.put("90210", Map.of("temperature", 25.0, "high", 30.0, "low", 20.0, "cached", false));
        initialCache.put("30301", Map.of("temperature", 27.0, "high", 33.0, "low", 23.0, "cached", true));
        initialCache.put("60601", Map.of("temperature", 18.0, "high", 22.0, "low", 14.0, "cached", true));
        initialCache.put("33101", Map.of("temperature", 30.0, "high", 35.0, "low", 28.0, "cached", true));
        initialCache.put("54321", Map.of("temperature", 36.0, "high", 37.0, "low", 22.0, "cached", true));

    }

    @Cacheable(value = "forecast", key = "#zipCode", unless = "#result == null")
    public Map<String, Object> getForecast(String zipCode) {
        if (initialCache.containsKey(zipCode)) {
            return initialCache.get(zipCode);
        }

        String apiUrl = "EXTERNAL_WEB_SERVICE" + zipCode + "&days=1";
        if ( ! apiUrl.contains("EXTERNAL_WEB_SERVICE")) {
            Map<String, Object> forecast = new HashMap<>();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                // Process the response body
                System.out.println("Response Body: " + responseBody);
                HashMap<String, Object> responseForecast = ResponseConverter.convert(response);

                forecast.put("temperature", ((Map<String, Object>) responseForecast.get("current")).get("temp_c"));
                forecast.put("high", ((Map<String, Object>) ((List<Map<String, Object>>) responseForecast.get("forecast")).get(0).get("day")).get("maxtemp_c"));
                forecast.put("low", ((Map<String, Object>) ((List<Map<String, Object>>) responseForecast.get("forecast")).get(0).get("day")).get("mintemp_c"));
                forecast.put("cached", false);
                if(!initialCache.containsKey(zipCode)) {
                    initialCache.put(zipCode, forecast); //add to cache if not in
                }
                return forecast;
            } else {
                // Handle the error
                System.err.println("Error: " + response.getStatusCode());
            }
        }
        return null;
    }

    public Map<String, Object> getForecastByAddress(String address) {
        try {
            String zipCode =extractZipCode(address);
            return getForecast(zipCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}