package com.bosch.pai.bearing.core.operation.processor;

import android.util.Log;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.SensorUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ShutDownDetectionProcessor.class, BearingHandler.class, SensorUtil.class, LogAndToastUtil.class, Log.class})
public class ShutDownDetectionProcessorTest {
    @Mock
    private BearingCallBack bearingCallBack;

    @Mock
    private ObservationHandlerAndListener observationHandlerAndListener;
    @Mock
    private RequestDataHolder data1;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(BearingHandler.class);
        PowerMockito.mockStatic(SensorUtil.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(Log.class);
        final Map<String, RequestDataHolder> map = new HashMap<>();
        map.put(UUID.randomUUID().toString(), data1);

        PowerMockito.when(BearingHandler.getUuidToRequestDataHolderMap()).thenReturn(map);
        PowerMockito.when(data1.getObservationHandlerAndListener()).thenReturn(observationHandlerAndListener);

    }

    @Test
    public void testConstructorAndRunInvoke() {
        final ShutDownDetectionProcessor processor = new ShutDownDetectionProcessor(UUID.randomUUID().toString(), null, bearingCallBack);
        processor.run();
        Mockito.verify(observationHandlerAndListener, Mockito.times(1))
                .removeAndUnregisterSensorObservation(Mockito.any(BearingConfiguration.Approach.class));
        PowerMockito.verifyStatic(Mockito.times(1));
        SensorUtil.setShutdown(Mockito.anyBoolean());
    }
}
