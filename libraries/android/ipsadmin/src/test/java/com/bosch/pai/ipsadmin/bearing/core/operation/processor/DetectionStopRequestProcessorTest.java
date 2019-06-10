package com.bosch.pai.ipsadmin.bearing.core.operation.processor;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestDetectionStopEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;

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
@PrepareForTest({DetectionStopRequestProcessor.class, BearingHandler.class})
public class DetectionStopRequestProcessorTest {

    @Mock
    private BearingCallBack bearingCallBack;

    @Mock
    private ObservationHandlerAndListener observationHandlerAndListener;
    @Mock
    private RequestDataHolder data1;

    @Before
    public void init() {
        PowerMockito.mockStatic(BearingHandler.class);
        final Map<String, RequestDataHolder> map = new HashMap<>();
        map.put(UUID.randomUUID().toString(), data1);

        PowerMockito.when(BearingHandler.getUuidToRequestDataHolderMap()).thenReturn(map);
        PowerMockito.when(data1.getObservationDataType()).thenReturn(RequestDataHolder.ObservationDataType.SITE_DETECTION)
                .thenReturn(RequestDataHolder.ObservationDataType.LOCATION_DETECTION);
        PowerMockito.when(data1.getObservationHandlerAndListener()).thenReturn(observationHandlerAndListener);
    }

    @Test
    public void testConstructorAndRunInvoke() {
        final RequestDetectionStopEvent requestDetectionStopEvent = new RequestDetectionStopEvent(UUID.randomUUID().toString(), EventType.STOP_DETECTION, bearingCallBack);
        requestDetectionStopEvent.setSite(true);
        final DetectionStopRequestProcessor processor = new DetectionStopRequestProcessor(UUID.randomUUID().toString(), requestDetectionStopEvent, bearingCallBack);
        processor.run();
        requestDetectionStopEvent.setSite(false);
        processor.run();
        Mockito.verify(data1, Mockito.times(2))
                .getObservationHandlerAndListener();
        Mockito.verify(observationHandlerAndListener, Mockito.times(2))
                .removeAndUnregisterSensorObservation(Mockito.any(BearingConfiguration.Approach.class));
    }
}
