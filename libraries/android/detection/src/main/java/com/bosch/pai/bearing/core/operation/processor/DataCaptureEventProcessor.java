package com.bosch.pai.bearing.core.operation.processor;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.bearing.sensordatastore.event.DataCaptureRequestEvent;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.SensorUtil;

import java.util.UUID;

/**
 * The type Data capture event processor.
 */
public final class DataCaptureEventProcessor extends TrainingRequestProcessor {

    /**
     * Instantiates a new Data capture event processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public DataCaptureEventProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(DataCaptureEventProcessor.class.getName());
    }

    @Override
    public void run() {
        final DataCaptureRequestEvent dataCaptureRequestEvent = (DataCaptureRequestEvent) event;
        BearingResponseAggregator.getInstance().updateTrainingResponseAggregatorMap(UUID.fromString(requestID), dataCaptureRequestEvent.getApproach(), (BearingCallBack) sender);

        if (dataCaptureRequestEvent.isSite()) {
            siteTrainUtil.setSiteTrainCallback(BearingResponseAggregator.getInstance());
            siteTrainUtil.registerSensorObservationListener();
            siteTrainUtil.setApproach(dataCaptureRequestEvent.getApproach());

            if(dataCaptureRequestEvent.getSensors() != null &&
                    dataCaptureRequestEvent.getSensors().contains(BearingConfiguration.SensorType.ST_BLE) &&
                    BearingConfiguration.Approach.THRESHOLDING != dataCaptureRequestEvent.getApproach()) {
                SensorUtil.setScanForBLEMac(true);
            } else {
                SensorUtil.setScanForBLEMac(false);
            }

            if (dataCaptureRequestEvent.isSiteMerge()) {
                siteTrainUtil.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_RECORD_APPEND);
                siteTrainUtil.setAutoMergeEnabled(dataCaptureRequestEvent.isAutoMergeEnable());
                siteTrainUtil.scanSensorForSignalMerge(UUID.fromString(requestID),
                        dataCaptureRequestEvent.getSiteName(), dataCaptureRequestEvent.getSensors());
            } else {
                siteTrainUtil.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_RECORD);
                siteTrainUtil.createSite(UUID.fromString(requestID),
                        dataCaptureRequestEvent.getSiteName(),dataCaptureRequestEvent.getNoOfFloors(), true, dataCaptureRequestEvent.getSensors());
            }


        } else {
            locationTrainerUtil.setLocationTrainListener(BearingResponseAggregator.getInstance());
            locationTrainerUtil.setObservationDataType(RequestDataHolder.ObservationDataType.LOCATION_DATA_RECORD);
            locationTrainerUtil.registerSensorObservationListener();
            locationTrainerUtil.setApproach(dataCaptureRequestEvent.getApproach());

            /*Is location retrain boolean is enabled based on the operationType selection. If the operation type selected is retrain */
            if (dataCaptureRequestEvent.isLocationRetrain()) {
                locationTrainerUtil.retrainLocation(UUID.fromString(requestID),
                        dataCaptureRequestEvent.getSiteName(), dataCaptureRequestEvent.getLocations().get(0), dataCaptureRequestEvent.getSensors());
            } else {
                locationTrainerUtil.addLocationToSite(UUID.fromString(requestID),
                        dataCaptureRequestEvent.getSiteName(), dataCaptureRequestEvent.getLocations().get(0), dataCaptureRequestEvent.isAutoMergeEnable(), dataCaptureRequestEvent.getSensors());
            }

        }
    }
}
