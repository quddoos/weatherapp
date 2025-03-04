package com.aqkassignment.com.weatherapp;

import com.aqkassignment.com.weatherapp.service.WeatherService;
import com.aqkassignment.com.weatherapp.service.util.ResponseConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherService.initializeCache();
    }

    @Test
    void getForecast_returnsCachedData() {
        Map<String, Object> forecast = weatherService.getForecast("10001");

        assertThat(forecast).isNotNull();
        assertThat(forecast.get("temperature")).isEqualTo(22.0);
        assertThat(forecast.get("high")).isEqualTo(28.0);
        assertThat(forecast.get("low")).isEqualTo(18.0);
        assertThat(forecast.get("cached")).isEqualTo(true);
    }

    @Test
    void getForecast_fetchesFromApi() {
        String zipCode = "99999";
        String mockResponseBody = "{\"current\": {\"temp_c\": 24.0}, \"forecast\": [{\"day\": {\"maxtemp_c\": 29.0, \"mintemp_c\": 19.0}}]}";

        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);

        HashMap<String, Object> convertedResponse = new HashMap<>();
        convertedResponse.put("current", Map.of("temp_c", 24.0));
        convertedResponse.put("forecast", List.of(Map.of("day", Map.of("maxtemp_c", 29.0, "mintemp_c", 19.0))));

        mockStatic(ResponseConverter.class);
        when(ResponseConverter.convert(mockResponse)).thenReturn(convertedResponse);

        Map<String, Object> forecast = weatherService.getForecast(zipCode);

        assertThat(forecast).isNotNull();
        assertThat(forecast.get("temperature")).isEqualTo(24.0);
        assertThat(forecast.get("high")).isEqualTo(29.0);
        assertThat(forecast.get("low")).isEqualTo(19.0);
        assertThat(forecast.get("cached")).isEqualTo(false);
    }

    @Test
    void getForecast_handlesApiError() {
        String zipCode = "99999";

        ResponseEntity<String> mockErrorResponse = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockErrorResponse);

        Map<String, Object> forecast = weatherService.getForecast(zipCode);

        assertThat(forecast).isNull();
    }

    @Test
    void getForecastByAddress_extractsZipCodeAndFetchesForecast() {
        String address = "123 Main St, New York, NY 10001";

        Map<String, Object> forecast = weatherService.getForecastByAddress(address);

        assertThat(forecast).isNotNull();
        assertThat(forecast.get("temperature")).isEqualTo(22.0);
        assertThat(forecast.get("cached")).isEqualTo(true);
    }

    @Test
    void getForecastByAddress_handlesException() {
        String invalidAddress = "Invalid Address";

        assertThatThrownBy(() -> weatherService.getForecastByAddress(invalidAddress))
                .isInstanceOf(RuntimeException.class);
    }
}

