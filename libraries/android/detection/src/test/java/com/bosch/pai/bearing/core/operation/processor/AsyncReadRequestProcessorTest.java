package com.bosch.pai.bearing.core.operation.processor;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.event.RequestReadEvent;
import com.bosch.pai.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
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
@PrepareForTest({BearingResponseAggregator.class, AsyncReadRequestProcessor.class})
public class AsyncReadRequestProcessorTest {

    @Mock
    private BearingCallBack sender;
    @Mock
    private RequestReadEvent event;

    @Mock
    private BearingResponseAggregator bearingResponseAggregator;
    @Mock
    private BearingOperations bearingOperations;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(BearingResponseAggregator.class);
        PowerMockito.when(BearingResponseAggregator.getInstance()).thenReturn(bearingResponseAggregator);
        PowerMockito.doNothing().when(bearingResponseAggregator, "updateReadResponseAggregatorMap",
                Mockito.any(UUID.class), Mockito.any(BearingCallBack.class));
        PowerMockito.whenNew(BearingOperations.class).withNoArguments().thenReturn(bearingOperations);
        PowerMockito.doNothing().when(bearingOperations, "readDataWithAsynchronousResponse", Mockito.any(RequestReadEvent.class));
    }

    @Test
    public void testConstructorAndRunInvoke() {
        AsyncReadRequestProcessor asyncReadRequestProcessor = new AsyncReadRequestProcessor(UUID.randomUUID().toString(), event, sender);
        Assert.assertNotNull(asyncReadRequestProcessor);
        asyncReadRequestProcessor.run();
        Mockito.verify(bearingResponseAggregator, Mockito.times(1))
                .updateReadResponseAggregatorMap(Mockito.any(UUID.class), Mockito.any(BearingCallBack.class));
        Mockito.verify(bearingOperations, Mockito.times(1)).readDataWithAsynchronousResponse(Mockito.any(RequestReadEvent.class));

    }
}
