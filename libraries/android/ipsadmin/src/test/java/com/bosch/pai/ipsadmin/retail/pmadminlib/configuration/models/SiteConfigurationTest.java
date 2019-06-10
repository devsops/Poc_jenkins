package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SiteConfiguration.class})
public class SiteConfigurationTest {

    private SiteConfiguration siteConfiguration;

    @Before
    public void init() throws Exception {
        siteConfiguration = new SiteConfiguration(1,1.5);
        siteConfiguration = new SiteConfiguration(siteConfiguration);
    }

    @Test
    public void getAndSetMinSitePredictCountTest(){
        siteConfiguration.setMinSitePredictCount(1);
        Assert.assertEquals(1, siteConfiguration.getMinSitePredictCount());
    }

    @Test
    public void getAndSetMinLocationProbabilityTest(){
        siteConfiguration.setMinLocationProbability(1.2);
        Assert.assertEquals(1.2, siteConfiguration.getMinLocationProbability(),0.0);
    }

    @Test
    public void testToString() {
        SiteConfiguration siteConfiguration = new SiteConfiguration(1,1.5);
        Assert.assertNotNull("Assertion failed",siteConfiguration.toString());
    }
}
