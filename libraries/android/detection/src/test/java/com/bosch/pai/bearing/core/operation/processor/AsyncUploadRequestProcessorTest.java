package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.event.RequestUploadEvent;
import com.bosch.pai.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;

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
@PrepareForTest({BearingResponseAggregator.class, AsyncUploadRequestProcessor.class})
public class AsyncUploadRequestProcessorTest {

    @Mock
    private BearingCallBack sender;
    @Mock
    private RequestUploadEvent event;

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
        PowerMockito.doNothing().when(bearingOperations, "readDataWithAsynchronousResponse", Mockito.any(RequestUploadEvent.class));
    }

    @Test
    public void testConstructorAndRunInvoke() {
        AsyncUploadRequestProcessor asyncUploadRequestProcessor = new AsyncUploadRequestProcessor(UUID.randomUUID().toString(), event, sender);
        Assert.assertNotNull(asyncUploadRequestProcessor);
        asyncUploadRequestProcessor.run();
        Mockito.verify(bearingResponseAggregator, Mockito.times(1))
                .updateReadResponseAggregatorMap(Mockito.any(UUID.class), Mockito.any(BearingCallBack.class));
        Mockito.verify(bearingOperations, Mockito.times(1)).uploadDataAsynchronousResponseServerCall(Mockito.any(RequestUploadEvent.class));

    }
}
