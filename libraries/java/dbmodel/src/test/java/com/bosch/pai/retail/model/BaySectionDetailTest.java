package com.bosch.pai.retail.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BaySectionDetailTest {
@Test


    public void testBaySectionDetailTest(){


        BaySectionDetail baySectionDetail = new BaySectionDetail();


        baySectionDetail.setBayName("test");
        baySectionDetail.setSectionName("abc");

        assertEquals("test",baySectionDetail.getBayName());
        assertEquals("abc",baySectionDetail.getSectionName());

        assertNotNull(baySectionDetail.toString());







    }











}
