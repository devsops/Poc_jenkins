package com.bosch.pai.ipsadmin.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.List;


/**
 * The type Request detection stop event.
 */
public class RequestDetectionStopEvent extends Event {

    private boolean isSite;

    /**
     * Instantiates a new Request detection stop event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestDetectionStopEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Instantiates a new Request detection stop event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param siteName  the site name
     */
    public RequestDetectionStopEvent(String requestID, EventType eventType, Sender sender, String siteName) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Request detection stop event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param locations the locations
     * @param siteName  the site name
     */
    public RequestDetectionStopEvent(String requestID, EventType eventType, Sender sender, List<String> locations, String siteName) {
        super(requestID, eventType, sender, locations, siteName);
    }

    /**
     * Sets site.
     *
     * @param isSite the is site
     */
    public void setSite(boolean isSite) {
        this.isSite = isSite;
    }

    /**
     * Is site boolean.
     *
     * @return the boolean
     */
    public boolean isSite() {
        return this.isSite;
    }
}
