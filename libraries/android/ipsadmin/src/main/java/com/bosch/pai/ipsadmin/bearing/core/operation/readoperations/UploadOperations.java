package com.bosch.pai.ipsadmin.bearing.core.operation.readoperations;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestUploadEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingRESTClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * The type Upload operations.
 */
public class UploadOperations {

    private static final String TAG = UploadOperations.class.getName();
    private SynchronousServerCalls synchronousServerCalls;
    private BearingRESTClient bearingRestClient;
    private BearingOperationCallback bearingOperationCallback;


    /**
     * Instantiates a new Upload operations.
     *
     * @param bearingRESTClient the bearing rest client
     */
    public UploadOperations(BearingRESTClient bearingRESTClient) {
        synchronousServerCalls = new SynchronousServerCalls(bearingRESTClient, new SiteDetectorUtil(), new LocationDetectorUtil());
        this.bearingRestClient = bearingRESTClient;
    }

    /**
     * Register bearing operation callback.
     *
     * @param bearingOperationCallback the bearing operation callback
     */
    public void registerBearingOperationCallback(BearingOperationCallback bearingOperationCallback) {
        this.bearingOperationCallback = bearingOperationCallback;

    }

    /**
     * Upload synchronous response server call boolean.
     *
     * @param operationType the operation type
     * @param bearingData   the bearing data
     * @param bearingMode   the bearing mode
     * @return the boolean
     */
    public boolean uploadSynchronousResponseServerCall(BearingConfiguration.OperationType operationType, BearingData bearingData, BearingMode bearingMode) {
        final String siteName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.SITE);
        boolean result = false;
        switch (operationType) {
            case UPLOAD_SITE:
                result = synchronousServerCalls.uploadSiteData(siteName);
                break;
            case UPLOAD_LOCATION_CSV:
                result = uploadLocationsBasedOnApproach(BearingConfiguration.Approach.FINGERPRINT, bearingMode, bearingData, siteName);
                break;
            case UPLOAD_LOCATION_THRESH:
                result = uploadLocationsBasedOnApproach(BearingConfiguration.Approach.THRESHOLDING, bearingMode, bearingData, siteName);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "uploadSynchronousResponseServerCall: Unsupported OperationType");
        }
        return result;
    }


    private boolean uploadLocationsBasedOnApproach(BearingConfiguration.Approach approach, BearingMode bearingMode, BearingData bearingData, String siteName) {
        if (BearingMode.LOCATION.equals(bearingMode)) {
            final String locationName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.LOCATION);
            return synchronousServerCalls.uploadLocationData(siteName, locationName, approach);
        } else {
            return synchronousServerCalls.uploadAllLocationsForSite(siteName, approach);
        }
    }


    /**
     * Upload data asynchronous response server call.
     *
     * @param requestUploadEvent the request upload event
     */
    public void uploadDataAsynchronousResponseServerCall(RequestUploadEvent requestUploadEvent) {
        final UUID requestID = UUID.fromString(requestUploadEvent.getRequestID());
        final String siteName = requestUploadEvent.getSiteName();
        switch (requestUploadEvent.getFetchRequest()) {
            case SITE_UPLOAD:
                uploadSite(requestID, siteName);
                break;
            case LOCATION_UPLOAD:
                final String locationName = requestUploadEvent.getLocationName();
                uploadLocation(requestID, siteName, locationName, requestUploadEvent.getApproach());
                break;
            case LOCATIONS_UPLOAD:
                uploadLocations(requestID, siteName, requestUploadEvent.getApproach());
                break;
            case SITE_THRESH_LOCATIONS_DATA_UPOAD:
                uploadSiteThreshLocationsData(requestID, siteName);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "uploadDataAsynchronousResponseServerCall: Unsupported operation");
        }
    }

    private void uploadSiteThreshLocationsData(final UUID requestID, String siteName) {
        bearingRestClient.uploadSiteThreshLocationsData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                bearingOperationCallback.onDataReceived(requestID, Collections.<String>emptyList());
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(requestID, errMessage);
            }
        });
    }

    private void uploadLocations(final UUID requestID, String siteName, BearingConfiguration.Approach approach) {
        bearingRestClient.uploadLocationsDataForSite(siteName, approach, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                bearingOperationCallback.onDataReceived(requestID, new ArrayList<String>());
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(requestID, errMessage);
            }
        });
    }

    private void uploadLocation(final UUID requestID, String siteName, String locationName, BearingConfiguration.Approach approach) {
        bearingRestClient.uploadLocationData(siteName, locationName, approach, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                bearingOperationCallback.onDataReceived(requestID, new ArrayList<String>());
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(requestID, errMessage);
            }
        });
    }

    private void uploadSite(final UUID requestID, String siteName) {
        bearingRestClient.uploadSiteData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                bearingOperationCallback.onDataReceived(requestID, new ArrayList<String>());
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(requestID, errMessage);
            }
        });
    }


}
