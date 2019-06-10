package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.offeranalytics.OfferDetails;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserOfferAnalyticsResponseTest {

    @Test

    public void testUserOfferAnalyticsResponseTest() {

        UserOfferAnalyticsResponse userOfferAnalyticsResponse = new UserOfferAnalyticsResponse();

       userOfferAnalyticsResponse.setDetails(new ArrayList<OfferDetails>());
       userOfferAnalyticsResponse.setHierarchyType("section");
       userOfferAnalyticsResponse.setSiteName("kor");




        assertNotNull(userOfferAnalyticsResponse.getDetails());

        assertEquals("section",userOfferAnalyticsResponse.getHierarchyType());
        assertEquals("kor", userOfferAnalyticsResponse.getSiteName());
        assertNotNull(userOfferAnalyticsResponse.toString());
















    }
}