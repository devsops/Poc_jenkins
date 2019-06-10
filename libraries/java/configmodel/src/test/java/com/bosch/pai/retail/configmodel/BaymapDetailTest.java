package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaymapDetailTest {

@Test
    public void testBaymapDetailTest() {


        BaymapDetail baymapDetail = new BaymapDetail("test","test1","ban","kor",new HashSet<String>());
    BaymapDetail baymapDetail1 = new BaymapDetail("test","test1","ban","kor",new HashSet<String>());


        baymapDetail.setCompanyId("test");
        baymapDetail.setLocationName("kor");
        baymapDetail.setSiteName("ban");
        baymapDetail.setStoreId("test1");
        baymapDetail.setBays(new HashSet<String>());

        assertEquals("test",baymapDetail.getCompanyId());
        assertEquals("kor",baymapDetail.getLocationName());
        assertEquals("ban",baymapDetail.getSiteName());
        assertEquals("test1",baymapDetail.getStoreId());

        assertEquals(0,baymapDetail.getBays().size());
        assertNotNull(baymapDetail.toString());
        assertNotNull(baymapDetail.hashCode());
    assertTrue(baymapDetail.equals(baymapDetail1));




    }





}
