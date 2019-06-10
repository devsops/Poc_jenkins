package com.bosch.pai.retail.adtuning.model.offer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OfferResponseStatusTest {



    //erResponseStatus offerResponseStatus = new OfferResponseStatus("","","");

    @Test
    public void testResponseStatusTest(){

    OfferResponseStatus offerResponseStatus = new OfferResponseStatus(OfferResponseStatus.STATUS.ACCEPTED);

        offerResponseStatus.setStatus(OfferResponseStatus.STATUS.ACCEPTED);

        assertEquals(OfferResponseStatus.STATUS.valueOf("ACCEPTED"),offerResponseStatus.getStatus());
        assertNotNull(offerResponseStatus.toString());
        assertNotNull(offerResponseStatus.getStatus());


}

}
