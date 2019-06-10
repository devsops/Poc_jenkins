package com.bosch.pai.bearing.train.operations;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.event.RequestUploadEvent;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.enums.EventType;

import java.util.UUID;


/**
 * Upload API are design to keep all bearing data files upload independent.
 * Upload API will support two modes:
 * a. Synchronous response
 * b. asynchronous response.
 * <p>
 * The following operations are supported :
 * 1.Set Server EndPoint for Upload
 * 2.Upload Site snapshot data.
 * 3.Upload Location scv data.
 * <p>
 * However these two modes are not supported for all the operations.
 * <p>
 * ***********************************************************
 * <b>Set Server Endpoint </b> (only supported in synchronous mode)
 * OperationType= SET_SERVER_ENDPOINT
 * bearingData =null
 * bearingCallBack=null
 * ***************************************************************
 * <b>Site Snapshot Upload </b> (supported in both modes)
 * OperationType= UPLOAD_SITE
 * bearingData= siteMetadata with siteName
 * response can be both synchronous or asynchronous.
 * ***************************************************************
 * <b>Location csv upload</b>(supported in both modes)
 * OperationType=UPLOAD_LOCATION_CSV (for fingerprint file upload) / UPLOAD_LOCATION_THRESH(for threshold file upload)
 * BearingData = siteMetaData with siteName and locationMetaData with specific locationName to upload location.
 * ***************************************************************
 * <b>All locations upload for specific site</b>(supported in both modes)
 * OperationType= UPLOAD_LOCATION_CSV(for fingerprint file upload)/UPLOAD_LOCATION_THRESH(for threshold file upload)
 * BearingData= siteMetaData with siteName will upload all the locations for the specific site.
 * ***************************************************************
 */
public class Upload {
    private BearingHandler bearingHandler;
    private BearingOperations bearingOperations;

    /**
     * Instantiates a new Upload.
     *
     * @param bearingHandler the bearing handler
     */
    public Upload(BearingHandler bearingHandler) {
        this.bearingHandler = bearingHandler;
        this.bearingOperations = new BearingOperations();

    }

    /**
     * Synchronous response boolean.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param bearingData          the bearing data
     * @return the boolean
     */
    public boolean synchronousResponse(BearingConfiguration bearingConfiguration, BearingData bearingData) {
        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);

        if (bearingData == null && operationType.equals(BearingConfiguration.OperationType.SET_SERVER_ENDPOINT)) {
            return bearingOperations.setServerUrlEndpoint(bearingConfiguration.getIpAddress(), bearingConfiguration.getCertificateInputStream());
        }

        final BearingMode bearingMode = BearingRequestParser.getBearingModeForTraining(bearingData);
        return bearingOperations.uploadSynchronousResponseServerCall(operationType, bearingData, bearingMode);
    }


    /**
     * Asynchronous response.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param bearingData          the bearing data
     * @param bearingCallBack      the bearing call back
     */
    public void asynchronousResponse(BearingConfiguration bearingConfiguration, BearingData bearingData, BearingCallBack bearingCallBack) {

        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);
        String transactionId = String.valueOf(UUID.randomUUID());
        final RequestUploadEvent requestUploadEvent = requestNonSyncResponseUploadForServer(transactionId, operationType, bearingData, bearingCallBack);
        bearingHandler.enqueue(transactionId, requestUploadEvent, EventType.ASYNC_UPLOAD);
    }


    private RequestUploadEvent requestNonSyncResponseUploadForServer(String requestId, BearingConfiguration.OperationType operationType, BearingData bearingData, BearingCallBack bearingCallBack) {

        final RequestUploadEvent requestUploadEvent = new RequestUploadEvent(requestId, EventType.ASYNC_UPLOAD, bearingCallBack);
        final String siteName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.SITE);

        if (BearingConfiguration.OperationType.UPLOAD_SITE.equals(operationType)) {
            requestUploadEvent.setSiteName(siteName);
            requestUploadEvent.setFetchRequest(RequestUploadEvent.ServerFetch.SITE_UPLOAD);
            return requestUploadEvent;
        } else if (BearingConfiguration.OperationType.UPLOAD_LOCATION_CSV.equals(operationType)) {
            return requestUploadEventForLocApproach(requestUploadEvent, bearingData, BearingConfiguration.Approach.FINGERPRINT, siteName);

        } else if (BearingConfiguration.OperationType.UPLOAD_LOCATION_THRESH.equals(operationType)) {
            return requestUploadEventForLocApproach(requestUploadEvent, bearingData, BearingConfiguration.Approach.THRESHOLDING, siteName);
        } else if (BearingConfiguration.OperationType.UPLOAD_SITE_THRESH_DATA.equals(operationType)) {
            requestUploadEvent.setSiteName(siteName);
            requestUploadEvent.setFetchRequest(RequestUploadEvent.ServerFetch.SITE_THRESH_LOCATIONS_DATA_UPOAD);
            return requestUploadEvent;
        }
        return requestUploadEvent;
    }

    private RequestUploadEvent requestUploadEventForLocApproach(RequestUploadEvent requestUploadEvent, BearingData bearingData, BearingConfiguration.Approach approach, String siteName) {

        final BearingMode bearingModeForTraining = BearingRequestParser.getBearingModeForTraining(bearingData);
        if (BearingMode.LOCATION.equals(bearingModeForTraining)) {
            final String locationName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.LOCATION);
            requestUploadEvent.setSiteName(siteName);
            requestUploadEvent.setLocationName(locationName);
            requestUploadEvent.setApproach(approach);
            requestUploadEvent.setFetchRequest(RequestUploadEvent.ServerFetch.LOCATION_UPLOAD);
            return requestUploadEvent;

        } else if (BearingMode.SITE.equals(bearingModeForTraining)) {
            requestUploadEvent.setSiteName(siteName);
            requestUploadEvent.setApproach(approach);
            requestUploadEvent.setFetchRequest(RequestUploadEvent.ServerFetch.LOCATIONS_UPLOAD);
            return requestUploadEvent;
        }

        return requestUploadEvent;
    }


}
