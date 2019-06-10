package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs;

import android.content.Context;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.RequestResponseHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.wifi.WifiObserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourceDataManager.class, ConfigurationSettings.class, ResourceStateManager.class,
        RequestResponseHandler.class, Sensor.class, Property.class, ConfigurationSettings.ActiveMode.class, Context.class,
        LogAndToastUtil.class, WifiObserver.class})
public class ResourceDataManagerTest {
    @Mock
    private RequestResponseHandler requestResponseHandler;
    @Mock
    private ResourceStateManager resourceStateManager;
    @Mock
    private SensorObservationHandler sensorObservationHandler;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private Sensor sensor;
    @Mock
    private Context context;
    @Mock
    private WifiObserver wifiObserver;
    @Mock
    private LogAndToastUtil logAndToastUtil;

    private ResourceDataManager resourceDataManager;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Sensor.class);
        PowerMockito.mockStatic(Property.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mockStatic(ResourceStateManager.class);
        PowerMockito.mockStatic(RequestResponseHandler.class);
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(WifiObserver.class);
        PowerMockito.mockStatic(ConfigurationSettings.ActiveMode.class);
        PowerMockito.when(ConfigurationSettings.saveConfigObject(Mockito.any(ConfigurationSettings.class))).thenReturn(true);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class), Mockito.any()))
                .thenReturn(sensor);
        PowerMockito.when(sensor.getProperty(Property.Sensor.WIFI_ACTIVE_MODE)).thenReturn("RESPONSIVE");
        PowerMockito.when(sensor.getProperty(Property.Sensor.BLE_ACTIVE_MODE)).thenReturn("RESPONSIVE");
        PowerMockito.when(sensor.getProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL)).thenReturn(Double.valueOf("4000"));
        PowerMockito.when(sensor.getProperty(Property.Sensor.BLE_ACTIVE_MODE_INTERVAL)).thenReturn(Double.valueOf("4000"));
        resourceDataManager = new ResourceDataManager(requestResponseHandler, resourceStateManager, sensorObservationHandler);
    }

    @Test
    public void modifySensorConfigurationTest() {
        //resourceDataManager.modifySensorConfiguration(BearingConfiguration.SensorType.ST_WIFI,true,5654);
    }

    @Test
    public void setUpSensorTest() {
        //Assert.assertFalse(resourceDataManager.setUpSensor(context, BearingConfiguration.SensorType.ST_WIFI));
    }

    @Test
    public void getUpdatedDataValuesTest() {
        Assert.assertNull(resourceDataManager.getUpdatedDataValues(BearingConfiguration.SensorType.ST_WIFI));
    }

    @Test
    public void onResponseReceivedTest(){
        List<SnapshotObservation> snapshotObservations = new ArrayList<>();
        SnapshotObservation snapshotObservation = new SnapshotObservation();
        SnapshotObservation snapshotObservation1 = new SnapshotObservation();
        SnapshotObservation snapshotObservation2 = new SnapshotObservation();
        SnapshotObservation snapshotObservation3 = new SnapshotObservation();
        SnapshotObservation snapshotObservation4 = new SnapshotObservation();
        SnapshotObservation snapshotObservation5 = new SnapshotObservation();
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        snapshotObservation1.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        snapshotObservation2.setSensorType(BearingConfiguration.SensorType.ST_GEOFENCE);
        snapshotObservation3.setSensorType(BearingConfiguration.SensorType.ST_GPS);
        snapshotObservation4.setSensorType(BearingConfiguration.SensorType.ST_IMU);
        snapshotObservation5.setSensorType(BearingConfiguration.SensorType.ST_MAGNETO);
        snapshotObservations.add(snapshotObservation);
        snapshotObservations.add(snapshotObservation1);
        snapshotObservations.add(snapshotObservation2);
        snapshotObservations.add(snapshotObservation3);
        snapshotObservations.add(snapshotObservation4);
        snapshotObservations.add(snapshotObservation5);
        resourceDataManager.onResponseReceived(snapshotObservations);
        Mockito.verify(logAndToastUtil, Mockito.atLeastOnce()).addLogs(Mockito.any(LogAndToastUtil.LOG_STATUS.class), Mockito.anyString(), Mockito.anyString());
    }
}
