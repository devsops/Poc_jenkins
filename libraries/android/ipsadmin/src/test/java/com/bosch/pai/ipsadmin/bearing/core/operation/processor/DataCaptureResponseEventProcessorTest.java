package com.bosch.pai.ipsadmin.bearing.core.operation.processor;


import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.event.DataCaptureResponseEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataCaptureResponseEventProcessor.class})
public class DataCaptureResponseEventProcessorTest {

    @Mock
    private RequestDataHolder requestDataHolder;

    @Mock
    private ObservationHandlerAndListener observationHandlerAndListener;
    @Mock
    private BearingCallBack sender;
    @Mock
    private DataCaptureResponseEvent event;

    @Before
    public void init() {
        PowerMockito.when(requestDataHolder.getObservationHandlerAndListener()).thenReturn(observationHandlerAndListener);
    }

    @Test
    public void testConstructorAndRunInvoke() {
        final DataCaptureResponseEventProcessor dataCaptureResponseEventProcessor = new DataCaptureResponseEventProcessor(UUID.randomUUID().toString(), event, sender, requestDataHolder);
        dataCaptureResponseEventProcessor.run();
        Mockito.verify(observationHandlerAndListener, Mockito.times(1))
                .onSensorDataReceived(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyListOf(SnapshotObservation.class));
    }

}
