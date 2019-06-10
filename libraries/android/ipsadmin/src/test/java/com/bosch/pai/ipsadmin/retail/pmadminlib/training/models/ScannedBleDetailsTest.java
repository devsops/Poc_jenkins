package com.bosch.pai.ipsadmin.retail.pmadminlib.training.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ScannedBleDetails.class})
public class ScannedBleDetailsTest {

    private ScannedBleDetails scannedBleDetails;

    @Before
    public void init() throws Exception {
        scannedBleDetails = new ScannedBleDetails();
    }

    @Test
    public void getAndSetBleIdTest(){
        scannedBleDetails.setBleId("ble");
        Assert.assertEquals("ble",scannedBleDetails.getBleId());
    }

    @Test
    public void getAndSetBleRssiTest(){
        scannedBleDetails.setBleRssi(5.5);
        Assert.assertEquals(5.5,scannedBleDetails.getBleRssi(),0.0);
    }

    @Test
    public void testToString() {
        ScannedBleDetails scannedBleDetail = new ScannedBleDetails("ble",5.5);
        assertNotNull("Assertion failed", scannedBleDetail.toString());
    }
}
