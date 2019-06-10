package com.bosch.pai.ipsadmin.bearing.core.event;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

public class RequestDetectionShutdownEvent extends Event {

    public RequestDetectionShutdownEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }
}
