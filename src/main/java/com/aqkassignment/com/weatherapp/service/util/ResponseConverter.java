package com.aqkassignment.com.weatherapp.service;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

public class ResponseConverter {

    public static <ObjectMapper> HashMap<String, Object> convert(ResponseEntity<?> responseEntity) {
        Object body = responseEntity.getBody();

        if (body == null) {
            return new HashMap<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> map = objectMapper.convertValue(body, HashMap.class);
        return map;
    }
}