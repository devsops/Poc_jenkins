package com.bosch.pai.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RequestUploadEventTest {

    @Mock
    private Sender sender;

    @Test
    public void testConstructor() {
        final EventType EVENT_TYPE = EventType.ASYNC_UPLOAD;
        final String REQUEST_ID = "REQUEST_ID";
        final String LOCATION_NAME = "LOCATION_NAME";
        final RequestUploadEvent event = new RequestUploadEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(event);
        event.setFetchRequest(RequestUploadEvent.ServerFetch.LOCATION_UPLOAD);
        event.setLocationName(LOCATION_NAME);
        Assert.assertEquals(RequestUploadEvent.ServerFetch.LOCATION_UPLOAD, event.getFetchRequest());
        Assert.assertEquals(LOCATION_NAME, event.getLocationName());
    }
}
