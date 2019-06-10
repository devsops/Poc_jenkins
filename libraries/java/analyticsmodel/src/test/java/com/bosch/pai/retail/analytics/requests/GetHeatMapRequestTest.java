
package com.bosch.pai.retail.analytics.requests;

import org.junit.Test;

import static org.junit.Assert.*;





public class GetHeatMapRequestTest {
    @Test
    public void testGetHeatMapRequest() {
        String ASSERT_MESSAGE = "Assertion failed";

        Long startTime = 2L;
        Long endTime = 3L;
        String site = "testSite";
        String location = "testLocation";
        GetHeatMapRequest getHeatMapRequest = new GetHeatMapRequest();
        getHeatMapRequest.setStartTime(startTime);
        getHeatMapRequest.setEndTime(endTime);
        getHeatMapRequest.setSite(site);
        getHeatMapRequest.setLocation(location);


        final Long startTimeActual = getHeatMapRequest.getStartTime();
        assertEquals(ASSERT_MESSAGE, startTime, startTimeActual);
        final Long endTimeActual = getHeatMapRequest.getEndTime();
        assertEquals(ASSERT_MESSAGE, endTime, endTimeActual);
        final String siteActual = getHeatMapRequest.getSite();
        assertEquals(ASSERT_MESSAGE, site, siteActual);
        final String locationActual = getHeatMapRequest.getLocation();
        assertEquals(ASSERT_MESSAGE, location, locationActual);

        final String actualString = getHeatMapRequest.toString();
        assertNotEquals(ASSERT_MESSAGE, actualString);

    }

}
