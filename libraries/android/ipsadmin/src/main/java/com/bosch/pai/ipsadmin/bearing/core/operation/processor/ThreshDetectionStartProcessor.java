package com.bosch.pai.ipsadmin.bearing.core.operation.processor;



import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestThreshDetectStartEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;

import java.util.List;
import java.util.UUID;

/**
 * The type Thresh detection initiateDetection processor.
 */
public final class ThreshDetectionStartProcessor extends DetectionRequestProcessor {
    private ObservationHandlerAndListener observationHandlerAndListener;
    private String siteName;
    private static final String TAG = ThreshDetectionStartProcessor.class.getName();


    /**
     * Instantiates a new Thresh detection initiateDetection processor.
     *
     * @param requestID the request id
     * @param event     the event
     * @param sender    the sender
     */
    public ThreshDetectionStartProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(ThreshDetectionStartProcessor.class.getName());
        observationHandlerAndListener = new ObservationHandlerAndListener();

    }

    @Override
    public void run() {
        final RequestThreshDetectStartEvent requestThreshDetectStartEvent = (RequestThreshDetectStartEvent) this.event;
        SensorUtil.setScanForBLEMac(false);
        observationHandlerAndListener.setApproach(requestThreshDetectStartEvent.getApproach());
        BearingResponseAggregator.getInstance().updateDetectionResponseAggregatorMap(UUID.fromString(requestID),
                BearingConfiguration.OperationType.DETECT_LOC, requestThreshDetectStartEvent.getApproach(),
                (BearingCallBack) sender);
        registerSensorObservationListener();
        setCurrentSite(requestThreshDetectStartEvent.getSiteName());
        setObservationDataType(RequestDataHolder.ObservationDataType.LOCATION_DETECTION);

        if (requestThreshDetectStartEvent.getSensors().contains(BearingConfiguration.SensorType.ST_BLE)) {
            initiateDetection(UUID.fromString(requestThreshDetectStartEvent.getRequestID()), requestThreshDetectStartEvent.getSensors());
        }
    }

    /**
     * Listener registers with BearingResponse Aggregator . The Bearing Response Aggregator Aggregates the responses.
     */
    private void registerSensorObservationListener() {
        observationHandlerAndListener.registerSensorObservationListener();
    }

    /**
     * Sets the siteName for the site to which the detected location maps too. This set is useful to map to the folder structure to which the location maps.
     *
     * @param currentSite the current site
     */
    private void setCurrentSite(String currentSite) {
        if (currentSite == null || currentSite.isEmpty()) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "setCurrentSite: No valid siteName set for site");
        }
        this.siteName = currentSite;
    }

    /**
     * Location detection based on threshold values stored during location training.
     * <p>
     * During detection each value test data is scored against the observation data obtained via training. The label for the maximum score is returned
     *
     * @param transcationId  id mapped for each api call in bearing Response aggregator.
     * @param sensorTypeList the sensortype List used to trigger detection
     */
    private void initiateDetection(UUID transcationId, List<BearingConfiguration.SensorType> sensorTypeList) {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "startSiteDetection");

        observationHandlerAndListener.addObservationSource(transcationId, sensorTypeList, true);
        final RequestDataHolder requestDataHolder = new RequestDataHolder(transcationId, RequestDataHolder.ObservationDataType.LOCATION_DETECTION,
                observationHandlerAndListener);
        requestDataHolder.setActiveModeOn(true);
        requestDataHolder.setSiteName(siteName);
        requestDataHolder.setApproach(BearingConfiguration.Approach.THRESHOLDING);
        BearingHandler.addRequestToRequestDataHolderMap(requestDataHolder);

    }

    /**
     * Sets observation data type.
     *
     * @param observationDataType the observation data type
     */
    private void setObservationDataType(RequestDataHolder.ObservationDataType observationDataType) {
        observationHandlerAndListener.setObservationDataType(observationDataType);
    }


}
