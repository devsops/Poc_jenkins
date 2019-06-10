package com.bosch.pai.bearing.core.event;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class RequestDetectionStartEventTest {

    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    private final BearingConfiguration.Approach APPROACH = BearingConfiguration.Approach.FINGERPRINT;
    @Mock
    private Sender sender;
    @Mock
    private EventSender eventSender;

    private List<String> locationName = new ArrayList<>();

    @Test
    public void testConstructor() {
        String REQUEST_ID = "REQUEST_ID";
        final RequestDetectionStartEvent requestDetectionStartEvent1 = new RequestDetectionStartEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(requestDetectionStartEvent1);
        String SITE_NAME = "SITE_NAME";
        final RequestDetectionStartEvent requestDetectionStartEvent2 = new RequestDetectionStartEvent(REQUEST_ID, EVENT_TYPE, sender, SITE_NAME);
        Assert.assertNotNull(requestDetectionStartEvent2);
        final RequestDetectionStartEvent requestDetectionStartEvent3 = new RequestDetectionStartEvent(REQUEST_ID, EVENT_TYPE, sender, locationName, SITE_NAME);
        Assert.assertNotNull(requestDetectionStartEvent3);

        requestDetectionStartEvent3.setSite(true);
        Assert.assertTrue(requestDetectionStartEvent3.isSite());


    }
}
