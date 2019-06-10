package com.bosch.pai.bearing.sensordatastore.event;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.EventType;

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
@PrepareForTest({DataCaptureResponseEvent.class})
public class DataCaptureResponseEventTest {

    @Mock
    private BearingCallBack callBack;

    private List<String> locations = new ArrayList<>();

    @Test
    public void testSetterAndGetter() {
        final UUID observerID = UUID.randomUUID();
        final DataCaptureResponseEvent event = new DataCaptureResponseEvent(UUID.randomUUID().toString(), EventType.CAPTURE_DATA_RESP, callBack);
        final DataCaptureResponseEvent  event1 = new DataCaptureResponseEvent(UUID.randomUUID().toString(), EventType.CAPTURE_DATA_RESP, callBack, "siteName");
        final DataCaptureResponseEvent  event2 = new DataCaptureResponseEvent(UUID.randomUUID().toString(), EventType.CAPTURE_DATA_RESP, callBack, locations,"siteName");
        event.setObserverID(observerID);
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotObservation> list = new ArrayList<>();
        list.add(snapshotObservation);
        event.setSnapshotObservations(list);

        Assert.assertEquals(observerID, event.getObserverID());
        Assert.assertNotNull(event.getSnapshotObservations());
        Assert.assertEquals(snapshotObservation, event.getSnapshotObservations().get(0));
    }

}
