package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.event.GenerateClassifierEvent;
import com.bosch.pai.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.UUID;

/**
 * The type Generate classifier event processor.
 */
public final class GenerateClassifierEventProcessor extends TrainingRequestProcessor {

    /**
     * Instantiates a new Generate classifier event processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public GenerateClassifierEventProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(GenerateClassifierEventProcessor.class.getName());
    }

    @Override
    public void run() {
        final GenerateClassifierEvent generateClassifierEvent = (GenerateClassifierEvent) event;
        triggerTraining(requestID, generateClassifierEvent);
    }

    private void triggerTraining(String transactionId, GenerateClassifierEvent generateClassifierEvent) {

        if (generateClassifierEvent.isGenerateOnServer()) {

            final BearingOperations bearingOperations = new BearingOperations();
            BearingResponseAggregator.getInstance().updateReadResponseAggregatorMap(UUID.fromString(requestID), (BearingCallBack) sender);
            bearingOperations.registerListener(BearingResponseAggregator.getInstance());
            bearingOperations.setTransactionId(transactionId);
            bearingOperations.triggerTrainingOnServer(generateClassifierEvent.getSiteName(), generateClassifierEvent.getApproach());

        } else {
            BearingResponseAggregator.getInstance().updateTrainingResponseAggregatorMap(UUID.fromString(requestID), generateClassifierEvent.getApproach(), (BearingCallBack) sender);
            locationTrainerUtil.setLocationTrainListener(BearingResponseAggregator.getInstance());
            locationTrainerUtil.executeTraining(UUID.fromString(requestID), generateClassifierEvent.getSiteName());

        }

    }


}
