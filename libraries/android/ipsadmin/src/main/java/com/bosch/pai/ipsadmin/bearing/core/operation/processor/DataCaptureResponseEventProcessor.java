package com.bosch.pai.ipsadmin.bearing.core.operation.processor;


import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.event.DataCaptureResponseEvent;

/**
 * The type Data capture response event processor.
 */
public final class DataCaptureResponseEventProcessor extends TrainingRequestProcessor {

    private RequestDataHolder requestDataHolder;

    /**
     * Instantiates a new Data capture response event processor.
     *
     * @param requestID         the request id
     * @param event             the event
     * @param sender            the sender
     * @param requestDataHolder the request data holder
     */
    public DataCaptureResponseEventProcessor(String requestID, Event event, Sender sender, RequestDataHolder requestDataHolder) {
        super(requestID, event, sender);
        Thread.currentThread().setName(DataCaptureResponseEventProcessor.class.getName());
        this.requestDataHolder = requestDataHolder;
    }

    @Override
    public void run() {
        final ObservationHandlerAndListener observationHandlerAndListener = this.requestDataHolder.getObservationHandlerAndListener();
        final DataCaptureResponseEvent dataCaptureResponseEvent = (DataCaptureResponseEvent) event;
        observationHandlerAndListener.onSensorDataReceived(requestDataHolder.getSiteName(),
                requestDataHolder.getNoOfFloors(),
                requestDataHolder.getLocationName(),
                dataCaptureResponseEvent.getSnapshotObservations());
    }
}
