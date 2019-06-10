
package com.bosch.pai.retail.analytics.model.heatmap;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;




public class HeatMapDetailTest {
    private final String storeId = "store";
    private final String siteName = "site";
    private final String locationName = "location";
    private final Integer userCount = 1;
    private final String ASSERT_MESSAGE = "Assertion failed";

    @Test
    public void testHeatMapDetailTest() throws Exception {
        Long startTime = 1511948719289L;
        Long endTime = 1511948444289L;
        final Timestamp startTimestamp = new Timestamp(startTime);
        final Timestamp endTimestamp = new Timestamp(endTime);
        HeatMapDetail heatMapDetail = new HeatMapDetail();
        String companyName = "company name";
        heatMapDetail.setCompanyName(companyName);
        heatMapDetail.setLocationName(locationName);
        heatMapDetail.setSiteName(siteName);
        heatMapDetail.setStartTime(startTime);
        heatMapDetail.setEndTime(endTime);
        heatMapDetail.setStoreId(storeId);
        heatMapDetail.setUserCount(userCount);

        final String companyNameActual = heatMapDetail.getCompanyName();
        final String locationNameActual = heatMapDetail.getLocationName();
        final String siteNameActual = heatMapDetail.getSiteName();

        final String storeIdActual = heatMapDetail.getStoreId();
        final Integer userCountActual = heatMapDetail.getUserCount();

       // final Timestamp startTimeActual = heatMapDetail.getStartTime();
       // final Timestamp endTimeActual = heatMapDetail.getEndTime();


        assertEquals(ASSERT_MESSAGE, companyName, companyNameActual);
        assertEquals(ASSERT_MESSAGE, locationName, locationNameActual);
        assertEquals(ASSERT_MESSAGE, siteName, siteNameActual);
        assertEquals(ASSERT_MESSAGE, storeId, storeIdActual);
        assertEquals(ASSERT_MESSAGE, userCount, userCountActual);
     assertNotNull(heatMapDetail.getStartTime());
     assertNotNull(heatMapDetail.getEndTime());
    }

    @Test
    public void testConstructor() {
        String companyName = "test";
        HeatMapDetail heatMapDetail = new HeatMapDetail("test", "new", "entry","kor", 5, 12L,13L);
        final String companyNameActual = heatMapDetail.getCompanyName();
        assertEquals(ASSERT_MESSAGE, companyName, companyNameActual);

    }

    @Test
    public void testToString() {
        String companyName = "name";
        HeatMapDetail heatMapDetail = new HeatMapDetail();
        final String objectTest = heatMapDetail.toString();
        assertNotNull(ASSERT_MESSAGE, objectTest);
    }

}
