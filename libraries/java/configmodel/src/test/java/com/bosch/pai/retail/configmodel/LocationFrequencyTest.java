package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocationFrequencyTest {

    @Test

    public void testLocationFrequencyTest(){

        LocationFrequency locationFrequency2 = new LocationFrequency();

        LocationFrequency locationFrequency = new LocationFrequency("test",15l);
        LocationFrequency locationFrequency1 = new LocationFrequency("test",15l);

        locationFrequency.setLocationName("test");
          locationFrequency.setMaxOfferCount(15l);
        assertEquals("test",locationFrequency.getLocationName());
        assertEquals(15,locationFrequency.getMaxOfferCount());
        assertNotNull(locationFrequency.toString());
        assertNotNull(locationFrequency.hashCode());
        assertNotNull(locationFrequency2);
        assertTrue(locationFrequency.equals(locationFrequency1));














    }






















}
