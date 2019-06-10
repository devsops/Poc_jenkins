
package com.bosch.pai.retail.adtuning.requests;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;




public class GetOfferRequestTest {

    private final String ASSERT_MESSAGE = "Assertion failed";

    @Test
    public void testGetOfferRequest() {
        GetOfferRequest getOfferRequest = new GetOfferRequest();
        String location = "location_name";
        String site = "site_name";
        String userId = "userid_2001";
        getOfferRequest.setLocation(location);
        getOfferRequest.setSite(site);
        getOfferRequest.setUserId(userId);


        final String locationActual = getOfferRequest.getLocation();
        final String actualSite = getOfferRequest.getSite();
        final String actualUserId = getOfferRequest.getUserId();
        Assert.assertEquals(ASSERT_MESSAGE, location, locationActual);
        Assert.assertEquals(ASSERT_MESSAGE, site, actualSite);
        Assert.assertEquals(ASSERT_MESSAGE, userId, actualUserId);


    }

    @Test
    public void testToString() {
        GetOfferRequest getOfferRequest = new GetOfferRequest();
        String location = "location_name";
        String site = "site_name";
        String userId = "userid_2001";
        getOfferRequest.setLocation(location);
        getOfferRequest.setSite(site);
        getOfferRequest.setUserId(userId);
        final String objectString = getOfferRequest.toString();
        assertNotNull(ASSERT_MESSAGE, objectString);
    }

}
