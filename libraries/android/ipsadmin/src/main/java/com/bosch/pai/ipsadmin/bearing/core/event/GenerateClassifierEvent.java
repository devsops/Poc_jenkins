package com.bosch.pai.ipsadmin.bearing.core.event;


import android.support.annotation.NonNull;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import java.util.List;

/**
 * The type Generate classifier event.
 */
public class GenerateClassifierEvent extends Event {

    private boolean generateOnServer;

    /**
     * Instantiates a new Generate classifier event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param approach  the approach
     * @param sender    the sender
     */
    public GenerateClassifierEvent(@NonNull String requestID, @NonNull EventType eventType, BearingConfiguration.Approach approach, @NonNull Sender sender) {
        super(requestID, eventType, sender);
        this.approach = approach;
    }

    /**
     * Instantiates a new Generate classifier event.
     *
     * @param requestID the request id
     * @param siteName  the site name
     * @param eventType the event type
     * @param sender    the sender
     */
    public GenerateClassifierEvent(@NonNull String requestID, @NonNull String siteName, @NonNull EventType eventType, @NonNull EventSender sender) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Generate classifier event.
     *
     * @param requestID the request id
     * @param siteName  the site name
     * @param locations the locations
     * @param eventType the event type
     * @param sender    the sender
     */
    public GenerateClassifierEvent(@NonNull String requestID, @NonNull String siteName, @NonNull List<String> locations, @NonNull EventType eventType, @NonNull EventSender sender) {
        super(requestID, eventType, sender, locations, siteName);
    }

    /**
     * Is generate on server boolean.
     *
     * @return the boolean
     */
    public boolean isGenerateOnServer() {
        return generateOnServer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GenerateClassifierEvent that = (GenerateClassifierEvent) o;

        return generateOnServer == that.generateOnServer;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (generateOnServer ? 1 : 0);
       // result = 31 * result + (approach != null ? approach.hashCode() : 0);
        return result;
    }

    /**
     * Sets generate on server.
     *
     * @param generateOnServer the generate on server
     */
    public void setGenerateOnServer(boolean generateOnServer) {
        this.generateOnServer = generateOnServer;
    }


}
