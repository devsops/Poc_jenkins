package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;

import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class ListenerActiveSensorMapTest {

    @Mock
    private SensorObservationListener listener;

    @Test
    public void testSetterAndGetter() {
        final ListenerActiveSensorMap sensorMap = new ListenerActiveSensorMap();
        sensorMap.setFirstObservation(true);
        sensorMap.setObservationListener(listener);
        final List<SensorSelection> selectionList = new ArrayList<>();
        selectionList.add(new SensorSelection());
        sensorMap.setSensorSelections(selectionList);
        Assert.assertTrue(sensorMap.getFirstObservation());
        Assert.assertEquals(listener, sensorMap.getObservationListener());
        Assert.assertEquals(selectionList, sensorMap.getSensorSelections());
    }
}
