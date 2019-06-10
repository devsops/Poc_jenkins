package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocationDetectionResponse.class)
public class LocationDetectionResponseTest {

    private LocationDetectionResponse locationDetectionResponse;
    private Map<String, Double> locationUpdateMap = new HashMap<>();

    @Before
    public void init() throws Exception {
        locationDetectionResponse = new LocationDetectionResponse();
        locationDetectionResponse = new LocationDetectionResponse(locationDetectionResponse);
    }

    @Test
    public void getAndSetLocationUpdateMapTest(){
        locationDetectionResponse.setLocationUpdateMap(locationUpdateMap);
        Assert.assertEquals(locationUpdateMap,locationDetectionResponse.getLocationUpdateMap());
    }

    @Test
    public void getAndSetSiteNameTest(){
        locationDetectionResponse.setSiteName("siteName");
        Assert.assertEquals("siteName",locationDetectionResponse.getSiteName());
    }

    @Test
    public void getAndSetLocationNameTest(){
        locationDetectionResponse.setLocationName("locationName");
        Assert.assertEquals("locationName",locationDetectionResponse.getLocationName());
    }

    @Test
    public void getAndSetTimestampTest(){
        locationDetectionResponse.setTimestamp("10215");
        Assert.assertEquals("10215",locationDetectionResponse.getTimestamp());
    }

    @Test
    public void getAndSetProbabilityTest(){
        locationDetectionResponse.setProbability(5.5);
        Assert.assertEquals(5.5,locationDetectionResponse.getProbability(),0.0);
    }

    @Test
    public void testToString() {
        LocationDetectionResponse locationDetectionResponse = new LocationDetectionResponse("siteName", "locationName", "10215", 5.5, locationUpdateMap);
        assertNotNull("Assertion failed",locationDetectionResponse.toString());
    }
}
