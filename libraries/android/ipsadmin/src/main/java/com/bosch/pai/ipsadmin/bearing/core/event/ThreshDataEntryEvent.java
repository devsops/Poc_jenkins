package com.bosch.pai.ipsadmin.bearing.core.event;


import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Thresh data entry event.
 */
public class ThreshDataEntryEvent extends Event {

    private List<SnapshotItem> snapshotItems = new ArrayList<>();
    private String locationName;
    private boolean isDataReEntry;

    /**
     * Gets location name.
     *
     * @return the location name
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Sets location name.
     *
     * @param locationName the location name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    /**
     * Instantiates a new Thresh data entry event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public ThreshDataEntryEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Gets snapshot items.
     *
     * @return the snapshot items
     */
    public List<SnapshotItem> getSnapshotItems() {
        return Collections.unmodifiableList(snapshotItems);
    }

    /**
     * Sets snapshot items.
     *
     * @param snapshotItems the snapshot items
     */
    public void setSnapshotItems(List<SnapshotItem> snapshotItems) {
        this.snapshotItems = snapshotItems != null ? new ArrayList<>(snapshotItems) : new ArrayList<SnapshotItem>();
    }

    public void setDataReEntry(boolean dataReEntry) {
        isDataReEntry = dataReEntry;
    }

    public boolean isDataReEntry() {
        return isDataReEntry;
    }
}
