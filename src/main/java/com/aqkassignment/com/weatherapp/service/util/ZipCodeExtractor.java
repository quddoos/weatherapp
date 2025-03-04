package com.aqkassignment.com.weatherapp.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipCodeExtractor {

    public static String extractZipCode(String address) {
        String zipCode = null;
        if (address != null && !address.isEmpty()) {
            Pattern pattern = Pattern.compile("\\b\\d{5}(?:-\\d{4})?\\b");
            Matcher matcher = pattern.matcher(address);
            if (matcher.find()) {
                zipCode = matcher.group();
            }
        }
        return zipCode;
    }
}