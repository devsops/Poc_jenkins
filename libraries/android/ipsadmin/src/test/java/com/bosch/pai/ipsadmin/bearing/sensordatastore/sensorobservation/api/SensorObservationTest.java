
package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api;

import android.content.Context;
import android.os.Looper;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.RequestResponseHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceStateManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PrepareForTest({SensorObservation.class, ResourceStateManager.class, ResourceDataManager.class, RequestResponseHandler.class, ResourceStateManager.class, SensorUtil.class,
        LogAndToastUtil.class, Context.class})
public class SensorObservationTest {

    @Mock
    private SensorObservationListener listener;
    @Mock
    private RequestResponseHandler requestResponseHandler;
    @Mock
    private ResourceStateManager resourceStateManager;
    @Mock
    private ResourceDataManager resourceDataManager;
    @Mock
    private SensorObservationHandler sensorObservationHandler;
    @Mock
    private Looper looper;
    @Mock
    Context context;
    @Mock
    private LogAndToastUtil logAndToastUtil;

    private SensorObservation sensorObservationInstance;
    private boolean result;
    private UUID uuid;
    private List<BearingConfiguration.SensorType> sensorTypes;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(SensorUtil.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(Context.class);
        PowerMockito.whenNew(RequestResponseHandler.class).withNoArguments().thenReturn(requestResponseHandler);
        PowerMockito.whenNew(SensorObservationHandler.class).withArguments(Mockito.any(Looper.class))
                .thenReturn(sensorObservationHandler);
        PowerMockito.whenNew(ResourceStateManager.class).withArguments(Mockito.any(SensorObservationHandler.class))
                .thenReturn(resourceStateManager);
        PowerMockito.when(resourceStateManager.updateSensorStateOnActiveModeEnable(Mockito.any(UUID.class),
                Mockito.any(BearingConfiguration.SensorType.class))).thenReturn(true);
        PowerMockito.whenNew(ResourceDataManager.class).withArguments(Mockito.any(RequestResponseHandler.class), Mockito.any(ResourceStateManager.class), Mockito.any(SensorObservationHandler.class))
                .thenReturn(resourceDataManager);
        PowerMockito.when(SensorUtil.checkAreWIFISensorsEnabled(Mockito.any(Context.class)))
                .thenReturn(true);
        PowerMockito.when(SensorUtil.checkAreBLESenorsEnabled(Mockito.any(Context.class)))
                .thenReturn(true);
        PowerMockito.when(requestResponseHandler.updateSensorRequestToListenerMap(Mockito.any(UUID.class), Mockito.any(BearingConfiguration.SensorType.class)))
                .thenReturn(true);
        PowerMockito.when(requestResponseHandler.removeSensorFromRequestToListenerMap(Mockito.any(UUID.class), Mockito.any(BearingConfiguration.SensorType.class)))
                .thenReturn(true);
        PowerMockito.when(requestResponseHandler.createSensorRequestToListenerMap(Mockito.any(UUID.class),
                Mockito.any(SensorObservationListener.class))).thenReturn(true);
        PowerMockito.when(resourceDataManager.setUpSensor(Mockito.any(Context.class), Mockito.any(BearingConfiguration.SensorType.class)))
                .thenReturn(true);
        SensorObservation.init(looper);
        sensorObservationInstance = SensorObservation.getInstance();
        sensorTypes = new ArrayList<>();
        uuid = UUID.randomUUID();
        sensorTypes.add(BearingConfiguration.SensorType.ST_WIFI);
        result = sensorObservationInstance.addSource(uuid, sensorTypes);
    }

    @Test
    public void test1() {
        Assert.assertNotNull(sensorObservationInstance.registerListener(listener));
        /*Mockito.verify(requestResponseHandler)
                .createSensorRequestToListenerMap(Mockito.any(UUID.class), Mockito.any(SensorObservationListener.class));*/
    }

    @Test
    public void test2() {
        Assert.assertTrue(result);
    }

    @Test
    public void getAndSetContextTest(){
        sensorObservationInstance.setContext(context);
        Assert.assertEquals(context,sensorObservationInstance.getContext());
    }

    @Test
    public void removeSourceTest(){
        sensorObservationInstance.removeSource(UUID.randomUUID(), sensorTypes);
        Mockito.verify(logAndToastUtil,Mockito.atLeastOnce()).addLogs(Mockito.any(LogAndToastUtil.LOG_STATUS.class), Mockito.anyString(),Mockito.anyString());
    }

    @Test
    public void accessSensorStateToEnableActiveModeTest(){
        Assert.assertFalse(sensorObservationInstance.accessSensorStateToEnableActiveMode(sensorTypes));
    }

    /*@Test
    public void test3() {
        sensorObservationInstance.enableActiveMode(uuid, sensorTypes);
        Mockito.verify(requestResponseHandler)
                .updateActiveModeforSensor(Mockito.any(UUID.class), Mockito.any(BearingConfiguration.SensorType.class), Mockito.anyBoolean());
    }

    @Test
    public void test4() {
        sensorObservationInstance.removeSource(uuid, sensorTypes);
        Mockito.verify(requestResponseHandler)
                .removeSensorFromRequestToListenerMap(Mockito.any(UUID.class), Mockito.any(BearingConfiguration.SensorType.class));
        Mockito.verify(resourceStateManager)
                .destroyState(Mockito.any(BearingConfiguration.SensorType.class));
    }*/
}

