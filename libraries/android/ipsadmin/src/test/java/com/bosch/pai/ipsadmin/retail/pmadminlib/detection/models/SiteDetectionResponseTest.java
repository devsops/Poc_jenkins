package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SiteDetectionResponse.class)
public class SiteDetectionResponseTest {

    private SiteDetectionResponse siteDetectionResponse;

    @Before
    public void init() throws Exception {
        siteDetectionResponse = new SiteDetectionResponse();
        siteDetectionResponse = new SiteDetectionResponse(siteDetectionResponse);
    }

    @Test
    public void getAndSetSiteNameTest(){
        siteDetectionResponse.setSiteName("siteName");
        Assert.assertEquals("siteName",siteDetectionResponse.getSiteName());
    }

    @Test
    public void getAndSetTimestampTest(){
        siteDetectionResponse.setTimestamp("10215");
        Assert.assertEquals("10215",siteDetectionResponse.getTimestamp());
    }

    @Test
    public void testToString() {
        SiteDetectionResponse siteDetectionResponse = new SiteDetectionResponse("siteName", "10215");
        Assert.assertNotEquals("Assertion failed", siteDetectionResponse.toString());
    }
}
