package com.bosch.pai.bearing.sensordatastore.event;


import android.support.annotation.NonNull;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.List;

/**
 * The type Data capture request event.
 */
public class DataCaptureRequestEvent extends Event {
    private boolean isSite;
    private boolean isSiteMerge;
    private boolean isLocationRetrain = false;
    private boolean isAutoMergeEnable = false;
    private int noOfFloors;


    /**
     * Instantiates a new Data capture request event.
     *
     * @param requestID the request id
     * @param eventType the event type
     * @param sender    the sender
     */
    public DataCaptureRequestEvent(@NonNull String requestID, @NonNull EventType eventType, @NonNull Sender sender) {
        super(requestID, eventType, sender);
    }

    /**
     * Instantiates a new Data capture request event.
     *
     * @param requestID the request id
     * @param siteName  the site name
     * @param eventType the event type
     * @param sender    the sender
     */
    public DataCaptureRequestEvent(@NonNull String requestID, @NonNull String siteName, @NonNull EventType eventType, @NonNull Sender sender) {
        super(requestID, eventType, sender, siteName);
    }

    /**
     * Instantiates a new Data capture request event.
     *
     * @param requestID the request id
     * @param siteName  the site name
     * @param locations the locations
     * @param eventType the event type
     * @param sender    the sender
     */
    public DataCaptureRequestEvent(@NonNull String requestID, @NonNull String siteName, @NonNull List<String> locations, @NonNull EventType eventType, @NonNull Sender sender) {
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


    /**
     * Is site merge boolean.
     *
     * @return the boolean
     */
    public boolean isSiteMerge() {
        return isSiteMerge;
    }

    /**
     * Sets site merge.
     *
     * @param siteMerge the site merge
     */
    public void setSiteMerge(boolean siteMerge) {
        isSiteMerge = siteMerge;
    }

    /**
     * Is location retrain boolean.
     *
     * @return the boolean
     */
    public boolean isLocationRetrain() {
        return isLocationRetrain;
    }

    /**
     * Sets location retrain.
     *
     * @param locationRetrain the location retrain
     */
    public void setLocationRetrain(boolean locationRetrain) {
        isLocationRetrain = locationRetrain;
    }


    /**
     * Is auto merge enable boolean.
     *
     * @return the boolean
     */
    public boolean isAutoMergeEnable() {
        return isAutoMergeEnable;
    }

    /**
     * Sets auto merge enable.
     *
     * @param autoMergeEnable the auto merge enable
     */
    public void setAutoMergeEnable(boolean autoMergeEnable) {
        isAutoMergeEnable = autoMergeEnable;
    }

    /**
     * Gets no of floors.
     *
     * @return the no of floors
     */
    public int getNoOfFloors() {
        return noOfFloors;
    }

    /**
     * Sets no of floors.
     *
     * @param noOfFloors the no of floors
     */
    public void setNoOfFloors(int noOfFloors) {
        this.noOfFloors = noOfFloors;
    }
}
