package com.bosch.pai.bearing.sensordatastore.event;


import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The type Data capture response event.
 */
public class DataCaptureResponseEvent extends Event {

    private UUID observerID;
    private List<SnapshotObservation> snapshotObservations = new ArrayList<>();

    /**
     * Instantiates a new Data capture response event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public DataCaptureResponseEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Instantiates a new Data capture response event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param siteName  the site name
     */
    public DataCaptureResponseEvent(String requestID, EventType eventType, Sender sender, String siteName) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Data capture response event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param locations the locations
     * @param siteName  the site name
     */
    public DataCaptureResponseEvent(String requestID, EventType eventType, Sender sender, List<String> locations, String siteName) {
        super(requestID, eventType, sender, locations, siteName);
    }

    /**
     * Gets snapshot observations.
     *
     * @return the snapshot observations
     */
    public List<SnapshotObservation> getSnapshotObservations() {
        return Collections.unmodifiableList(snapshotObservations);
    }

    /**
     * Sets snapshot observations.
     *
     * @param snapshotObservations the snapshot observations
     */
    public void setSnapshotObservations(List<SnapshotObservation> snapshotObservations) {
        this.snapshotObservations = snapshotObservations != null ? new ArrayList<>(snapshotObservations) : new ArrayList<SnapshotObservation>();
    }

    /**
     * Gets observer id.
     *
     * @return the observer id
     */
    public UUID getObserverID() {
        return observerID;
    }

    /**
     * Sets observer id.
     *
     * @param observerID the observer id
     */
    public void setObserverID(UUID observerID) {
        this.observerID = observerID;
    }
}
