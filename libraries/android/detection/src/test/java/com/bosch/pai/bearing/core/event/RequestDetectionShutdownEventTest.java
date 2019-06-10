package com.bosch.pai.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;

public class RequestDetectionShutdownEventTest {

    @Mock
    private Sender sender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";
        final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
        final RequestDetectionShutdownEvent event = new RequestDetectionShutdownEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(event);
    }
}
