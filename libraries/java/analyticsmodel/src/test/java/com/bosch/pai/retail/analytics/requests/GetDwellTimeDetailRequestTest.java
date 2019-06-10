
package com.bosch.pai.retail.analytics.requests;

import org.junit.Test;

import static org.junit.Assert.*;





public class GetDwellTimeDetailRequestTest {
    @Test
    public void testGetDwellTimeDetailRequest() {
        String ASSERT_MESSAGE = "Assertion failed";

        Long startTime = 2L;
        Long endTime = 3L;
        String site = "testSite";
        String location = "testLocation";
        GetDwellTimeDetailRequest getDwellTimeDetailRequest = new GetDwellTimeDetailRequest();
        getDwellTimeDetailRequest.setStartTime(startTime);
        getDwellTimeDetailRequest.setEndTime(endTime);
        getDwellTimeDetailRequest.setSite(site);
        getDwellTimeDetailRequest.setLocation(location);


        final Long startTimeActual = getDwellTimeDetailRequest.getStartTime();
        assertEquals(ASSERT_MESSAGE, startTime, startTimeActual);
        final Long endTimeActual = getDwellTimeDetailRequest.getEndTime();
        assertEquals(ASSERT_MESSAGE, endTime, endTimeActual);
        final String siteActual = getDwellTimeDetailRequest.getSite();
        assertEquals(ASSERT_MESSAGE, site, siteActual);
        final String locationActual = getDwellTimeDetailRequest.getLocation();
        assertEquals(ASSERT_MESSAGE, location, locationActual);

        final String actualString = getDwellTimeDetailRequest.toString();
        assertNotEquals(ASSERT_MESSAGE, actualString);

    }

}
