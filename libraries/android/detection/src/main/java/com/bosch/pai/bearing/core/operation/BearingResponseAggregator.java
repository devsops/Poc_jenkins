package com.bosch.pai.bearing.core.operation;

import android.support.annotation.NonNull;

import com.bosch.pai.bearing.algorithm.Thresholding.training.ThreshTrainListener;
import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.operation.detection.location.LocationDetectListener;
import com.bosch.pai.bearing.core.operation.detection.site.SiteDetectListener;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperationCallback;
import com.bosch.pai.bearing.core.operation.training.location.TrainLocationListener;
import com.bosch.pai.bearing.core.operation.training.site.TrainSiteListener;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.DetectionDataForApproach;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.Header;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.BearingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The type Bearing response aggregator.
 */
public final class BearingResponseAggregator implements TrainSiteListener, TrainLocationListener, SiteDetectListener, LocationDetectListener, ThreshTrainListener, BearingOperationCallback {
    private final String TAG = BearingResponseAggregator.class.getName();
    private static BearingResponseAggregator bearingResponseAggregator;
    // Holds the training callback map
    private Map<String, BearingCallBack> trainingCallbackMap = new ConcurrentHashMap<>();
    // Holds the detection callback map
    /*Data holder map for latest responses from multiple algorithms*/

    private Map<UUID, List<DetectionDataForApproach>> multipleDetectionResponseMap = new HashMap<>();
    private Map<String, BearingCallbackOperation> detectionCallbackMap = new ConcurrentHashMap<>();
    //Holds the read api callback
    private Map<UUID, BearingCallBack> readCallbackMap = new ConcurrentHashMap<>();


    private BearingResponseAggregator() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public synchronized static BearingResponseAggregator getInstance() {
        if (bearingResponseAggregator == null) {
            bearingResponseAggregator = new BearingResponseAggregator();
        }
        return bearingResponseAggregator;
    }

    /**
     * Gets training callback map.
     *
     * @return the training callback map
     */
    Map<String, BearingCallBack> getTrainingCallbackMap() {
        return Collections.unmodifiableMap(trainingCallbackMap);
    }

    /* ************************************************************************************************ */
    /* ************************************** TRAINING_CALLBACKS ************************************** */
    /* ************************************************************************************************ */

    /**
     * Update response aggregator map.
     *
     * @param transactionId   the transaction id
     * @param approach        the approach
     * @param bearingCallBack the bearing call back
     */
    public void updateTrainingResponseAggregatorMap(@NonNull UUID transactionId, BearingConfiguration.Approach approach, @NonNull BearingCallBack bearingCallBack) {
        trainingCallbackMap.put(transactionId + "_" + approach, bearingCallBack);
    }


    @Override
    public void onTrainError(UUID transactionId, BearingConfiguration.Approach approach, String siteName, String errMessage) {
        final BearingOutput bearingSiteError = bearingOutputOnTrainError(StatusCode.INTERNAL_ERROR, BearingMode.SITE, errMessage);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingSiteError);
    }

    @Override
    public void onTrainSuccess(UUID transactionId, BearingConfiguration.Approach approach, String siteName) {
        final BearingOutput bearingSiteSuccess = bearingOutputOnTrainSuccess(StatusCode.OK, BearingMode.SITE);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingSiteSuccess);
    }

    @Override
    public void onSignalMerge(UUID transactionId, BearingConfiguration.Approach approach, String siteName, List<SnapshotObservation> snapshotObservationList) {
        final BearingOutput bearingOutput = bearingOutputOnTrainSuccess(StatusCode.OK, BearingMode.SITE);
        final Body body = new Body();
        body.setSnapshotObservations(snapshotObservationList);
        bearingOutput.setBody(body);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingOutput);
    }

    @Override
    public void onDataRecordProgress(UUID transactionID, BearingConfiguration.Approach approach, int progress) {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "*** OnData Recording progress ***" + progress);
        final BearingOutput bearingOutput = bearingOutputOnTrainProgress((progress * 100 / 60), StatusCode.MULTIPLE_RESPONSE, BearingMode.LOCATION);
        sendOutTrainProgressResponse(transactionID, approach, bearingOutput);
    }

    @Override
    public void onLocationsTrained(UUID transactionId, BearingConfiguration.Approach approach, String siteName) {
        final BearingOutput bearingOutput = bearingOutputOnTrainSuccess(StatusCode.OK, BearingMode.LOCATION);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingOutput);
    }

    @Override
    public void onLocationTrainError(UUID transactionId, BearingConfiguration.Approach approach, String errMessage) {
        final BearingOutput fingerprintOutput = bearingOutputOnTrainError(StatusCode.INTERNAL_ERROR, BearingMode.LOCATION, errMessage);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, fingerprintOutput);
    }

    @Override
    public void onDataRecordingCompleted(UUID transactionId, BearingConfiguration.Approach approach, String siteName, String locationName) {
        final BearingOutput bearingSiteSuccess = bearingOutputOnTrainSuccess(StatusCode.OK, BearingMode.LOCATION);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingSiteSuccess);
    }

    @Override
    public void onDataRecordError(UUID transactionId, BearingConfiguration.Approach approach, String errMessage) {
        final BearingOutput bearingFingerPrintError = bearingOutputOnTrainError(StatusCode.INTERNAL_ERROR, BearingMode.LOCATION, errMessage);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingFingerPrintError);
    }

    @Override
    public void onDataPersistError(UUID transactionId, BearingConfiguration.Approach approach, String errMessage) {
        final BearingOutput threshOutput = bearingOutputOnTrainError(StatusCode.INTERNAL_ERROR, BearingMode.LOCATION, errMessage);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, threshOutput);
    }

    @Override
    public void onDataPersistSuccess(UUID transactionId, BearingConfiguration.Approach approach, String successMessage) {
        final BearingOutput bearingOutput = bearingOutputOnTrainSuccess(StatusCode.OK, BearingMode.LOCATION);
        sendOutTrainingResponseAndUpdateCallbackMap(transactionId, approach, bearingOutput);
    }

    private BearingOutput bearingOutputOnTrainProgress(int progress, StatusCode multipleResponse, BearingMode location) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(multipleResponse);
        header.setBearingMode(location);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setOutput(String.valueOf(progress));
        bearingOutput.setBody(body);
        return bearingOutput;
    }

    private BearingOutput bearingOutputOnTrainSuccess(StatusCode statusCode, BearingMode bearingMode) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(statusCode);
        header.setBearingMode(bearingMode);
        bearingOutput.setHeader(header);
        return bearingOutput;
    }

    private BearingOutput bearingOutputOnTrainError(StatusCode statusCode, BearingMode bearingMode, String errorMsg) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(statusCode);
        bearingOutput.setHeader(header);
        header.setBearingMode(bearingMode);
        final Body body = new Body();
        body.setErrorMessage(errorMsg);
        bearingOutput.setBody(body);
        return bearingOutput;
    }


    private void sendOutTrainProgressResponse(UUID transactionID, BearingConfiguration.Approach approach, BearingOutput bearingOutput) {
        String identifier = transactionID + "_" + approach;
        if (trainingCallbackMap.keySet().contains(identifier)) {
            final BearingCallBack registeredCallback = trainingCallbackMap.get(identifier);
            registeredCallback.onLocationResponse(bearingOutput);
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "No registered callback for transactionID: " + identifier);
        }
    }

    private void sendOutTrainingResponseAndUpdateCallbackMap(UUID transactionId, BearingConfiguration.Approach approach, BearingOutput bearingOutput) {
        String identifier = transactionId + "_" + approach;
        if (trainingCallbackMap.keySet().contains(identifier)) {
            final BearingCallBack registeredCallback = trainingCallbackMap.get(identifier);
            registeredCallback.onLocationResponse(bearingOutput);
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "No registered callback for transactionID: " + transactionId);
        }
        trainingCallbackMap.remove(identifier);
    }

    /* ************************************************************************************************ */
    /* ************************************** DETECTION_CALLBACKS ************************************* */
    /* ************************************************************************************************ */

    /**
     * Update detection response aggregator map.
     *
     * @param transactionId   the transaction id
     * @param operationType   the operation type
     * @param approach        the approach
     * @param bearingCallBack the bearing call back
     */
    public synchronized void updateDetectionResponseAggregatorMap(UUID transactionId, BearingConfiguration.OperationType operationType, BearingConfiguration.Approach approach, BearingCallBack bearingCallBack) {
        if (transactionId == null) {
            String errorMsg = "Error in callback registration";
            final BearingOutput bearingOutput = bearingOutputOnError(StatusCode.INTERNAL_ERROR, errorMsg);
            bearingCallBack.onLocationResponse(bearingOutput);
            return;
        }
        BearingCallbackOperation bearingCallbackOperation = new BearingCallbackOperation(operationType, approach, bearingCallBack);
        removeUUIDForServedActiveModeRequest(bearingCallbackOperation);
        String identifier = transactionId + "_" + approach;
        detectionCallbackMap.put(identifier, bearingCallbackOperation);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, Thread.currentThread().getName() + "updateDetectionResponseAggregatorMap: check");

    }

    @Override
    public void onSiteDetectionStop(UUID transactionID, String statusMessage) {
        final BearingOutput bearingOutput = bearingOutputOnDetectError(BearingMode.SITE, statusMessage);
        sendOutDetectionResponseAndUpdateCallbackMap(transactionID, bearingOutput);
    }

    @Override
    public void onSiteEntry(UUID transactionID, String localTime, String siteName) {
        final BearingOutput bearingOutput = bearingOutputOnSiteDetect(localTime, siteName);
        sendOutDetectionResponseAndUpdateCallbackMap(transactionID, bearingOutput);
    }

    @Override
    public void onSiteExit(UUID transactionID, String localTime, String siteName) {
        final BearingOutput bearingOutput = bearingOutputOnSiteDetect(localTime, siteName);
        sendOutDetectionResponseAndUpdateCallbackMap(transactionID, bearingOutput);
    }

    @Override
    public void onErrorDetectingLocation(UUID uuid, String errMessage) {
        final BearingOutput bearingOutput = bearingOutputOnDetectError(BearingMode.LOCATION, errMessage);
        sendOutDetectionResponseAndUpdateCallbackMap(uuid, bearingOutput);
    }

    @Override
    public void onLocationUpdate(UUID uuid, BearingConfiguration.Approach approach, String siteName, Map<String, Double> locationToProbabilityMap, String localTime) {
        final BearingOutput bearingOutput = buildDetectionResponseWithApproach(uuid, approach, siteName, locationToProbabilityMap, localTime);
        if (bearingOutput != null) {
            sendOutDetectionResponseAndUpdateCallbackMap(uuid, bearingOutput);
        }
    }


    private List<String> getListOfApproachesForUUID(UUID uuid) {
        final List<String> uuidApproachList = new ArrayList<>();
        final Set<String> strings = detectionCallbackMap.keySet();

        for (String uuidStrings : strings) {
            if (uuidStrings.contains(uuid.toString())) {
                uuidApproachList.add(uuidStrings);
            }
        }
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "getListOfApproachesForUUID: collecting all Approaches");
        return uuidApproachList;
    }


    private synchronized BearingOutput buildDetectionResponseWithApproach(UUID uuid, BearingConfiguration.Approach approach, String siteName, Map<String, Double> locationToProbabilityMap, String localTime) {

        updateResponseMapForApproaches(uuid, approach, locationToProbabilityMap, localTime);
        if (multipleDetectionResponseMap.get(uuid).size() == getListOfApproachesForUUID(uuid).size()) {
            final BearingOutput bearingOutput = bearingOutputOnLocationDetect(multipleDetectionResponseMap.get(uuid), siteName);
            multipleDetectionResponseMap.remove(uuid);
            return bearingOutput;
        }
        return null;
    }


    private void updateResponseMapForApproaches(UUID uuid, BearingConfiguration.Approach approach, Map<String, Double> locationToProbabilityMap, String localTime) {

        final DetectionDataForApproach detectionDataForApproach = new DetectionDataForApproach(approach, locationToProbabilityMap, localTime);

        if (multipleDetectionResponseMap.get(uuid) == null) {
            final List<DetectionDataForApproach> detectionDataForApproaches = new ArrayList<>();
            detectionDataForApproaches.add(detectionDataForApproach);
            multipleDetectionResponseMap.put(uuid, detectionDataForApproaches);
        } else {
            List<DetectionDataForApproach> detectionDataForApproachList = multipleDetectionResponseMap.get(uuid);
            final List<DetectionDataForApproach> detectionDataForApproaches = updateDetectionDataForApproach(detectionDataForApproach, detectionDataForApproachList);
            multipleDetectionResponseMap.put(uuid, detectionDataForApproaches);
        }
    }


    private List<DetectionDataForApproach> updateDetectionDataForApproach(DetectionDataForApproach detectionDataForApproach, List<DetectionDataForApproach> detectionDataForApproaches) {

        boolean temp = false;
        for (DetectionDataForApproach detectionDataForExistingApproach : detectionDataForApproaches) {
            if (detectionDataForExistingApproach.getApproach() == detectionDataForApproach.getApproach()) {
                detectionDataForExistingApproach.setCellConfidence(detectionDataForApproach.getCellConfidence());
                detectionDataForExistingApproach.setLocalTime(detectionDataForApproach.getLocalTime());
            } else {
                temp = true;
            }
        }
        if (temp) {
            detectionDataForApproaches.add(detectionDataForApproach);
        }
        return detectionDataForApproaches;
    }


    private BearingOutput bearingOutputOnError(StatusCode statusCode, String errorMsg) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(statusCode);
        header.setBearingMode(null);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setErrorMessage(errorMsg);
        bearingOutput.setBody(body);
        return bearingOutput;
    }


    private BearingOutput bearingOutputOnDetectError(BearingMode bearingMode, String msg) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.INTERNAL_ERROR);
        header.setBearingMode(bearingMode);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setErrorMessage(msg);
        bearingOutput.setBody(body);
        return bearingOutput;
    }

    private BearingOutput bearingOutputOnLocationDetect(List<DetectionDataForApproach> detectionDataForApproaches, String siteName) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.OK);
        header.setBearingMode(BearingMode.LOCATION);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setOutput(siteName);
        body.setLocationDetectionOutput(detectionDataForApproaches);
        bearingOutput.setBody(body);
        return bearingOutput;
    }


    private BearingOutput bearingOutputOnSiteDetect(String timestamp, String msg) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.OK);
        header.setBearingMode(BearingMode.SITE);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setTimestamp(timestamp);
        body.setOutput(msg);
        bearingOutput.setBody(body);
        return bearingOutput;
    }


    private BearingOutput bearingOutputOnReadOperation(List<String> list) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.OK);
        header.setBearingMode(BearingMode.READ_DATA);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setResponseList(list);
        bearingOutput.setBody(body);
        return bearingOutput;


    }

    private void removeUUIDForServedActiveModeRequest(BearingCallbackOperation bearingCallbackOperation) {
        final BearingConfiguration.OperationType operationType = bearingCallbackOperation.getOperationType();
        final BearingConfiguration.Approach approach = bearingCallbackOperation.getApproach();
        final Set<String> uuids = detectionCallbackMap.keySet();
        final Iterator<String> iterator = uuids.iterator();
        String uuidToRemove = null;
        while (iterator.hasNext()) {
            final String uuidToTest = iterator.next();
            if (detectionCallbackMap.get(uuidToTest).getOperationType().equals(operationType) &&
                    detectionCallbackMap.get(uuidToTest).getApproach().equals(approach)) {
                uuidToRemove = uuidToTest;
            }
        }
        if (uuidToRemove == null) {
            return;
        }
        detectionCallbackMap.remove(uuidToRemove);
    }

    private void sendOutDetectionResponseAndUpdateCallbackMap(UUID transactionId, BearingOutput bearingOutput) {
        Iterator iterator = detectionCallbackMap.keySet().iterator();
        String uuidOnError = null;
        while (iterator.hasNext()) {
            final String transactionID = (String) iterator.next();
            if (transactionID.contains(transactionId.toString())) {
                final BearingCallbackOperation bearingCallbackOperation = detectionCallbackMap.get(transactionID);
                final BearingCallBack registeredCallback = bearingCallbackOperation.getBearingCallback();
                final BearingConfiguration.OperationType operationType = bearingCallbackOperation.getOperationType();
                registeredCallback.onLocationResponse(bearingOutput);
                if (isResponseServed(bearingOutput, operationType)) {
                    uuidOnError = transactionID;
                }
                break;
            }
        }
        if (uuidOnError == null) {
            return;
        }
        detectionCallbackMap.remove(uuidOnError);
    }


    private void senOutReadResponseAndUpdateCallbackMapForRead(UUID transactionId, BearingOutput bearingOutput) {

        Iterator iterator = readCallbackMap.keySet().iterator();
        UUID uuidOnError = null;
        while (iterator.hasNext()) {
            final UUID transactionID = (UUID) iterator.next();
            if (transactionId.equals(transactionID)) {
                final BearingCallBack registeredCallback = readCallbackMap.get(transactionID);
                registeredCallback.onLocationResponse(bearingOutput);
                uuidOnError = transactionID;
                break;
            }
        }
        if (uuidOnError == null) {
            return;
        }
        readCallbackMap.remove(uuidOnError);

    }

    private boolean isResponseServed(BearingOutput bearingOutput, BearingConfiguration.OperationType operationType) {
        boolean detectSite = !StatusCode.OK.equals(bearingOutput.getHeader().getStatusCode()) ||
                !operationType.equals(BearingConfiguration.OperationType.DETECT_SITE) ||
                operationType.equals(BearingConfiguration.OperationType.DETECT_LOC);
        boolean detectLoc = !StatusCode.OK.equals(bearingOutput.getHeader().getStatusCode()) ||
                operationType.equals(BearingConfiguration.OperationType.DETECT_SITE) ||
                !operationType.equals(BearingConfiguration.OperationType.DETECT_LOC);
        return detectSite && detectLoc;
    }

    /**********************************READ API callbacks @param transactionId the transaction id
     * @param bearingCallBack the bearing call back
     */


    public void updateReadResponseAggregatorMap(@NonNull UUID transactionId, @NonNull BearingCallBack bearingCallBack) {
        readCallbackMap.put(transactionId, bearingCallBack);
    }


    @Override
    public void onDataReceived(UUID transactionId, List<String> dataList) {
        final BearingOutput bearingOutput = bearingOutputOnReadOperation(dataList);
        senOutReadResponseAndUpdateCallbackMapForRead(transactionId, bearingOutput);

    }

    @Override
    public void onDataReceivedError(UUID transactionId, String errorMessage) {
        final BearingOutput bearingOutput = bearingOutputOnError(StatusCode.INTERNAL_ERROR, errorMessage);
        senOutReadResponseAndUpdateCallbackMapForRead(transactionId, bearingOutput);
    }

}
