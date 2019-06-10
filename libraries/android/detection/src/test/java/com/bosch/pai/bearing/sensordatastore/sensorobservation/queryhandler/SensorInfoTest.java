package com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SensorInfo.class})
public class SensorInfoTest {

    private SensorInfo sensorInfo;

    @Before
    public void init() throws Exception {
        sensorInfo = new SensorInfo(BearingConfiguration.SensorType.ST_WIFI,true,4564151);
    }

    @Test
    public void testFunc(){
        sensorInfo.setResponseBased(true);
        sensorInfo.setScanInterval(4564151);
        sensorInfo.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        Assert.assertTrue(sensorInfo.isResponseBased());
        Assert.assertEquals(4564151, sensorInfo.getScanInterval());
        Assert.assertEquals(BearingConfiguration.SensorType.ST_WIFI, sensorInfo.getSensorType());
    }
}
