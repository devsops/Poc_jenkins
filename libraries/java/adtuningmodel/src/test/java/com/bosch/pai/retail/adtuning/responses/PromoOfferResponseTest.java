package com.bosch.pai.retail.adtuning.responses;

import com.bosch.pai.retail.adtuning.model.offer.LocationPromoDetail;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

public class PromoOfferResponseTest {

    StatusMessage statusMessage = new StatusMessage();
    @Test

    public void testPromoOfferResponseTest(){


        PromoOfferResponse promoOfferResponse = new PromoOfferResponse();

        promoOfferResponse.setStatusMessage(statusMessage);
        promoOfferResponse.setPromoDetailList(new ArrayList<LocationPromoDetail>());
        assertNotNull(promoOfferResponse.getStatusMessage());
        assertNotNull(promoOfferResponse.getPromoDetailList());
        assertNotNull(promoOfferResponse.toString());



    }









}
