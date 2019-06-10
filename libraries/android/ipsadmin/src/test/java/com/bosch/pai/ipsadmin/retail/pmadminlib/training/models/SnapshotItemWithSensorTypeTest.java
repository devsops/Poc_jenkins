package com.bosch.pai.ipsadmin.retail.pmadminlib.training.models;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SnapshotItemWithSensorTypeTest {

    @Mock
    SnapshotItem snapshotItem;

    private SnapshotItemWithSensorType snapshotItemWithSensorType = new SnapshotItemWithSensorType(BearingConfiguration.SensorType.ST_WIFI, snapshotItem);

    @Test
    public void tgetSnapshotItemWithSensorTypeTest(){
        Assert.assertEquals(BearingConfiguration.SensorType.ST_WIFI, snapshotItemWithSensorType.getSensorType());
        Assert.assertNull(snapshotItemWithSensorType.getSnapshotItem());
    }
}
