package com.bosch.pai.ipsadmin.bearing.core.operation.processor;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.event.ThreshDataEntryEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;

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
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreshDataEntryEventProcessor.class, AlgorithmLifeCycleHandler.class, BearingResponseAggregator.class})
public class ThreshDataEntryEventProcessorTest {

    @Mock
    private BearingResponseAggregator bearingResponseAggregator;

    @Mock
    private AlgorithmLifeCycleHandler algorithmLifeCycleHandler;

    @Mock
    private BearingCallBack bearingCallBack;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(BearingResponseAggregator.class);
        PowerMockito.when(BearingResponseAggregator.getInstance()).thenReturn(bearingResponseAggregator);
        PowerMockito.mockStatic(AlgorithmLifeCycleHandler.class);
        PowerMockito.when(AlgorithmLifeCycleHandler.getInstance()).thenReturn(algorithmLifeCycleHandler);
    }

    @Test
    public void testConstructorAndRunInvoke() {
        final ThreshDataEntryEvent event = new ThreshDataEntryEvent(UUID.randomUUID().toString(),
                EventType.THRESH_DATA_ENTRY, bearingCallBack);
        final List<BearingConfiguration.SensorType> list = new ArrayList<>();
        list.add(BearingConfiguration.SensorType.ST_BLE);
        event.setSensors(list);
        final ThreshDataEntryEventProcessor processor = new ThreshDataEntryEventProcessor(event.getRequestID(), event, bearingCallBack);
        processor.run();
        Mockito.verify(bearingResponseAggregator, Mockito.times(1))
                .updateTrainingResponseAggregatorMap(Mockito.any(UUID.class), Mockito.any(BearingConfiguration.Approach.class), Mockito.any(BearingCallBack.class));
        Mockito.verify(algorithmLifeCycleHandler, Mockito.times(1))
                .enqueue(Mockito.anyString(), Mockito.any(ThreshDataEntryEvent.class),
                        Mockito.any(EventType.class), Mockito.anyString());
    }

}
