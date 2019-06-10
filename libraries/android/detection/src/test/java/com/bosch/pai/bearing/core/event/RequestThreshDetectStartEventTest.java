package com.bosch.pai.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RequestThreshDetectStartEventTest {

    @Mock
    private Sender sender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";
        final EventType EVENT_TYPE = EventType.STOP_THRESH_DETECTION;
        final RequestThreshDetectStartEvent event = new RequestThreshDetectStartEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(event);
    }
}
