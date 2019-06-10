package com.bosch.pai.ipsadmin.bearing.core.event;

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

@RunWith(PowerMockRunner.class)
public class RequestDetectionStopEventTest {
    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    private final BearingConfiguration.Approach APPROACH = BearingConfiguration.Approach.FINGERPRINT;
    @Mock
    private Sender sender;
    @Mock
    private EventSender eventSender;

    @Test
    public void testConstructor() {
        String REQUEST_ID = "REQUEST_ID";
        final RequestDetectionStopEvent requestDetectionStopEvent1 = new RequestDetectionStopEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(requestDetectionStopEvent1);
        String SITE_NAME = "SITE_NAME";
        final RequestDetectionStopEvent requestDetectionStopEvent2 = new RequestDetectionStopEvent(REQUEST_ID, EVENT_TYPE, sender, SITE_NAME);
        Assert.assertNotNull(requestDetectionStopEvent2);
        final RequestDetectionStopEvent requestDetectionStopEvent3 = new RequestDetectionStopEvent(REQUEST_ID, EVENT_TYPE, sender, new ArrayList<>(), SITE_NAME);
        Assert.assertNotNull(requestDetectionStopEvent3);

        requestDetectionStopEvent3.setSite(true);
        Assert.assertTrue(requestDetectionStopEvent3.isSite());


    }
}
