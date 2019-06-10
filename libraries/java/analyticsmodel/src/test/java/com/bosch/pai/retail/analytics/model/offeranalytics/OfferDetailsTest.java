package com.bosch.pai.retail.analytics.model.offeranalytics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OfferDetailsTest {



    @Test

    public void testOfferDetailsTest(){

        OfferDetails offerDetails =new OfferDetails();
        offerDetails.setAcceptedOfferCount(12);
        offerDetails.setDisplayedOfferCount(120);
        offerDetails.setName("test");


        assertEquals(12,offerDetails.getAcceptedOfferCount());
        assertEquals(120,offerDetails.getDisplayedOfferCount());

        assertEquals("test",offerDetails.getName());
        assertNotNull(offerDetails.toString());


    }
}
