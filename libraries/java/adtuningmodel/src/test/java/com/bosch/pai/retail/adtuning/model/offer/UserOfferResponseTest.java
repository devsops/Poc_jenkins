package com.bosch.pai.retail.adtuning.model.offer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserOfferResponseTest {

    //OfferResponseStatus offerResponseStatus = new OfferResponseStatus(OfferResponseStatus.STATUS.ACCEPTED);

    @Test
     public void testUserOfferResponse(){

        UserOfferResponse userOfferResponse = new UserOfferResponse();


        userOfferResponse.setSiteName("lab");
        userOfferResponse.setLocationName("pai");
        userOfferResponse.setStoreId("123");
        userOfferResponse.setMessageCode("589");
        userOfferResponse.setUserId("testuser");
        userOfferResponse.setOfferActiveDuration(12L);
        userOfferResponse.setUserResponseTimeStamp(10L);
        //userOfferResponse.setofferResponseStatus(offerResponseStatus);

        assertEquals("lab",userOfferResponse.getSiteName());
        assertEquals("pai",userOfferResponse.getLocationName());
        assertEquals("123",userOfferResponse.getStoreId());
        assertEquals("589",userOfferResponse.getMessageCode());
        assertEquals("testuser",userOfferResponse.getUserId());
        assertNotNull(userOfferResponse.getOfferActiveDuration());
        assertNotNull(userOfferResponse.getUserResponseTimeStamp());

        assertNotNull(userOfferResponse.toString());




    }







}
