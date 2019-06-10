package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.gps_geofence;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GeoFenceStatusCode.class})
public class GeoFenceStatusCodeTest {

    @Test
    public void getStatusCodeStringTest(){
        Assert.assertEquals("GEO_FENCE_ENTERED",GeoFenceStatusCode.getStatusCodeString(1));
        Assert.assertEquals("GEO_FENCE_EXITED",GeoFenceStatusCode.getStatusCodeString(2));
        Assert.assertEquals("GEO_FENCE_DWELL",GeoFenceStatusCode.getStatusCodeString(4));
        Assert.assertEquals("UNKNOWN_STATUS_CODE",GeoFenceStatusCode.getStatusCodeString(0));
    }
}
