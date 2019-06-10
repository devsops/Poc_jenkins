package com.bosch.pai.ipsadmin.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

/**
 * The type Request read event.
 */
public class RequestReadEvent extends Event {

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

        ALL_SITES_FROM_SERVER,

        ALL_LOCATIONS_FROM_SERVER
    }

    private ServerFetch fetchRequest;

    /**
     * Instantiates a new Request read event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestReadEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Gets fetch request.
     *
     * @return the fetch request
     */
    public ServerFetch getFetchRequest() {
        return fetchRequest;
    }

    /**
     * Sets fetch request.
     *
     * @param fetchRequest the fetch request
     */
    public void setFetchRequest(ServerFetch fetchRequest) {
        this.fetchRequest = fetchRequest;
    }

}
