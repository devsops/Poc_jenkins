package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.util;

import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.util.ConfigurationUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest()
public class ConfigurationUtilTest {
    private static final String COMPANIES = "companies/";
    private static final String STORES = "/stores/";
    private static final String SITES = "/sites/";
    private static final String LOCATIONS = "/locations/";

    private static final String CATEGORY = "/category/";

    @Before
    public void init() {

    }

    @Test
    public void getCateDeptBrandEndpointTest() {

        String catdep = ConfigurationUtil.getCateDeptBrandEndpoint("CompanyIdTEst", "StoreIDTest", "CategoryTest", "departTest");
        String catdep_withNull = ConfigurationUtil.getCateDeptBrandEndpoint("CompanyIdTEst", "StoreIDTest", null, null);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/getAllCategory/", catdep_withNull);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/category/CategoryTest/department/departTest/getBrandForDepartment/", catdep);
    }

    @Test
    public void getLocationBayMapEndpointTest() {
        String locationmapendpointtst = ConfigurationUtil.getLocationBayMapEndpoint("CompanyIdTEst", "StoreIDTest", "SiteTest", "LocationTest");
        String locationmapendpointtst_nulldata = ConfigurationUtil.getLocationBayMapEndpoint("CompanyIdTEst", "StoreIDTest", null, null);
        String locationmapendpointtst_locnulldata = ConfigurationUtil.getLocationBayMapEndpoint("CompanyIdTEst", "StoreIDTest", "SiteTest", null);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/locations/LocationTest/getCateDeptBrandMappingDetails/", locationmapendpointtst);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/getCateDeptBrandMappingDetails/", locationmapendpointtst_nulldata);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/locations/getCateDeptBrandMappingDetails/", locationmapendpointtst_locnulldata);
    }

    @Test
    public void getSiteConfigurationEndpointTets() {
        String siteconfigendpoint = ConfigurationUtil.getSiteConfigurationEndpoint("CompanyIdTEst", "StoreIDTest", "SiteTest");
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/getSiteConfiguration/", siteconfigendpoint);
        String siteconfigendpoint_nullsite = ConfigurationUtil.getSiteConfigurationEndpoint("CompanyIdTEst", "StoreIDTest", null);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/getSiteConfiguration/", siteconfigendpoint_nullsite);

    }

    @Test
    public void getLocationBayMapUrlTest() {
        String locbymapurl = ConfigurationUtil.getLocationBayMapUrl("CompanyIdTEst", "StoreIDTest", "SiteTest", "LocationTest");
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/locations/LocationTest/getLocationBayMap/", locbymapurl);
        String locbymapurl_nullLoc = ConfigurationUtil.getLocationBayMapUrl("CompanyIdTEst", "StoreIDTest", "SiteTest", null);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/locations/getLocationBayMap/", locbymapurl_nullLoc);
        String locbymapurl_sitenullLoc = ConfigurationUtil.getLocationBayMapUrl("CompanyIdTEst", "StoreIDTest", null, null);
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/getLocationBayMap/", locbymapurl_sitenullLoc);

    }

    @Test
    public void getStoreConfigurationEndpointTest() {
        String strconfigEndpoint = ConfigurationUtil.getStoreConfigurationEndpoint(COMPANIES, STORES, SITES);
        Assert.assertEquals("companies/companies//stores//stores//sites//sites//getStoreConfiguration/", strconfigEndpoint);
        String strconfigEndpoint_nulldata = ConfigurationUtil.getStoreConfigurationEndpoint(COMPANIES, null, null);
        Assert.assertEquals("companies/companies//getStoreConfiguration/", strconfigEndpoint_nulldata);

    }

    @Test
    public void testAllrestMethods() {
        Assert.assertEquals("companies/companies//saveStoreConfiguration/", ConfigurationUtil.getSaveStoreConfigurationEndpoint(COMPANIES));
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/locations/LocationTest/SaveLocationBayMap/", ConfigurationUtil.saveLocationBayMapUrl("CompanyIdTEst", "StoreIDTest", "SiteTest", "LocationTest"));
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/SaveOrUpdateLocations/", ConfigurationUtil.getSaveStoreLocationsEndpoint("CompanyIdTEst", "StoreIDTest", "SiteTest"));
        Assert.assertEquals("companies/CompanyIdTEst/stores/StoreIDTest/sites/SiteTest/SaveSiteConfiguration/", ConfigurationUtil.saveSiteConfigurationEndpoint("CompanyIdTEst", "StoreIDTest", "SiteTest"));


    }

    @Test
    public void getCategoriesForBayMapUrlTest(){
        Assert.assertEquals("companies/companies//stores//stores//categories/",ConfigurationUtil.getCategoriesForBayMapUrl(COMPANIES,STORES));
    }

}
