package com.bosch.pai.bearing.detect.operations;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.event.RequestDetectionShutdownEvent;
import com.bosch.pai.bearing.core.event.RequestDetectionStartEvent;
import com.bosch.pai.bearing.core.event.RequestDetectionStopEvent;
import com.bosch.pai.bearing.core.event.RequestThreshDetectStartEvent;
import com.bosch.pai.bearing.core.event.RequestThreshDetectionStopEvent;
import com.bosch.pai.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.enums.EventType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The type Detection.
 */
public class Detection {
    private static final String TAG = Detection.class.getClass().getName();
    private BearingHandler bearingHandler;


    /**
     * Instantiates a new Detection.
     *
     * @param bearingHandler the bearing handler
     */
    public Detection(BearingHandler bearingHandler) {
        this.bearingHandler = bearingHandler;
    }

    /**
     * Bearing Detection is designed to work for multiple approaches and expects to support multiple bearingConfiguration (sensors).All bearingConfiguration related parametres are feed to BearingConfiguration.
     * BearingData is used to feed any input to the bearing System
     * Bearing is capable to detect macro and micro location. Macro location called site and micro location are referred locations.
     * .....................
     * To detect site :
     * OperationType : DETECT_SITE.
     * Map<APPROACH:List<SensorTypes>> : Mapping the expected Approach and the corresponding sensors supported in the approach
     * BearingData : null
     * BearingCallBack : the anonymous implementation on onLocationResponse for Bearing Responses.
     * <p>
     * .....................
     * To detect location :
     * OperationType : DETECT_LOCATION.
     * Map<APPROACH:List<SensorTypes>> : Mapping the expected Approach and the corresponding sensors supported in the approach
     * BearingData : siteMetaData with the siteName to be passsed for which the micro level detection has to be done.
     * BearingCallBack : the anonymous implementation on onLocationREsponse for Bearing Responses.
     *
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingData     the bearing data
     * @param bearingCallBack the bearing call back
     */
    @Nullable
    public void invokeStartBearing(@NonNull BearingConfiguration bearingConfiguration, @NonNull BearingData bearingData, @NonNull BearingCallBack bearingCallBack) {

        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);
        final Set<BearingConfiguration.Approach> approaches = BearingRequestParser.parseConfigurationForApproachList(bearingConfiguration);
        final UUID uuid = UUID.randomUUID();

        for (BearingConfiguration.Approach approach : approaches) {

            final BearingMode bearingMode = BearingRequestParser.getBearingModeForDetection(bearingData);

            if (operationType == null || bearingMode == null)
                return;
            final List<BearingConfiguration.SensorType> sensorTypeList = BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);
            switch (operationType) {

                case DETECT_SITE:

                    final RequestDetectionStartEvent requestDetectionStartEvent = new RequestDetectionStartEvent(uuid.toString(),
                            EventType.TRIGGER_DETECTION,
                            bearingCallBack);
                    requestDetectionStartEvent.setSite(true);
                    requestDetectionStartEvent.setApproach(approach);
                    requestDetectionStartEvent.setSensors(sensorTypeList);
                    bearingHandler.enqueue(uuid.toString(), requestDetectionStartEvent, EventType.TRIGGER_DETECTION);
                    break;

                case DETECT_LOC:

                    if (bearingMode.equals(BearingMode.LOCATION)) {
                        final String siteName = bearingData.getSiteMetaData().getSiteName();
                        startLocationDetectionWithApproach(uuid.toString(), approach, sensorTypeList, siteName, bearingCallBack);
                    }
                    break;
                default:
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "invokeStartBearing: Unsupported Operation");
                    break;
            }
        }
    }


    @Nullable
    private void startLocationDetectionWithApproach(String uuid, BearingConfiguration.Approach approach, List<BearingConfiguration.SensorType> sensorTypeList, String siteName, BearingCallBack bearingCallBack) {
        switch (approach) {
            case FINGERPRINT:
                final RequestDetectionStartEvent requestDetectionStartEvent = new RequestDetectionStartEvent(uuid,
                        EventType.TRIGGER_DETECTION,
                        bearingCallBack);
                requestDetectionStartEvent.setSiteName(siteName);
                requestDetectionStartEvent.setSite(false);
                requestDetectionStartEvent.setSensors(sensorTypeList);
                requestDetectionStartEvent.setApproach(BearingConfiguration.Approach.FINGERPRINT);
                bearingHandler.enqueue(uuid, requestDetectionStartEvent, EventType.TRIGGER_DETECTION);
                break;
            case THRESHOLDING:
                final RequestThreshDetectStartEvent requestThreshDetectStartEvent = new RequestThreshDetectStartEvent(uuid,
                        EventType.THRESH_DETECTION,
                        bearingCallBack);
                requestThreshDetectStartEvent.setSiteName(siteName);
                requestThreshDetectStartEvent.setApproach(BearingConfiguration.Approach.THRESHOLDING);
                requestThreshDetectStartEvent.setSensors(sensorTypeList);
                bearingHandler.enqueue(uuid, requestThreshDetectStartEvent, EventType.THRESH_DETECTION);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Error finding matching approach");
        }
    }


    /**
     * Invoke stop bearing.
     *
     * @param bearingConfiguration the bearingConfiguration
     */
    public void invokeStopBearing(BearingConfiguration bearingConfiguration) {

        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);
        final Set<BearingConfiguration.Approach> approaches = BearingRequestParser.parseConfigurationForApproachList(bearingConfiguration);

        for (BearingConfiguration.Approach approach : approaches) {

            final List<BearingConfiguration.SensorType> sensorTypeList = BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);
            final UUID uuid = UUID.randomUUID();
            if (operationType == null)
                return;
            switch (operationType) {
                case DETECT_SITE:
                    final RequestDetectionStopEvent requestDetectionStopEvent = new RequestDetectionStopEvent(uuid.toString(),
                            EventType.STOP_DETECTION,
                            null);
                    requestDetectionStopEvent.setSite(true);
                    requestDetectionStopEvent.setSensors(sensorTypeList);
                    requestDetectionStopEvent.setApproach(approach);
                    bearingHandler.enqueue(uuid.toString(), requestDetectionStopEvent, EventType.STOP_DETECTION);
                    break;
                case DETECT_LOC:
                    stopDetectionWithApproach(uuid, approach, sensorTypeList);
                    break;
                default:
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "invokeStopBearing: Unsupported Operation ");
                    break;
            }
        }
    }

    private void stopDetectionWithApproach(UUID uuid, BearingConfiguration.Approach approach, List<BearingConfiguration.SensorType> sensorTypes) {
        switch (approach) {
            case FINGERPRINT:
                final RequestDetectionStopEvent requestDetectionStopEvent = new RequestDetectionStopEvent(uuid.toString(),
                        EventType.STOP_DETECTION,
                        null);
                requestDetectionStopEvent.setSite(false);
                requestDetectionStopEvent.setSensors(sensorTypes);
                requestDetectionStopEvent.setApproach(approach);
                bearingHandler.enqueue(uuid.toString(), requestDetectionStopEvent, EventType.STOP_DETECTION);
                break;
            case THRESHOLDING:
                final RequestThreshDetectionStopEvent requestThreshDetectionStopEvent = new RequestThreshDetectionStopEvent(uuid.toString(),
                        EventType.STOP_THRESH_DETECTION, null);
                requestThreshDetectionStopEvent.setSensors(sensorTypes);
                requestThreshDetectionStopEvent.setApproach(approach);
                bearingHandler.enqueue(uuid.toString(), requestThreshDetectionStopEvent, EventType.STOP_THRESH_DETECTION);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "invokeStopBearing: Unsupported Approach");
        }
    }


    public void shutdown() {
        final UUID uuid = UUID.randomUUID();
        final RequestDetectionStopEvent requestDetectionStopEvent = new RequestDetectionStopEvent(uuid.toString(),
                EventType.SHUTDOWN_DETECTION, null);
        bearingHandler.enqueue(uuid.toString(), requestDetectionStopEvent, EventType.SHUTDOWN_DETECTION);
    }
}
