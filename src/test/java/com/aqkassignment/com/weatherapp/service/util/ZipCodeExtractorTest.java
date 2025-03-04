package com.aqkassignment.com.weatherapp.service.util;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ZipCodeExtractorTest {

    @Test
    public void testExtractZipCode_validZipCode() {
        assertEquals("12345", ZipCodeExtractor.extractZipCode("123 Main St, Anytown, CA 12345"));
        assertEquals("12345-6789", ZipCodeExtractor.extractZipCode("PO Box 456, Anytown, NY 12345-6789"));
    }

    @Test
    public void testExtractZipCode_noZipCode() {
        assertNull(ZipCodeExtractor.extractZipCode("123 Main St, Anytown, CA"));
        assertNull(ZipCodeExtractor.extractZipCode(""));
    }

    @Test
    public void testExtractZipCode_multipleZipCodes() {
        assertEquals("54321", ZipCodeExtractor.extractZipCode("123 Main St 54321 Anytown CA 98765"));
    }

    @Test
    public void testExtractZipCode_invalidZipCode() {
        assertNull(ZipCodeExtractor.extractZipCode("123 Main St, Anytown, CA 1234"));
        assertNull(ZipCodeExtractor.extractZipCode("123 Main St, Anytown, CA 123456"));
    }

    @Test
    public void testExtractZipCode_nullAddress() {
        assertNull(ZipCodeExtractor.extractZipCode(null));
    }
}