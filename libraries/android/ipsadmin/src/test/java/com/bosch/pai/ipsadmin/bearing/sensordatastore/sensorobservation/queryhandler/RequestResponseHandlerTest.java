
package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestResponseHandler.class})
public class RequestResponseHandlerTest {
    private RequestResponseHandler requestResponseHandler;
    private UUID uuid;
    private SensorObservationListener sensorObservationListener;
    private ListenerActiveSensorMap listenerActiveSensorMap;

    @Mock
    private ResourceDataManager resourceDataManager;

    @Before
    public void init() {
        requestResponseHandler = new RequestResponseHandler();
        uuid = new UUID(0, 0);
        sensorObservationListener = new SensorObservationListener() {
            @Override
            public void onObservationReceived(List<SnapshotObservation> snapshotObservations) {

            }

            @Override
            public void onSourceUnavailable(BearingConfiguration.SensorType obsSource, String message) {

            }

            @Override
            public void onSourceAdded(BearingConfiguration.SensorType sensorType) {

            }
        };
        listenerActiveSensorMap = new ListenerActiveSensorMap();
        listenerActiveSensorMap.setFirstObservation(true);
        listenerActiveSensorMap.setObservationListener(sensorObservationListener);
        requestResponseHandler.registerListener(resourceDataManager);
    }

    @Test
    public void createSensorRequestToListenerMapNullTest() {
        final boolean sensorRequestToListenerMapTest = requestResponseHandler.createSensorRequestToListenerMap(null, null);
        Assert.assertFalse(sensorRequestToListenerMapTest);

    }

    @Test
    public void createSensorRequestToListenerMapEmptyTest() {
        final boolean sensorRequestToListenerMapTest = requestResponseHandler.createSensorRequestToListenerMap(uuid, sensorObservationListener);
        Assert.assertTrue(sensorRequestToListenerMapTest);

    }

    @Test
    public void updateSensorRequestToListenerMapTest() {
        final boolean results = requestResponseHandler.updateSensorRequestToListenerMap(UUID.randomUUID(), BearingConfiguration.SensorType.ST_WIFI);
        Assert.assertFalse(results);

    }

    @Test
    public void updateActiveModeforSensorNullTest() {
        requestResponseHandler.updateSensorRequestToListenerMap(uuid, BearingConfiguration.SensorType.ST_BLE);
        Assert.assertFalse(requestResponseHandler.updateActiveModeforSensor(null, null, true));

    }

    @Test
    public void updateActiveModeSensorCheckTest() {
        requestResponseHandler.updateSensorRequestToListenerMap(uuid, BearingConfiguration.SensorType.ST_BLE);
        Assert.assertFalse(requestResponseHandler.updateActiveModeforSensor(uuid, BearingConfiguration.SensorType.ST_WIFI, true));

    }

    @Test
    public void removeSensorFromRequestToListenerMapNullTest() {
        Assert.assertFalse(requestResponseHandler.removeSensorFromRequestToListenerMap(null, null));
    }

    @Test
    public void removeSensorFromRequestToListenerMapWithoutSensorTest() {
        Assert.assertFalse(requestResponseHandler.removeSensorFromRequestToListenerMap(uuid, BearingConfiguration.SensorType.ST_BLE));

    }

    @Test
    public void removeSensorFromRequestToListenerMapCheckTest() {
        requestResponseHandler.updateSensorRequestToListenerMap(uuid, BearingConfiguration.SensorType.ST_BLE);
        Assert.assertFalse(requestResponseHandler.removeSensorFromRequestToListenerMap(uuid, BearingConfiguration.SensorType.ST_WIFI));

    }

    @Test
    public void deleteSensorRequestToListenerMapNullTest() {
        Assert.assertFalse(requestResponseHandler.deleteSensorRequestToListenerMap(null));
    }

    @Test
    public void deleteSensorRequestToListenerMapCheckTest() {
        UUID newUuid = new UUID(0, 2);
        Assert.assertFalse(requestResponseHandler.deleteSensorRequestToListenerMap(newUuid));
    }


    @Test
    public void notifyObservationAndUpdateNullTest() {
        requestResponseHandler.notifyObservationAndUpdate(null);
        Assert.assertTrue(true);

    }

    @Test
    public void notifyObservationAndUpdateCheckTest() {
        requestResponseHandler.updateSensorRequestToListenerMap(uuid, BearingConfiguration.SensorType.ST_BLE);
        requestResponseHandler.notifyObservationAndUpdate(BearingConfiguration.SensorType.ST_WIFI);
        Assert.assertTrue(true);
    }
}

