package com.bosch.pai.ipsadmin.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import java.util.List;

/**
 * The type Request detection start event.
 */
public class RequestDetectionStartEvent extends Event {

    private boolean isSite;

    /**
     * Instantiates a new Request detection start event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestDetectionStartEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Instantiates a new Request detection start event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param siteName  the site name
     */
    public RequestDetectionStartEvent(String requestID, EventType eventType, Sender sender, String siteName) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Request detection start event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param locations the locations
     * @param siteName  the site name
     */
    public RequestDetectionStartEvent(String requestID, EventType eventType, Sender sender, List<String> locations, String siteName) {
        super(requestID, eventType, sender, locations, siteName);
    }

    /**
     * Is site boolean.
     *
     * @return the boolean
     */
    public boolean isSite() {
        return this.isSite;
    }

    /**
     * Sets site.
     *
     * @param site the site
     */
    public void setSite(boolean site) {
        this.isSite = site;
    }
}
