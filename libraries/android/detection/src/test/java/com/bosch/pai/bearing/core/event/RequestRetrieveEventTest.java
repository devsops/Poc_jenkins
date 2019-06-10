package com.bosch.pai.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RequestRetrieveEventTest {

    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    @Mock
    private Sender sender;
    @Mock
    private EventSender eventSender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";
        final RequestRetrieveEvent.ServerFetch serverFetch = RequestRetrieveEvent.ServerFetch.ALL_LOCATION_NAMES_FROM_SERVER;
        final RequestRetrieveEvent requestRetrieveEvent = new RequestRetrieveEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(requestRetrieveEvent);

        requestRetrieveEvent.setFetchRequest(serverFetch);
        Assert.assertEquals(serverFetch, requestRetrieveEvent.getFetchRequest());

    }
}
