package com.bosch.pai.retail.adtuning.model.offer;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PromoDetailTest {



    @Test
    public void testPromoDetailTest(){
        PromoDetail promoDetail = new PromoDetail("hello",
                "12",15,new HashMap<String, String>(),
                "newItem" ,"footwear","c/abc");



        assertEquals("hello",promoDetail.getDisplayMessage());
        assertEquals("12",promoDetail.getMessageCode());
        assertEquals(Integer.valueOf(15),promoDetail.getRank());
        assertEquals("newItem",promoDetail.getItemCode());
        assertEquals("footwear",promoDetail.getItemDescription());
        assertEquals("c/abc",promoDetail.getImageUrl());
        assertNotNull(promoDetail.toString());

        assertNotNull(promoDetail.hashCode());


    }











}
