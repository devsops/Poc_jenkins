package com.bosch.pai.retail.adtuning.model.offer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PromoLocationTest {
    @Test
            public void testPromoLocationTest() {


        PromoLocation promoLocation= new PromoLocation("test", "new1");


        promoLocation.setLocationName("new1");
        promoLocation.setSiteName("test");
        assertEquals("test",promoLocation.getSiteName());
        assertEquals("new1",promoLocation.getLocationName());

        assertNotNull(promoLocation.hashCode());
        assertNotNull(promoLocation.equals(0));
    }
}
