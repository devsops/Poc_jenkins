package com.bosch.pai.retail.adtuning.responses;

import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.adtuning.model.offer.PromoLocation;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PromoMapOfferResponseTest {

    StatusMessage statusMessage = new StatusMessage();
    @Test

    public void testPromoMapOfferResponseTest(){

        PromoMapOfferResponse promoMapOfferResponse =new PromoMapOfferResponse();


promoMapOfferResponse.setPromoDetailMap(new HashMap<PromoLocation, List<PromoDetail>>());
promoMapOfferResponse.setStatusMessage(statusMessage);
        assertNotNull(promoMapOfferResponse.getStatusMessage());
       promoMapOfferResponse.getPromoDetailMap();
        assertNotNull(promoMapOfferResponse.toString());






    }










}
