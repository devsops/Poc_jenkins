package com.bosch.pai.ipsadmin.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RequestReadEventTest {

    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    @Mock
    private Sender sender;
    @Mock
    private EventSender eventSender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";
        final RequestReadEvent.ServerFetch serverFetch = RequestReadEvent.ServerFetch.ALL_LOCATIONS_FROM_SERVER;
        final RequestReadEvent requestReadEvent1 = new RequestReadEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(requestReadEvent1);

        requestReadEvent1.setFetchRequest(serverFetch);
        Assert.assertEquals(serverFetch, requestReadEvent1.getFetchRequest());

    }

}
