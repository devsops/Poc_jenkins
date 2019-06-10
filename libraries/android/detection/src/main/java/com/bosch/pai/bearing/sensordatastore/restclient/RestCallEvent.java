package com.bosch.pai.bearing.sensordatastore.restclient;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.List;

/**
 * The type Rest call event.
 */
public class RestCallEvent extends Event {

    /**
     * The enum Type.
     */
    enum Type {
        /**
         * Site download type.
         */
        SITE_DOWNLOAD,
        /**
         * Location download type.
         */
        LOCATION_DOWNLOAD,
        /**
         * Svmt download type.
         */
        SVMT_DOWNLOAD,
        /**
         * Get sites type.
         */
        GET_SITES,
        /**
         * Get locations type.
         */
        GET_LOCATIONS,
        /**
         * Site upload type.
         */
        SITE_UPLOAD,
        /**
         * Location upload type.
         */
        LOCATION_UPLOAD,
        /**
         * Svmt upload type.
         */
        SVMT_UPLOAD
    }

    /**
     * Instantiates a new Rest call event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RestCallEvent(String requestID, EventType eventType, Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Instantiates a new Rest call event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param siteName  the site name
     */
    public RestCallEvent(String requestID, EventType eventType, Sender sender, String siteName) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Rest call event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     * @param locations the locations
     * @param siteName  the site name
     */
    public RestCallEvent(String requestID, EventType eventType, Sender sender, List<String> locations, String siteName) {
        super(requestID, eventType, sender, locations, siteName);
    }
}
