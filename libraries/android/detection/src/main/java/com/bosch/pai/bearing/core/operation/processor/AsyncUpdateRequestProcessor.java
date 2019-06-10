package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.event.RequestReadEvent;
import com.bosch.pai.bearing.core.event.RequestUpdateEvent;
import com.bosch.pai.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.UUID;

/**
 * The type Async update request processor.
 */
public class AsyncUpdateRequestProcessor implements Runnable {

    private String requestID;
    private Event event;
    private Sender sender;

    /**
     * Instantiates a new Async update request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public AsyncUpdateRequestProcessor(String requestID, Event event, Sender sender) {
        this.requestID = requestID;
        this.event = event;
        this.sender = sender;
    }


    @Override
    public void run() {

        final RequestUpdateEvent requestUpdateEvent = (RequestUpdateEvent) event;
        BearingResponseAggregator.getInstance().updateReadResponseAggregatorMap(UUID.fromString(requestID), (BearingCallBack) sender);
        BearingOperations bearingOperations = new BearingOperations();
        bearingOperations.registerListener(BearingResponseAggregator.getInstance());
        bearingOperations.setTransactionId(requestID);
        bearingOperations.updateDataWithAsynchronousResponse(requestUpdateEvent);
    }
}
