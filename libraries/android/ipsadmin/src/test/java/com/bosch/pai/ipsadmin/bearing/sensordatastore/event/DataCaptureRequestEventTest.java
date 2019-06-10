package com.bosch.pai.ipsadmin.bearing.sensordatastore.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.core.BearingCallBack;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataCaptureRequestEvent.class})
public class DataCaptureRequestEventTest {
    @Mock
    private BearingCallBack sender;

    private List<String> locations = new ArrayList<>();

    @Test
    public void testSetterAndGetter() {
        final DataCaptureRequestEvent event = new DataCaptureRequestEvent(UUID.randomUUID().toString(), EventType.CAPTURE_DATA_EVENT, sender);
        final DataCaptureRequestEvent  event1 = new DataCaptureRequestEvent(UUID.randomUUID().toString(), "siteName", EventType.CAPTURE_DATA_RESP, sender);
        final DataCaptureRequestEvent  event2 = new DataCaptureRequestEvent(UUID.randomUUID().toString(), "siteName", locations, EventType.CAPTURE_DATA_RESP, sender);
        event.setSite(true);
        event.setSiteMerge(true);
        event.setNoOfFloors(1);
        event.setLocationRetrain(true);

        Assert.assertTrue(event.isSite());
        Assert.assertTrue(event.isSiteMerge());
        Assert.assertEquals(1, event.getNoOfFloors());
        Assert.assertTrue(event.isLocationRetrain());
    }
}
