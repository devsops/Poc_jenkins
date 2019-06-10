package com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.config;

import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.config.AnalyticsConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class AnalyticsConfigTest {

    private AnalyticsConfig analyticsConfig;
    private static final String ALL = "All";
    private static final String COMPANIES = "companies/";
    private static final String STORES = "/stores/";
    private static final String SITES = "/sites/";
    private static final String SITES_NULL = "/sites";
    private static final String LOCATIONS = "/locations/";
    private static final String LOCATIONS_NULL = "/locations";
    private static final String LOCATIONS_WO_SLASH = "/locations";
    /*@Test
    public void testGetInstance() {
        Assert.assertTrue(this.analyticsConfig != null);
    }*/

    @Test
    public void getDwellTimeUrlTest() {
        String dwellTimeUrl = COMPANIES + "CompanyID" + STORES + "StoreID" + SITES + "SiteName" + LOCATIONS + "LocationName";
        String dwellTimeEndPoint = "/DwellTime/";
        String ex = AnalyticsConfig.getDwellTimeUrl("CompanyID", "StoreID", "SiteName", "LocationName");
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getDwellTimeUrlLocationNullTest() {
        String dwellTimeUrl = COMPANIES + "CompanyID" + STORES + "StoreID" + SITES + "SiteName" + LOCATIONS_NULL ;
        String dwellTimeEndPoint = "/DwellTime/";
        String ex = AnalyticsConfig.getDwellTimeUrl("CompanyID", "StoreID", "SiteName", null);
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getDwellTimeUrlLocationSiteNullTest() {
        String dwellTimeUrl = COMPANIES + "CompanyID" + STORES + "StoreID" + SITES_NULL ;
        String dwellTimeEndPoint = "/DwellTime/";
        String ex = AnalyticsConfig.getDwellTimeUrl("CompanyID", "StoreID", null, "LocationName");
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getHeatmapEndpointTest() {
        String dwellTimeUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest" + SITES + "SiteTest" + LOCATIONS + "LocationTest";
        String dwellTimeEndPoint = "/HeatMap/";
        String ex = AnalyticsConfig.getHeatmapEndpoint("CompaniesTest", "Storetest", "SiteTest", "LocationTest");
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getHeatmapEndpointLocationNullTest() {
        String dwellTimeUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest" + SITES + "SiteTest" + LOCATIONS_NULL ;
        String dwellTimeEndPoint = "/HeatMap/";
        String ex = AnalyticsConfig.getHeatmapEndpoint("CompaniesTest", "Storetest", "SiteTest", null);
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getHeatmapEndpointSiteNullTest() {
        String dwellTimeUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest" + SITES_NULL ;
        String dwellTimeEndPoint = "/HeatMap/";
        String ex = AnalyticsConfig.getHeatmapEndpoint("CompaniesTest", "Storetest", null, "LocationTest");
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getOfferAnalyticsEndpointTest() {
        String dwellTimeUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest" + SITES + "SiteTest" + LOCATIONS + "LocationTest";
        String dwellTimeEndPoint = "/OfferAnalytics/";
        String ex = AnalyticsConfig.getOfferAnalyticsEndpoint("CompaniesTest", "Storetest", "SiteTest", "LocationTest");
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getOfferAnalyticsEndpointLocationNullTest() {
        String dwellTimeUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest" + SITES + "SiteTest" + LOCATIONS_NULL;
        String dwellTimeEndPoint = "/OfferAnalytics/";
        String ex = AnalyticsConfig.getOfferAnalyticsEndpoint("CompaniesTest", "Storetest", "SiteTest", null);
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getOfferAnalyticsEndpointSiteNullTest() {
        String dwellTimeUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest" + SITES_NULL;
        String dwellTimeEndPoint = "/OfferAnalytics/";
        String ex = AnalyticsConfig.getOfferAnalyticsEndpoint("CompaniesTest", "Storetest", null, "LocationTest");
        Assert.assertEquals(dwellTimeUrl + dwellTimeEndPoint, ex);
    }

    @Test
    public void getEntryExitEndpointTest() {
        final String entryExitUrl = COMPANIES + "CompaniesTest" + STORES + "Storetest";
        final String entryExitEndPoint = "/getEntryExit/";
        String ex = AnalyticsConfig.getEntryExitEndpoint("CompaniesTest", "Storetest");
        Assert.assertEquals(entryExitUrl + entryExitEndPoint, ex);

    }
}
