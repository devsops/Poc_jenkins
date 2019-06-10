package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SiteLocationDetailsTest {

    @Test

    public void testSiteLocationDetailsTest(){

      LocationCateDeptBrand locationCateDeptBrand = new LocationCateDeptBrand();
        SiteLocationDetails siteLocationDetails = new SiteLocationDetails("test","test1","kor","ban",locationCateDeptBrand);
        SiteLocationDetails siteLocationDetails1 = new SiteLocationDetails("test","test1","kor","ban",locationCateDeptBrand);
        SiteLocationDetails siteLocationDetails2 =new SiteLocationDetails();

        siteLocationDetails.setCompanyId("test");
       siteLocationDetails.setLocationCateDeptBrand(locationCateDeptBrand);
        siteLocationDetails.setLocationName("ban");
        siteLocationDetails.setSiteName("kor");
        siteLocationDetails.setStoreId("test1");

        assertEquals("test",siteLocationDetails.getCompanyId());
        assertEquals("ban",siteLocationDetails.getLocationName());
        assertEquals("kor",siteLocationDetails.getSiteName());
        assertEquals("test1",siteLocationDetails.getStoreId());
        assertNotNull(siteLocationDetails.toString());
        assertNotNull(siteLocationDetails2);
        assertNotNull(siteLocationDetails.hashCode());
        assertTrue(siteLocationDetails.equals(siteLocationDetails1));







    }










}
