package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.algorithm.Thresholding.training.ThreshTrainerUtil;
import com.bosch.pai.bearing.core.operation.training.location.LocationTrainerUtil;
import com.bosch.pai.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

/**
 * The type Training request processor.
 */
abstract class TrainingRequestProcessor implements Runnable {
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
     * The Location trainer util.
     */
    protected LocationTrainerUtil locationTrainerUtil;
    /**
     * The Site train util.
     */
    protected SiteTrainUtil siteTrainUtil;
    /**
     * The Thresh trainer util.
     */
    protected ThreshTrainerUtil threshTrainerUtil;

    /**
     * Instantiates a new Training request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    TrainingRequestProcessor(String requestID, Event event, Sender sender) {
        this.requestID = requestID;
        this.event = event;
        this.sender = sender;
        this.locationTrainerUtil = new LocationTrainerUtil();
        this.siteTrainUtil = new SiteTrainUtil();
    }
}
