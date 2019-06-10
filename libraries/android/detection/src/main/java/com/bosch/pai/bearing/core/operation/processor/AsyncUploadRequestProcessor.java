package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.event.RequestUploadEvent;
import com.bosch.pai.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.UUID;

/**
 * The type Async upload request processor.
 */
public class AsyncUploadRequestProcessor implements Runnable {

    private String requestID;
    private Event event;
    private Sender sender;

    /**
     * Instantiates a new Async upload request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public AsyncUploadRequestProcessor(String requestID, Event event, Sender sender) {
        this.requestID = requestID;
        this.event = event;
        this.sender = sender;
    }

    @Override
    public void run() {

        final RequestUploadEvent requestUploadEvent = (RequestUploadEvent) event;
        BearingResponseAggregator.getInstance().updateReadResponseAggregatorMap(UUID.fromString(requestID), (BearingCallBack) sender);
        BearingOperations bearingOperations = new BearingOperations();
        bearingOperations.registerListener(BearingResponseAggregator.getInstance());
        bearingOperations.setTransactionId(requestID);
        bearingOperations.uploadDataAsynchronousResponseServerCall(requestUploadEvent);

    }
}
