package com.bosch.pai.retail.adtuning.model.offer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OfferResponseTest {


    OfferResponseStatus offerResponseStatus = new OfferResponseStatus(OfferResponseStatus.STATUS.ACCEPTED);

    OfferResponse test = new OfferResponse();
    @Test
    public void testOfferResponse(){
        OfferResponse offerResponse = new OfferResponse("test","123",offerResponseStatus,12L);
          offerResponse.setUserId("test");
          offerResponse.setOfferActiveDuration(12L);
          offerResponse.setOfferResponseStatus(offerResponseStatus);
          offerResponse.setPromoCode("123");
          assertEquals("test",offerResponse.getUserId());
          assertNotNull(offerResponse.getOfferActiveDuration());
          assertEquals( offerResponseStatus,offerResponse.getOfferResponseStatus());
          assertEquals("123",offerResponse.getPromoCode());

        assertNotNull(offerResponse.toString());






    }
}
