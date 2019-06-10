package com.bosch.pai.ipsadmin.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class ThreshDataEntryEventTest {

    @Mock
    private Sender sender;

    @Test
    public void testConstructor() {
        final String REQUEST_ID = "REQUEST_ID";
        final String LOCATION_NAME = "LOCATION_NAME";
        final EventType eventType = EventType.THRESH_DATA_ENTRY;

        final ThreshDataEntryEvent event = new ThreshDataEntryEvent(REQUEST_ID, eventType, sender);
        Assert.assertNotNull(event);

        event.setDataReEntry(true);
        event.setLocationName(LOCATION_NAME);
        event.setSnapshotItems(new ArrayList<>());

        Assert.assertEquals(LOCATION_NAME, event.getLocationName());
        Assert.assertTrue(event.isDataReEntry());
        Assert.assertTrue(event.getSnapshotItems().isEmpty());

    }
}
