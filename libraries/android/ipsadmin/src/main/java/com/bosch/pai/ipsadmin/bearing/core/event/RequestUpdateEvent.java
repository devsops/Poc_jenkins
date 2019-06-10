package com.bosch.pai.ipsadmin.bearing.core.event;


import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Request update event.
 */
public class RequestUpdateEvent extends Event {

    /**
     * The enum Server fetch.
     */
    public enum ServerFetch {
        /**
         * Site rename local server fetch.
         */
        SITE_RENAME_LOCAL,
        /**
         * Signal merge local server fetch.
         */
        SIGNAL_MERGE_LOCAL,
        /**
         * Site rename sync with server server fetch.
         */
        SITE_RENAME_SYNC_WITH_SERVER,
        /**
         * Signal merge sync with server server fetch.
         */
        SIGNAL_MERGE_SYNC_WITH_SERVER
    }

    private RequestUpdateEvent.ServerFetch fetchRequest;
    private String siteNameOld;
    private String siteNameNew;
    private List<SnapshotObservation> snapshotObservations = new ArrayList<>();

    /**
     * Instantiates a new Request update event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestUpdateEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Gets fetch request.
     *
     * @return the fetch request
     */
    public RequestUpdateEvent.ServerFetch getFetchRequest() {
        return fetchRequest;
    }

    /**
     * Sets fetch request.
     *
     * @param fetchRequest the fetch request
     */
    public void setFetchRequest(RequestUpdateEvent.ServerFetch fetchRequest) {
        this.fetchRequest = fetchRequest;
    }

    /**
     * Gets site name old.
     *
     * @return the site name old
     */
    public String getSiteNameOld() {
        return siteNameOld;
    }

    /**
     * Sets site name old.
     *
     * @param siteNameOld the site name old
     */
    public void setSiteNameOld(String siteNameOld) {
        this.siteNameOld = siteNameOld;
    }

    /**
     * Gets site name new.
     *
     * @return the site name new
     */
    public String getSiteNameNew() {
        return siteNameNew;
    }

    /**
     * Sets site name new.
     *
     * @param siteNameNew the site name new
     */
    public void setSiteNameNew(String siteNameNew) {
        this.siteNameNew = siteNameNew;
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
        this.snapshotObservations = snapshotObservations != null ? new ArrayList<>(snapshotObservations) :
                new ArrayList<SnapshotObservation>();
    }

}
