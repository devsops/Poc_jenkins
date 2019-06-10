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
public class RequestUpdateEventTest {

    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    private final BearingConfiguration.Approach APPROACH = BearingConfiguration.Approach.FINGERPRINT;
    @Mock
    private Sender sender;
    @Mock
    private EventSender eventSender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";
        final String SITE_NAME_NEW = "SITE_NAME_NEW";
        final String SITE_NAME_OLD = "SITE_NAME_OLD";
        final RequestUpdateEvent event1 = new RequestUpdateEvent(REQUEST_ID, EVENT_TYPE, sender);
        Assert.assertNotNull(event1);
        event1.setFetchRequest(RequestUpdateEvent.ServerFetch.SIGNAL_MERGE_LOCAL);
        event1.setSiteNameNew(SITE_NAME_NEW);
        event1.setSnapshotObservations(new ArrayList<>());
        event1.setSiteNameOld(SITE_NAME_OLD);

        Assert.assertEquals(SITE_NAME_NEW, event1.getSiteNameNew());
        Assert.assertEquals(SITE_NAME_OLD, event1.getSiteNameOld());
        Assert.assertEquals(RequestUpdateEvent.ServerFetch.SIGNAL_MERGE_LOCAL, event1.getFetchRequest());
        Assert.assertTrue(event1.getSnapshotObservations().isEmpty());
    }
}
