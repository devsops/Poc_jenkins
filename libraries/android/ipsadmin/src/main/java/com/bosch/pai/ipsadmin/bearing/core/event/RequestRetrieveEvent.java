package com.bosch.pai.ipsadmin.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

/**
 * The type Request retrieve event.
 */
public class RequestRetrieveEvent extends Event {

    /**
     * The enum Server fetch.
     */
    public enum ServerFetch {
        /**
         * All sites local server fetch.
         */
        ALL_SITES_LOCAL,
        /**
         * All locations local server fetch.
         */
        ALL_LOCATIONS_LOCAL,
        /**
         * All thresh local server fetch.
         */
        ALL_THRESH_LOCAL,
        /**
         * All sites sync with server server fetch.
         */
        ALL_SITES_SYNC_WITH_SERVER,
        /**
         * All locations sync with server server fetch.
         */
        ALL_LOCATIONS_SYNC_WITH_SERVER,
        /**
         * Scan sensor server fetch.
         */
        SCAN_SENSOR,

        /**
         * All site names from server server fetch.
         */
        ALL_SITE_NAMES_FROM_SERVER,

        /**
         * All location names from server server fetch.
         */
        ALL_LOCATION_NAMES_FROM_SERVER,

        /**
         * Source id map server fetch.
         */
        SOURCE_ID_MAP,

        THRESH_SOURCE_ID_MAP;


    }

    private RequestRetrieveEvent.ServerFetch fetchRequest;

    /**
     * Instantiates a new Request retrieve event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestRetrieveEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Gets fetch request.
     *
     * @return the fetch request
     */
    public RequestRetrieveEvent.ServerFetch getFetchRequest() {
        return fetchRequest;
    }

    /**
     * Sets fetch request.
     *
     * @param fetchRequest the fetch request
     */
    public void setFetchRequest(RequestRetrieveEvent.ServerFetch fetchRequest) {
        this.fetchRequest = fetchRequest;
    }
}
