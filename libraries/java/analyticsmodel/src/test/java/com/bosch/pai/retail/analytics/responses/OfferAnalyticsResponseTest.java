package com.bosch.pai.retail.analytics.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OfferAnalyticsResponseTest {

    @Test

    public void testOfferAnalyticsResponseTest(){

        OfferAnalyticsResponse offerAnalyticsResponse = new OfferAnalyticsResponse();


        offerAnalyticsResponse.setCompanyId("test");
        offerAnalyticsResponse.setSiteName("kor");
        offerAnalyticsResponse.setAcceptedOfferCount(12);
        offerAnalyticsResponse.setDisplayedOfferCount(10);
        offerAnalyticsResponse.setLocationName("ban");
        offerAnalyticsResponse.setStoreId("123");
        offerAnalyticsResponse.setStartTime(12345);
        offerAnalyticsResponse.setEndTime(100);

        assertEquals("test",offerAnalyticsResponse.getCompanyId());
        assertEquals("kor",offerAnalyticsResponse.getSiteName());
        assertEquals(12,offerAnalyticsResponse.getAcceptedOfferCount());
        assertEquals(10,offerAnalyticsResponse.getDisplayedOfferCount());
        assertEquals("ban",offerAnalyticsResponse.getLocationName());
        assertEquals("123",offerAnalyticsResponse.getStoreId());
        assertEquals(12345,offerAnalyticsResponse.getStartTime());
        assertEquals(100,offerAnalyticsResponse.getEndTime());

        assertNotNull(offerAnalyticsResponse.toString());



    }







}
