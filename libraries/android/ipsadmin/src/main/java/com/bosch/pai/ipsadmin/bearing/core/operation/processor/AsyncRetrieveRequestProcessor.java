package com.bosch.pai.ipsadmin.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestReadEvent;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestRetrieveEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.ipsadmin.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.UUID;

/**
 * The type Async retrieve request processor.
 */
public class AsyncRetrieveRequestProcessor implements Runnable{

    private String requestID;
    private Event event;
    private Sender sender;


    /**
     * Instantiates a new Async retrieve request processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public AsyncRetrieveRequestProcessor(String requestID, Event event, Sender sender) {
        this.requestID = requestID;
        this.event = event;
        this.sender = sender;
    }


    @Override
    public void run() {

        final RequestRetrieveEvent requestRetrieveEvent = (RequestRetrieveEvent) event;
        BearingResponseAggregator.getInstance().updateReadResponseAggregatorMap(UUID.fromString(requestID), (BearingCallBack) sender);
        BearingOperations bearingOperations = new BearingOperations();
        bearingOperations.registerListener(BearingResponseAggregator.getInstance());
        bearingOperations.setTransactionId(requestID);
        bearingOperations.retrieveDataWithAsynchronousResponse(requestRetrieveEvent);
    }







}
