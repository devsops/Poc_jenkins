package com.bosch.pai.bearing.core.operation.detection.site;


import java.util.UUID;

/**
 * The interface Site detect listener.
 */
public interface SiteDetectListener {

    /**
     * On site detection stop.
     *
     * @param transactionID the transaction id
     * @param statusMessage the status message
     */
    void onSiteDetectionStop(UUID transactionID, String statusMessage);

    /**
     * On site entry.
     *
     * @param transactionID the transaction id
     * @param localTime     the local time
     * @param siteName      the site name
     */
    void onSiteEntry(UUID transactionID, String localTime, String siteName);

    /**
     * On site exit.
     *
     * @param transactionID the transaction id
     * @param localTime     the local time
     * @param siteName      the site name
     */
    void onSiteExit(UUID transactionID, String localTime, String siteName);
}
