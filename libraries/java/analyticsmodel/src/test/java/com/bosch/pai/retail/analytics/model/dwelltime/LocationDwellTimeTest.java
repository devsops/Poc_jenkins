
package com.bosch.pai.retail.analytics.model.dwelltime;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class LocationDwellTimeTest {

    @Test
    public void testLocationDwellTime() {
        LocationDwellTime locationDwellTime = new LocationDwellTime();
        Long startTime = 1511948719289L;
        Long endTime = 1511948444289L;
        String companyId = "COMPANY";
        Integer userCount = 3;
        String storeId = "20001";
        String siteName = "20001_site";
        String locationName = "location";
        Float averageDuration = 5F;

        final Timestamp startTimestamp = new Timestamp(startTime);
        final Timestamp endTimestamp = new Timestamp(endTime);
        locationDwellTime.setCompanyId(companyId);
        locationDwellTime.setStoreId(storeId);
        locationDwellTime.setSiteName(siteName);
        locationDwellTime.setLocationName(locationName);
        locationDwellTime.setAverageDuration(averageDuration);
        locationDwellTime.setUserCount(userCount);
        locationDwellTime.setEndTime(startTime);
        locationDwellTime.setStartTime(endTime);


        final String companyIdActual = locationDwellTime.getCompanyId();
        final String storeIdActual = locationDwellTime.getStoreId();
        String ASSERT_MESSAGE = "Assertion failed";
        assertEquals(ASSERT_MESSAGE, companyId, companyIdActual);
        assertEquals(ASSERT_MESSAGE, storeId, storeIdActual);
        final String siteNameActual = locationDwellTime.getSiteName();
        assertEquals(ASSERT_MESSAGE, siteName, siteNameActual);
        final String locationNameActual = locationDwellTime.getLocationName();
        assertEquals(ASSERT_MESSAGE, locationName, locationNameActual);
        final Float averageDurationActual = locationDwellTime.getAverageDuration();
        assertEquals(ASSERT_MESSAGE, averageDuration, averageDurationActual);
        final Integer userCountActual = locationDwellTime.getUserCount();
        assertEquals(ASSERT_MESSAGE, userCount, userCountActual);
        //final Timestamp endTime = locationDwellTime.getStartTime();
        //assertEquals(1511948719289L,locationDwellTime.getStartTime());
        //assertEquals(1511948444289L,locationDwellTime.getEndTime());
       // final Timestamp startTimeActual = locationDwellTime.getStartTime();
       // assertEquals(ASSERT_MESSAGE, startTimestamp, startTimeActual);
        assertNotNull(locationDwellTime.toString());
        assertNotNull(locationDwellTime.getStartTime());
        assertNotNull(locationDwellTime.getEndTime());

    }

}
