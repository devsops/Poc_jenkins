package com.bosch.pai.bearing.core.event;


import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

/**
 * The type Request upload event.
 */
public class RequestUploadEvent extends Event {

    /**
     * The enum Server fetch.
     */
    public enum ServerFetch {
        /**
         * Site upload server fetch.
         */
        SITE_UPLOAD,
        /**
         * Location upload server fetch.
         */
        LOCATION_UPLOAD,
        /**
         * Locations upload server fetch.
         */
        LOCATIONS_UPLOAD,

        SITE_THRESH_LOCATIONS_DATA_UPOAD;
    }

    private RequestUploadEvent.ServerFetch fetchRequest;
    private String locationName;

    /**
     * Instantiates a new Request upload event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public RequestUploadEvent(String requestID, EventType eventType, Sender sender) {
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

    /**
     * Gets location name.
     *
     * @return the location name
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Sets location name.
     *
     * @param locationName the location name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
