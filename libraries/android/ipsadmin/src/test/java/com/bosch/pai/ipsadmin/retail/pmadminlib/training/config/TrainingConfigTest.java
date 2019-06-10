package com.bosch.pai.ipsadmin.retail.pmadminlib.training.config;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TrainingConfig.class})
public class TrainingConfigTest {

    private TrainingConfig trainingConfig;

    private List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
    private List<BearingConfiguration.SensorType> sensorTypess = new ArrayList<>();

    @Before
    public void init() throws Exception {
        trainingConfig = new TrainingConfig();
        sensorTypess.add(BearingConfiguration.SensorType.ST_BLE);
        sensorTypes.add(BearingConfiguration.SensorType.ST_WIFI);
    }

    @Test
    public void getBleSensorTypesTest(){
        Assert.assertEquals(sensorTypess, trainingConfig.getBleSensorTypes());
    }

    @Test
    public void getWifiSensorTypesTest(){
        Assert.assertEquals(sensorTypes, trainingConfig.getWifiSensorTypes());
    }
}
