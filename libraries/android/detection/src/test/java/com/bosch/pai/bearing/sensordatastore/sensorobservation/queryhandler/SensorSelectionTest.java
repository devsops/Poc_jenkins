package com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SensorSelection.class})
public class SensorSelectionTest {

    @Test
    public void testFunc() {
        SensorSelection sensorSelection = new SensorSelection();
        sensorSelection.setActiveMode(true);
        sensorSelection.setObservationReceived(true);
        sensorSelection.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        Assert.assertTrue(sensorSelection.getActiveMode());
        Assert.assertTrue(sensorSelection.getObservationReceived());
        Assert.assertEquals(BearingConfiguration.SensorType.ST_WIFI,sensorSelection.getSensorType());
    }
}
