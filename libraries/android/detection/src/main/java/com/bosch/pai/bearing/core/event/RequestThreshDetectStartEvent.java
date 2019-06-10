package com.bosch.pai.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

/**
 * The type Request thresh detect start event.
 */
public class RequestThreshDetectStartEvent extends Event {


    /**
     * Instantiates a new Request thresh detect start event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestThreshDetectStartEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }


}
