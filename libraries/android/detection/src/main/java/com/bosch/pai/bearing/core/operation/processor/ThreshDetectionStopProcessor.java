package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.event.RequestThreshDetectionStopEvent;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.Collection;
import java.util.Map;

/**
 * The type Thresh detection stop processor.
 */
public class ThreshDetectionStopProcessor extends DetectionRequestProcessor {
    /**
     * Instantiates a new Detection request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public ThreshDetectionStopProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(ThreshDetectionStopProcessor.class.getName());

    }

    @Override
    public void run() {
        final RequestThreshDetectionStopEvent requestThreshDetectionStopEvent = (RequestThreshDetectionStopEvent) this.event;

        final Map<String, RequestDataHolder> uuidRequestDataHolderMap = BearingHandler.getUuidToRequestDataHolderMap();
        final Collection<RequestDataHolder> requestDataHolderCollection = uuidRequestDataHolderMap.values();

        for (RequestDataHolder requestDataHolder : requestDataHolderCollection) {

            if (requestDataHolder.getObservationDataType().equals(RequestDataHolder.ObservationDataType.LOCATION_DETECTION)) {
                requestDataHolder.getObservationHandlerAndListener().
                        removeAndUnregisterSensorObservation(requestThreshDetectionStopEvent.getApproach());
            }
        }
    }
}
