package com.bosch.pai.ipsadmin.bearing.detect.operations;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class ValidationTest {
    Validation validation;
    BearingConfiguration bearingConfiguration;
    List<BearingConfiguration.SensorType> bearingSensors;
    BearingConfiguration.Approach approach;
    BearingConfiguration.SensorType sensorType;
    Map<BearingConfiguration.Approach,List<BearingConfiguration.SensorType>> map;


    @Before
    public void testBefore()
    {
        MockitoAnnotations.initMocks(this);
        validation = new Validation();
        sensorType = BearingConfiguration.SensorType.ST_WIFI;
        approach = BearingConfiguration.Approach.FINGERPRINT;
        bearingSensors = new ArrayList<>();
        bearingSensors.add(sensorType);
        map = new HashMap<BearingConfiguration.Approach,List<BearingConfiguration.SensorType>>();
        map.put(approach,bearingSensors);
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,map);
    }

    @Test
    public void configurationRequestFingerPrintTest() throws Exception {
        boolean bb = validation.isValidConfigurationRequest(bearingConfiguration);
       // assertNotNull(bb);
       // PowerMockito.verifyPrivate(Validation.class);
        assertTrue(bb);

    }

    @Test
    public void configurationRequestThreshHoldingTest() throws Exception {

        sensorType = BearingConfiguration.SensorType.ST_BLE;
        approach = BearingConfiguration.Approach.THRESHOLDING;
        bearingSensors = new ArrayList<>();
        bearingSensors.add(sensorType);
        map = new HashMap<BearingConfiguration.Approach,List<BearingConfiguration.SensorType>>();
        map.put(approach,bearingSensors);
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,map);
        boolean bb = validation.isValidConfigurationRequest(bearingConfiguration);
        // assertNotNull(bb);
        //PowerMockito.verifyPrivate(Validation.class);
        assertTrue(bb);

    }




}
