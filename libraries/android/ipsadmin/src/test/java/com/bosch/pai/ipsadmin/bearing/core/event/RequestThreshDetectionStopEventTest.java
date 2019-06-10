package com.bosch.pai.ipsadmin.bearing.core.event;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class RequestThreshDetectionStopEventTest {
    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    private final BearingConfiguration.Approach APPROACH = BearingConfiguration.Approach.FINGERPRINT;
    @Mock
    private Sender sender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";

        final RequestThreshDetectionStopEvent event1 = new RequestThreshDetectionStopEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(event1);

        final String SITE_NAME = "SITE_NAME";
        final RequestThreshDetectionStopEvent event2 = new RequestThreshDetectionStopEvent(REQUEST_ID, EVENT_TYPE, sender, SITE_NAME);
        Assert.assertNotNull(event2);

        final RequestThreshDetectionStopEvent event3 = new RequestThreshDetectionStopEvent(REQUEST_ID, EVENT_TYPE, sender, new ArrayList<>(), SITE_NAME);
        Assert.assertNotNull(event3);


    }

}
