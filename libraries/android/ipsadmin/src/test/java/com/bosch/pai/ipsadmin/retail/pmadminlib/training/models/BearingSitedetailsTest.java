package com.bosch.pai.ipsadmin.retail.pmadminlib.training.models;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({BearingSitedetails.class})
public class BearingSitedetailsTest {

    private BearingSitedetails bearingSitedetails;

    private Set<String> wifiLocationNames = new HashSet<>();
    private Set<String> bleLocationNames = new HashSet<>();

    @Before
    public void init() {
        bearingSitedetails = new BearingSitedetails();
        wifiLocationNames.add("wifi");
        bleLocationNames.add("ble");
    }

    @Test
    public void getAndSetSiteNameTest(){
        bearingSitedetails.setSiteName("siteName");
        Assert.assertEquals("siteName", bearingSitedetails.getSiteName());
    }

    @Test
    public void getAndSetWifiLocationNamesTest(){
        bearingSitedetails.setWifiLocationNames(wifiLocationNames);
        Assert.assertEquals(wifiLocationNames, bearingSitedetails.getWifiLocationNames());
    }

    @Test
    public void getAndSetBleLocationNamesTest(){
        bearingSitedetails.setBleLocationNames(bleLocationNames);
        Assert.assertEquals(bleLocationNames, bearingSitedetails.getBleLocationNames());
    }

    @Test
    public void testToString() {
        BearingSitedetails bearingSitedetails1 = new BearingSitedetails("siteName",wifiLocationNames,bleLocationNames);
        assertNotNull("Assertion failed", bearingSitedetails1.toString());
    }
}
