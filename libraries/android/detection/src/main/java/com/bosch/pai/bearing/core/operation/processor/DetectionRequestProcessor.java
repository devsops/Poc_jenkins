package com.bosch.pai.bearing.core.operation.processor;

import com.bosch.pai.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

/**
 * The type Detection request processor.
 */
abstract class DetectionRequestProcessor implements Runnable {

    /**
     * The Request id.
     */
    protected String requestID;
    /**
     * The Event.
     */
    protected Event event;
    /**
     * The Sender.
     */
    protected Sender sender;

    /**
     * The Location detector util.
     */
    protected LocationDetectorUtil locationDetectorUtil;
    /**
     * The Site detector util.
     */
    protected SiteDetectorUtil siteDetectorUtil;
    /**
     * The Thresh detector util.
     */
    //protected ThreshDetectorUtil threshDetectorUtil;


    /**
     * Instantiates a new Detection request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    protected DetectionRequestProcessor(String requestID, Event event, Sender sender) {
        this.requestID = requestID;
        this.event = event;
        this.sender = sender;

        this.locationDetectorUtil = new LocationDetectorUtil();
        this.siteDetectorUtil = new SiteDetectorUtil();
    }
}
