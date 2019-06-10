package com.bosch.pai.retail.configmodel;

import com.bosch.pai.retail.requests.SaveLocationBaymapRequest;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SiteLocationsTest {


    @Test

    public void testSiteLocationsTest(){

        SiteLocations siteLocations = new SiteLocations("test","test1","kor",new HashSet<String>());
        SiteLocations siteLocations1 =new SiteLocations("test","test1","kor",new HashSet<String>());

        siteLocations.setCompanyId("test");
        siteLocations.setSiteName("kor");
        siteLocations.setStoreId("test1");
        siteLocations.setLocations(new HashSet<String>());


        assertEquals("test",siteLocations.getCompanyId());
        assertEquals("kor",siteLocations.getSiteName());
        assertEquals("test1",siteLocations.getStoreId());
        assertEquals(0,siteLocations.getLocations().size());

        assertTrue(siteLocations.equals(siteLocations1));

        assertNotNull(siteLocations.toString());
        assertNotNull(siteLocations.hashCode());












    }
















}
