package com.bosch.pai.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.List;

/**
 * The type Request thresh detection stop event.
 */
public class RequestThreshDetectionStopEvent extends Event {
    /**
     * Instantiates a new Request thresh detection stop event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestThreshDetectionStopEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Instantiates a new Request thresh detection stop event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param siteName  the site name
     */
    public RequestThreshDetectionStopEvent(String requestID, EventType eventType, Sender sender, String siteName) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Request thresh detection stop event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param locations the locations
     * @param siteName  the site name
     */
    public RequestThreshDetectionStopEvent(String requestID, EventType eventType, Sender sender, List<String> locations, String siteName) {
        super(requestID, eventType, sender, locations, siteName);
    }
}
