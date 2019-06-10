package com.bosch.pai.bearing.core.operation.detection.location;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.algorithm.Thresholding.detection.ThresholdDetection;
import com.bosch.pai.bearing.algorithm.distance.OverlayCellDetection;
import com.bosch.pai.bearing.algorithm.event.AlgorithmOutput;
import com.bosch.pai.bearing.algorithm.event.DetectionCalculationEvent;
import com.bosch.pai.bearing.algorithm.event.ThreshDetectionOutput;
import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.benchmark.bearinglogger.profiling.ResourceProfiler;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.core.util.Constants;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SvmClassifierData;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingRESTClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * The type Location detector util.
 */
public class LocationDetectorUtil {

    private final String TAG = LocationDetectorUtil.class.getName();
    private static String currentSite = Constants.SITE_UNKNOWN;
    private ObservationHandlerAndListener observationHandlerAndListener;
    private UUID bearingUUID;
    private LocationDetectListener locationListenerCallback;
    private static boolean serverDoesNotHaveFingerPrintData = false;
    private static String previousClusterDataDownloadSite = "";

    /**
     * Instantiates a new Location detector util.
     */
    public LocationDetectorUtil() {
        observationHandlerAndListener = new ObservationHandlerAndListener();
    }

    /**
     * Sets transaction id.
     *
     * @param bearingUUID the bearing uuid
     */
    public void setTransactionID(UUID bearingUUID) {
        this.bearingUUID = bearingUUID;
    }

    /**
     * Gets current site.
     *
     * @return the current site
     */
    public String getCurrentSite() {
        return currentSite;
    }

    private static void setCurrentSiteName(String name) {
        LocationDetectorUtil.currentSite = name;
    }

    private static void setPreviousClusterDataDownloadSite(String previousClusterDataDownloadSite) {
        LocationDetectorUtil.previousClusterDataDownloadSite = previousClusterDataDownloadSite;
    }

    private static void setServerDoesNotHaveFingerPrintData(boolean serverDoesNotHaveFingerPrintData) {
        LocationDetectorUtil.serverDoesNotHaveFingerPrintData = serverDoesNotHaveFingerPrintData;
    }

    /**
     * Sets current site.
     *
     * @param siteName the site name
     */
    public void setCurrentSite(String siteName) {
        LocationDetectorUtil.setCurrentSiteName(siteName);
        if (!Constants.SITE_UNKNOWN.equals(siteName) && !previousClusterDataDownloadSite.equals(siteName)) {
            LocationDetectorUtil.setPreviousClusterDataDownloadSite(siteName);
            ClusterDataDownloader.validateClusterDataAndDownload(siteName);
        }
    }

    /**
     * Gets location listener callback.
     *
     * @return the location listener callback
     */
    public LocationDetectListener getLocationListenerCallback() {
        return locationListenerCallback;
    }

    /**
     * Sets observation data type.
     *
     * @param observationDataType the observation data type
     */
    public void setObservationDataType(RequestDataHolder.ObservationDataType observationDataType) {
        observationHandlerAndListener.setObservationDataType(observationDataType);
    }

    /**
     * Sets location listener callback.
     *
     * @param locationListenerCallback the location listener callback
     */
    public void setLocationListenerCallback(LocationDetectListener locationListenerCallback) {
        this.locationListenerCallback = locationListenerCallback;
    }

    /**
     * Start location detection.
     *
     * @param uuid           the uuid
     * @param sensorTypeList the sensor type list
     */
    public void startLocationDetection(BearingConfiguration.Approach approach, UUID uuid, List<BearingConfiguration.SensorType> sensorTypeList) {
        new ResourceProfiler().writeDeviceInfo(null);
        this.bearingUUID = uuid;
        final boolean isSourceAdded = observationHandlerAndListener.addObservationSource(bearingUUID, sensorTypeList, true);
        if (!isSourceAdded) {
            if (locationListenerCallback != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Sensor not enabled !!! Sensor :: " + sensorTypeList);
                locationListenerCallback.onErrorDetectingLocation(bearingUUID, Constants.SENSOR_NOT_ENABLED);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "There is not a callback registered to receive location" +
                        " detection information. Register one using LocationDetectorUtil.registerLocationListener.");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Sensor not enabled !!! Sensor :: " + sensorTypeList + "  " + Constants.SENSOR_NOT_ENABLED);
            }
            return;
        }
        final RequestDataHolder requestDataHolder = new RequestDataHolder(uuid, RequestDataHolder.ObservationDataType.LOCATION_DETECTION,
                observationHandlerAndListener);
        requestDataHolder.setSiteName(currentSite);
        requestDataHolder.setActiveModeOn(true);
        requestDataHolder.setApproach(approach);
        BearingHandler.addRequestToRequestDataHolderMap(requestDataHolder);
    }

    private void sendReply(UUID uuid, BearingConfiguration.Approach approach, String siteName, Map<String, Double> s, String localTime) {
        if (locationListenerCallback != null) {
            locationListenerCallback.onLocationUpdate(uuid, approach, siteName, s, localTime);
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Register callback to get responses");
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "**** Detected location ****" + s);
        }
    }


    /**
     * Register sensor observation listener.
     */
    public void registerSensorObservationListener() {
        observationHandlerAndListener.registerSensorObservationListener();
    }


    /**
     * Detect location using svm.
     *
     * @param snapshotObservations the snapshot observations
     * @param sensorTypes          the sensor types
     * @param approach             the approach
     */
    public void detectLocation(List<SnapshotObservation> snapshotObservations, List<BearingConfiguration.SensorType> sensorTypes, BearingConfiguration.Approach approach) {

        final UUID requestUUID = UUID.randomUUID();

        switch (approach) {
            case FINGERPRINT:
                triggerDetectionWithFingerprinting(requestUUID, snapshotObservations, sensorTypes);
                break;
            case THRESHOLDING:
                triggerDetectionWithThreshold(requestUUID, snapshotObservations, sensorTypes);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "detectLocation: Unsupported Approach");
                break;
        }
    }

    /**
     * Write classifier data boolean.
     *
     * @param siteName          the site name
     * @param svmClassifierData the svm classifier data
     * @return the boolean
     */
    public boolean writeClassifierData(String siteName, SvmClassifierData svmClassifierData) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final PersistenceResult persistenceResult = persistenceHandler.writeClassifiers(siteName, svmClassifierData);
        return PersistenceResult.RESULT_OK.equals(persistenceResult);
    }

    /**
     * Sets approach.
     *
     * @param approach the approach
     */
    public void setApproach(BearingConfiguration.Approach approach) {
        observationHandlerAndListener.setApproach(approach);
    }

    /**
     * Method to fetch all associated locations with the current site set.
     *
     * @param siteName the site name
     * @param approach the approach
     * @return return list of location names. If site is unknown returns empty list.
     */
    public List<String> getLocationNames(String siteName, BearingConfiguration.Approach approach) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return new ArrayList<>(persistenceHandler.getLocationNames(siteName, approach));
    }

    /**
     * Method to get all the location names from the SVMT file for the associated Site
     *
     * @param siteName the site name
     * @return returns the list of location names. If site has no locations, empty location list is returned.
     */


    public List<String> getLocationNamesFromClusterData(String siteName) {

        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.getLocationNamesFromClassifier(siteName);
    }

    private void triggerDetectionWithFingerprinting(final UUID requestUUID, final List<SnapshotObservation> snapshotObservations, final List<BearingConfiguration.SensorType> sensorTypes) {

        DetectionCalculationEvent calculationEvent = new DetectionCalculationEvent(requestUUID.toString(), EventType.TRIGGER_DETECTION, new EventSender() {
            @Override
            public void reply(String requestID, String reply) {
                if (requestUUID.toString().equals(requestID)) {
                    try {
                        AlgorithmOutput output = new Gson().fromJson(reply, AlgorithmOutput.class);
                        if (output.getLocationToProbabilityMap() != null && !output.getLocationToProbabilityMap().isEmpty()) {
                            processSVMClassifierOutput(output);
                        } else {
                            processSnapshotObservationOnServer(currentSite, snapshotObservations, sensorTypes);
                        }

                    } catch (JsonSyntaxException ex) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Exception caught: ", ex);
                    }
                }
            }
        }, currentSite, OverlayCellDetection.class.getName(), snapshotObservations, sensorTypes);
        calculationEvent.setApproach(BearingConfiguration.Approach.FINGERPRINT);
        AlgorithmLifeCycleHandler.getInstance().enqueue(requestUUID.toString(), calculationEvent, EventType.TRIGGER_DETECTION, OverlayCellDetection.class.getName());
    }


    private void processSVMClassifierOutput(AlgorithmOutput outputAlgo) {
        if (outputAlgo != null) {
            String siteName = outputAlgo.getCurrentSite();
            Map<String, Double> s = outputAlgo.getLocationToProbabilityMap();
            String localTime = outputAlgo.getLocalTime();
            sendReply(bearingUUID, BearingConfiguration.Approach.FINGERPRINT, siteName, s, localTime);
        }
    }

    private void processSnapshotObservationOnServer(String currentSite, List<SnapshotObservation> snapshotObservations, List<BearingConfiguration.SensorType> sensorTypes) {
        if (serverDoesNotHaveFingerPrintData) {
            final Calendar cal = Calendar.getInstance();
            final TimeZone tz = cal.getTimeZone();
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
            sdf.setTimeZone(tz);
            final String localTime = sdf.format(new Date());
            sendReply(bearingUUID, BearingConfiguration.Approach.FINGERPRINT, currentSite, new HashMap<String, Double>(), localTime);
            return;
        }
        final BearingRESTClient bearingRESTClient = BearingRESTClient.getInstance();
        bearingRESTClient.processDataOnServer(currentSite, snapshotObservations, sensorTypes, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Success :: " + message, null);
                try {
                    AlgorithmOutput output = new Gson().fromJson(message, AlgorithmOutput.class);
                    String siteName = output.getCurrentSite();
                    Map<String, Double> s = output.getLocationToProbabilityMap();
                    if (s == null || s.isEmpty()) {
                        s = new HashMap<>();
                        LocationDetectorUtil.setServerDoesNotHaveFingerPrintData(true);
                    }
                    if (s.isEmpty()) {
                        s.put("LOCATION_UNKNOWN", 0.0);
                    }
                    String localTime = output.getLocalTime();
                    sendReply(bearingUUID, BearingConfiguration.Approach.FINGERPRINT, siteName, s, localTime);
                } catch (JsonSyntaxException ex) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Exception caught: ", ex);
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Error ::" + errMessage, null);
                //TODO :: Pass the error message through the bearing API
            }
        });
    }

    private void triggerDetectionWithThreshold(UUID requestUUID, List<SnapshotObservation> snapshotObservations, List<BearingConfiguration.SensorType> sensorTypes) {

        DetectionCalculationEvent detectionCalculationEvent = new DetectionCalculationEvent(requestUUID.toString(), EventType.THRESH_DETECTION, new EventSender() {
            @Override
            public void reply(String requestID, String reply) {

                if (requestID.toString().equals(requestID)) {

                    try {

                        ThreshDetectionOutput threshDetectionOutput = new Gson().fromJson(reply, ThreshDetectionOutput.class);
                        if (threshDetectionOutput != null) {
                            String siteName = threshDetectionOutput.getSiteName();
                            Map<String, Double> s = threshDetectionOutput.getLocationToProbabilityMap();
                            String localTime = threshDetectionOutput.getLocalTime();
                            sendReply(bearingUUID, BearingConfiguration.Approach.THRESHOLDING, siteName, s, localTime);
                        }

                    } catch (JsonSyntaxException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Exception caught: ", e);
                    }

                }
            }
        }, currentSite, ThresholdDetection.class.getName(), snapshotObservations, sensorTypes);
        detectionCalculationEvent.setApproach(BearingConfiguration.Approach.THRESHOLDING);
        AlgorithmLifeCycleHandler.getInstance().enqueue(requestUUID.toString(), detectionCalculationEvent, EventType.THRESH_DETECTION, ThresholdDetection.class.getName());


    }
}
