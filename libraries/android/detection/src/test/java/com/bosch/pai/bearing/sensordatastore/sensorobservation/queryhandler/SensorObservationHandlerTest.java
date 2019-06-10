package com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceStateManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SensorObservationHandler.class, Handler.class, ResourceDataManager.class, ResourceStateManager.class, Looper.class})
public class SensorObservationHandlerTest extends Handler {

    @Mock
    private ResourceDataManager resourceDataManager;
    @Mock
    private ResourceStateManager resourceStateManager;
    @Mock
    private Looper looper;
    @Mock
    private SensorObservationHandler sensorObservationHandler;
    @Mock
    private Message message;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Looper.class);
        sensorObservationHandler = new SensorObservationHandler(looper);
    }

    @Test
    public void testFunc(){
        sensorObservationHandler.registerResourceManagers(resourceDataManager,resourceStateManager);
        sensorObservationHandler.handleMessage(message);
    }
}
