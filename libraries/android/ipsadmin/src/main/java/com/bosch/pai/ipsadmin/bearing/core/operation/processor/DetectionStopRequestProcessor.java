package com.bosch.pai.ipsadmin.bearing.core.operation.processor;


import android.support.annotation.Nullable;

import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestDetectionStopEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.Collection;
import java.util.Map;

/**
 * The type Detection stop request processor.
 */
public final class DetectionStopRequestProcessor extends DetectionRequestProcessor {


    /**
     * Instantiates a new Detection stop request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public DetectionStopRequestProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(DetectionStopRequestProcessor.class.getName());
    }

    @Override
    public void run() {
        final RequestDetectionStopEvent requestDetectionStopEvent = (RequestDetectionStopEvent) this.event;
        if (requestDetectionStopEvent.isSite()) {
            stopSiteDetection(requestDetectionStopEvent.getApproach());
        } else {
            stopLocationDetection(requestDetectionStopEvent.getApproach());
        }
    }

    private void stopSiteDetection(BearingConfiguration.Approach approach) {
        final RequestDataHolder requestDataHolder = getRequestDataHolder(true);
        if (requestDataHolder == null) {
            return;
        }

        requestDataHolder.getObservationHandlerAndListener().removeAndUnregisterSensorObservation(approach);
    }

    private void stopLocationDetection(BearingConfiguration.Approach approach) {
        final RequestDataHolder requestDataHolder = getRequestDataHolder(false);
        if (requestDataHolder == null) {
            return;
        }
        requestDataHolder.getObservationHandlerAndListener().removeAndUnregisterSensorObservation(approach);
    }

    @Nullable
    private RequestDataHolder getRequestDataHolder(boolean isSite) {
        final Map<String, RequestDataHolder> uuidRequestDataHolderMap = BearingHandler.getUuidToRequestDataHolderMap();
        final Collection<RequestDataHolder> requestDataHolderCollection = uuidRequestDataHolderMap.values();
        if (requestDataHolderCollection.isEmpty()) {
            return null;
        }

        for (RequestDataHolder requestDataHolder : requestDataHolderCollection) {
            final RequestDataHolder.ObservationDataType observationDataType;
            if (isSite) {
                observationDataType = RequestDataHolder.ObservationDataType.SITE_DETECTION;
            } else {
                observationDataType = RequestDataHolder.ObservationDataType.LOCATION_DETECTION;
            }

            if (observationDataType == requestDataHolder.getObservationDataType()) {
                return requestDataHolder;
            }
        }
        return null;
    }
}
