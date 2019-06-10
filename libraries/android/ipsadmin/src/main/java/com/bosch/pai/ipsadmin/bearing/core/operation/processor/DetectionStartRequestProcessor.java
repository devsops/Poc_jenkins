package com.bosch.pai.ipsadmin.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestDetectionStartEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;

import java.util.UUID;

/**
 * The type Detection start request processor.
 */
public final class DetectionStartRequestProcessor extends DetectionRequestProcessor {

    /**
     * Instantiates a new Detection start request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public DetectionStartRequestProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(DetectionStartRequestProcessor.class.getName());
    }

    @Override
    public void run() {
        final RequestDetectionStartEvent requestDetectionStartEvent = (RequestDetectionStartEvent) this.event;

        if(requestDetectionStartEvent.getSensors() != null &&
                requestDetectionStartEvent.getSensors().contains(BearingConfiguration.SensorType.ST_BLE) &&
                BearingConfiguration.Approach.THRESHOLDING != requestDetectionStartEvent.getApproach()) {
            SensorUtil.setScanForBLEMac(true);
        } else {
            SensorUtil.setScanForBLEMac(false);
        }

        if (requestDetectionStartEvent.isSite()) {
            BearingResponseAggregator.getInstance().updateDetectionResponseAggregatorMap(UUID.fromString(requestID),
                    BearingConfiguration.OperationType.DETECT_SITE,requestDetectionStartEvent.getApproach(), (BearingCallBack) sender);
            siteDetectorUtil.registerSensorObservationListener();
            siteDetectorUtil.setApproach(requestDetectionStartEvent.getApproach());
            siteDetectorUtil.setCallback(BearingResponseAggregator.getInstance());
            siteDetectorUtil.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_DETECTION);
            siteDetectorUtil.startSiteDetection(requestDetectionStartEvent.getApproach(),
                    UUID.fromString(requestID), requestDetectionStartEvent.getSensors());
        } else {
            BearingResponseAggregator.getInstance().updateDetectionResponseAggregatorMap(UUID.fromString(requestID),
                    BearingConfiguration.OperationType.DETECT_LOC,requestDetectionStartEvent.getApproach(), (BearingCallBack) sender);
            locationDetectorUtil.registerSensorObservationListener();
            locationDetectorUtil.setCurrentSite(requestDetectionStartEvent.getSiteName());
            locationDetectorUtil.setApproach(requestDetectionStartEvent.getApproach());
            locationDetectorUtil.setLocationListenerCallback(BearingResponseAggregator.getInstance());
            locationDetectorUtil.setObservationDataType(RequestDataHolder.ObservationDataType.LOCATION_DETECTION);
            locationDetectorUtil.startLocationDetection(requestDetectionStartEvent.getApproach(),
                    UUID.fromString(requestID), requestDetectionStartEvent.getSensors());
        }
    }
}
