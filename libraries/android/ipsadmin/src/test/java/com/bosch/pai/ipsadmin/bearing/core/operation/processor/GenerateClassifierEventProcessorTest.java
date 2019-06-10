package com.bosch.pai.ipsadmin.bearing.core.operation.processor;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.event.GenerateClassifierEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.ipsadmin.bearing.core.operation.readoperations.BearingOperations;

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
@PrepareForTest({BearingResponseAggregator.class, GenerateClassifierEventProcessor.class, BearingOperations.class})
public class GenerateClassifierEventProcessorTest {

    @Mock
    private BearingResponseAggregator bearingResponseAggregator;

    @Mock
    private BearingOperations bearingOperations;
    @Mock
    private BearingCallBack bearingCallBack;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(BearingResponseAggregator.class);
        PowerMockito.when(BearingResponseAggregator.getInstance()).thenReturn(bearingResponseAggregator);
        PowerMockito.doNothing().when(bearingResponseAggregator, "updateReadResponseAggregatorMap",
                Mockito.any(UUID.class), Mockito.any(BearingCallBack.class));
        PowerMockito.whenNew(BearingOperations.class).withNoArguments().thenReturn(bearingOperations);
        PowerMockito.doNothing().when(bearingOperations, "triggerTrainingOnServer", Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class));
    }

    @Test
    public void testConstructorAndRunInvoke() {
        final GenerateClassifierEvent event = new GenerateClassifierEvent(UUID.randomUUID().toString(),
                EventType.TRIGGER_TRAINING, BearingConfiguration.Approach.FINGERPRINT, bearingCallBack);
        event.setGenerateOnServer(true);
        final GenerateClassifierEventProcessor processor = new GenerateClassifierEventProcessor(event.getRequestID(), event, bearingCallBack);
        processor.run();
        Mockito.verify(bearingOperations, Mockito.times(1))
                .triggerTrainingOnServer(Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class));
    }
}
