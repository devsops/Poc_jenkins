package com.bosch.pai.retail.adtuning.model.offer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocationPromoDetailTest {

    @Test
    public void testLocationPromoDetailTest(){
        LocationPromoDetail locationPromoDetail = new LocationPromoDetail();

        locationPromoDetail.setLocationName("test");
        locationPromoDetail.setPromoDetail(new ArrayList<PromoDetail>());
        locationPromoDetail.setSiteName("test");

        assertEquals("test",locationPromoDetail.getLocationName());
        assertEquals("test",locationPromoDetail.getSiteName());
        assertEquals(0,locationPromoDetail.getPromoDetail().size());

        assertNotNull(locationPromoDetail.toString());

    }
}
