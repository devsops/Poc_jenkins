package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SensorState.class})
public class SensorStateTest {

    @Test
    public void testFunc(){
        Integer i = 1;
        List<String> activeModeList = new ArrayList<>();
        SensorState sensorState = new SensorState();
        sensorState.setPendingScan(true);
        sensorState.setActiveModeScan(true);
        sensorState.setActiveModeRequestCount(i);
        sensorState.setActiveModeList(activeModeList);
        sensorState.setUuid(UUID.randomUUID());
        Assert.assertTrue(sensorState.getPendingScan());
        Assert.assertTrue(sensorState.isActiveModeScan());
        Assert.assertEquals(i,sensorState.getActiveModeRequestCount());
        Assert.assertEquals(activeModeList,sensorState.getActiveModeList());
        Assert.assertNotNull(sensorState.getUuid());
    }
}
