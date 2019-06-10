package com.bosch.pai.ipsadmin.bearing.core.operation.processor;


import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.algorithm.Thresholding.training.ThreshTrainOutput;
import com.bosch.pai.bearing.algorithm.Thresholding.training.ThresholdDataEntry;
import com.bosch.pai.bearing.algorithm.event.ThreshDataEntryAlgoEvent;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.event.ThreshDataEntryEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;
import com.google.gson.Gson;

import java.util.UUID;

/**
 * The type Thresh data entry event processor.
 */
public final class ThreshDataEntryEventProcessor extends TrainingRequestProcessor {

    /**
     * Instantiates a new Thresh data entry event processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public ThreshDataEntryEventProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(ThreshDataEntryEventProcessor.class.getName());
    }

    @Override
    public void run() {
        final ThreshDataEntryEvent threshDataEntryEvent = (ThreshDataEntryEvent) event;
        if (threshDataEntryEvent.getSensors().contains(BearingConfiguration.SensorType.ST_BLE)) {

            BearingResponseAggregator.getInstance().updateTrainingResponseAggregatorMap(UUID.fromString(requestID), threshDataEntryEvent.getApproach(), (BearingCallBack) sender);
            ThreshDataEntryAlgoEvent threshDataEntryAlgoEvent =
                    new ThreshDataEntryAlgoEvent(threshDataEntryEvent.getRequestID(), EventType.THRESH_DATA_ENTRY, new EventSender() {
                        @Override
                        public void reply(String requestID, String reply) {
                            if (threshDataEntryEvent.getRequestID().equals(requestID)) {
                                final ThreshTrainOutput threshTrainOutput = new Gson().fromJson(reply, ThreshTrainOutput.class);
                                if (threshTrainOutput.getStatus()) {
                                    BearingResponseAggregator.getInstance().onDataPersistSuccess(UUID.fromString(requestID), threshDataEntryEvent.getApproach(), threshTrainOutput.getMessage());
                                } else {
                                    BearingResponseAggregator.getInstance().onDataPersistError(UUID.fromString(requestID), threshDataEntryEvent.getApproach(), threshTrainOutput.getMessage());

                                }
                            }
                        }
                    }, BearingResponseAggregator.getInstance());

            threshDataEntryAlgoEvent.setSiteName(threshDataEntryEvent.getSiteName());
            threshDataEntryAlgoEvent.setLocationName(threshDataEntryEvent.getLocationName());
            threshDataEntryAlgoEvent.setSnapshotItems(threshDataEntryEvent.getSnapshotItems());
            threshDataEntryAlgoEvent.setRetrain(threshDataEntryEvent.isDataReEntry());

            AlgorithmLifeCycleHandler.getInstance().enqueue(threshDataEntryEvent.getRequestID(), threshDataEntryAlgoEvent, EventType.THRESH_DATA_ENTRY, ThresholdDataEntry.class.getName());

        }
    }
}
