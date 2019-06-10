
package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorInfo;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourceStateManager.class, LogAndToastUtil.class})
public class ResourceStateManagerTest {

    @Mock
    private SensorObservationHandler sensorObservationHandlerMock;
    @Mock
    private ResourceDataManager resourceDataManager;
    @Mock
    private LogAndToastUtil logAndToastUtil;

    private List<SensorInfo> availableSensorsList = new ArrayList<>();


    private ResourceStateManager resourceStateManager;

    @Before
    public void init() {
        PowerMockito.mockStatic(LogAndToastUtil.class);
        resourceStateManager = new ResourceStateManager(sensorObservationHandlerMock);
        resourceStateManager.registerListener(resourceDataManager,availableSensorsList);
        Assert.assertEquals(availableSensorsList,resourceStateManager.getAvailableSensorsList());
    }

    @Test
    public void createSensorStateTest(){
        Assert.assertFalse(resourceStateManager.createSensorState(null));
        Assert.assertTrue(resourceStateManager.createSensorState(BearingConfiguration.SensorType.ST_WIFI));
    }

    @Test
    public void notifySensorStateOnStopScanTest(){
        resourceStateManager.notifySensorStateOnStopScan(BearingConfiguration.SensorType.ST_WIFI);
        Mockito.verify(logAndToastUtil,Mockito.atLeastOnce()).addLogs(Mockito.any(LogAndToastUtil.LOG_STATUS.class),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void updateSensorStateOnActiveModeEnableTest(){
        Assert.assertFalse(resourceStateManager.updateSensorStateOnActiveModeEnable(UUID.randomUUID(), BearingConfiguration.SensorType.ST_WIFI));
        Assert.assertTrue(resourceStateManager.updateSensorStateOnActiveModeDisable(UUID.randomUUID(), BearingConfiguration.SensorType.ST_WIFI));
    }

    @Test
    public void getResourceStateMapTest(){
        Map<BearingConfiguration.SensorType, SensorState> sensorTypeSensorStateMap = new HashMap<>();
        Assert.assertEquals(sensorTypeSensorStateMap,resourceStateManager.getResourceStateMap());
    }

}

