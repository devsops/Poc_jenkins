package com.bosch.pai.ipsadmin.bearing.train.operations;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestRetrieveEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.Header;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.enums.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Retrieve Operations to support retrieval from bearing.
 * The retrieve API is designed for 4 scenarios:
 * <p>
 * 1. Sync with server : <b>TRUE</b> and Callback :<b>NONNULL</b>
 * 2. Sync with server :<b>FALSE</b> and Callback :<b>NONNULL</b>
 * 3. Sync with server :<b>TRUE</b> and Callback :<b>NULL</b>
 * 4. Sync with server :<b>FALSE</b> and Callback :<b>NULL</b>
 * <p>
 * <p>
 * 1. & 2. are synchronous API calls as they don't have callback
 * 3. & 4. are asynchronous API calls as they have callback
 * <p>
 * Sync with server is placed for server synchronization of BearingData.
 * The following operations are supported:
 * ***************************************************************
 * Retrieve all the site data present on device(synchronous and asynchronous)
 * syncWithServer ->false
 * OperationType ->READ_SITE_LIST
 * BearingData   ->null
 * ***************************************************************
 * Retrieve all the site data present on server after sync with device(synchronous and asynchronous)
 * syncWithServer -> true
 * OperationType -> READ_SITE_LIST
 * BearingData   -> null
 * ***************************************************************
 * Retrieve all the location data present on device(synchronous and asynchronous)
 * syncWithServer ->false
 * OperationType  ->READ_LOC_LIST
 * BearingData    ->siteMetaData with siteName passed in BearingData
 * ****************************************************************
 * Retrieve all the location data on server after sync with device(synchronous and asynchronous)
 * syncWithServer  ->true
 * OperationType   ->READ_LOC_LIST
 * BearingData     ->siteMetaData with siteName passed in BearingData
 * *****************************************************************
 * Scan sensor for signals excluding captured site signals (asynchronous)
 * synWithServer   ->false
 * operationType   ->RESCAN_SIGNAL_FOR_DELTA
 * approach        ->DATA_CAPTURE
 * List<Sensor>    ->SensorList for which the scan has to be done.
 * BearingData     ->siteMetaData with the siteName passed in BearingData
 * *****************************************************************
 * Snapshot site data fetch for specific site (synchronous)
 * syncWithServer  ->false
 * operationType   ->SNAPSHOT_FETCH
 * BearingData     -> siteMetaData with the sitName passed in BearingData
 * *****************************************************************
 */
public class Retrieve {
    private BearingOperations bearingOperations;
    private BearingHandler bearingHandler;

    /**
     * Instantiates a new Retrieve.
     *
     * @param bearingHandler the bearing handler
     */
    public Retrieve(BearingHandler bearingHandler) {
        this.bearingOperations = new BearingOperations();
        this.bearingHandler = bearingHandler;
    }


    /**
     * Synchronous response bearing output.
     *
     * @param syncWithServer       the sync with server
     * @param bearingData          the bearing data
     * @param bearingConfiguration bearingConfiguration with the operation type
     * @return the bearing output
     */
    public BearingOutput synchronousResponse(boolean syncWithServer, BearingConfiguration bearingConfiguration, BearingData bearingData) {

        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);

        if (syncWithServer) {
            final BearingOperations.Response response = bearingOperations.retrieveSynchronousDataAfterSyncWithServer(bearingData, operationType, true);
            return buildBearingOutputForSyncResponse(response);
        } else {
            final BearingOperations.Response response = bearingOperations.retrieveSynchronousDataWithoutSyncWithServer(bearingData, operationType);
            return buildBearingOutputForSyncResponse(response);
        }
    }


    /**
     * Asynchronous response.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param syncWithServer       the sync with server
     * @param bearingData          the bearing data
     * @param bearingCallBack      the bearing call back
     */
    public void asynchronousResponse(boolean syncWithServer, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingCallBack bearingCallBack) {
        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);
        String requestId = String.valueOf(UUID.randomUUID());

        if (syncWithServer) {
            final RequestRetrieveEvent requestRetrieveEvent = requestForNonSyncResponseWithSyncWithServer(requestId, operationType, bearingData, bearingCallBack);
            bearingHandler.enqueue(requestId, requestRetrieveEvent, EventType.ASYNC_RETRIEVE);
        } else {
            final RequestRetrieveEvent requestRetrieveEvent = requestForNonSyncRetrieveRespWithoutSyncServer(requestId, bearingConfiguration, operationType, bearingData, bearingCallBack);
            bearingHandler.enqueue(requestId, requestRetrieveEvent, EventType.ASYNC_RETRIEVE);
        }
    }

    private RequestRetrieveEvent requestForNonSyncResponseWithSyncWithServer(String requestId, BearingConfiguration.OperationType operationType, BearingData bearingData, BearingCallBack bearingCallBack) {
        RequestRetrieveEvent requestRetrieveEvent = new RequestRetrieveEvent(requestId, EventType.ASYNC_RETRIEVE, bearingCallBack);
        if (BearingConfiguration.OperationType.READ_SITE_LIST.equals(operationType)) {
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.ALL_SITES_SYNC_WITH_SERVER);
            return requestRetrieveEvent;

        } else if (BearingConfiguration.OperationType.READ_LOC_LIST.equals(operationType)) {
            String siteName = null;
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.ALL_LOCATIONS_SYNC_WITH_SERVER);
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestRetrieveEvent.setSiteName(siteName);
            return requestRetrieveEvent;
        } else if (BearingConfiguration.OperationType.READ_THRESH_LIST.equals(operationType)) {
            String siteName = null;
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestRetrieveEvent.setSiteName(siteName);
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.THRESH_SOURCE_ID_MAP);
            return requestRetrieveEvent;
        } else if (BearingConfiguration.OperationType.READ_SITE_LIST_ON_SERVER.equals(operationType)) {
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.ALL_SITE_NAMES_FROM_SERVER);
            return requestRetrieveEvent;


        } else if (BearingConfiguration.OperationType.READ_LOC_LIST_ON_SERVER.equals(operationType)) {

            String siteName = null;
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.ALL_LOCATION_NAMES_FROM_SERVER);
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestRetrieveEvent.setSiteName(siteName);
            return requestRetrieveEvent;

        }

        return requestRetrieveEvent;
    }

    private RequestRetrieveEvent requestForNonSyncRetrieveRespWithoutSyncServer(String requestId, BearingConfiguration bearingConfiguration, BearingConfiguration.OperationType operationType, BearingData bearingData, BearingCallBack bearingCallBack) {
        final RequestRetrieveEvent requestRetrieveEvent = new RequestRetrieveEvent(requestId, EventType.ASYNC_RETRIEVE, bearingCallBack);
        String siteName = null;
        if (BearingConfiguration.OperationType.READ_SITE_LIST.equals(operationType)) {
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.ALL_SITES_LOCAL);
            return requestRetrieveEvent;
        } else if (BearingConfiguration.OperationType.READ_LOC_LIST.equals(operationType)) {

            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.ALL_LOCATIONS_LOCAL);
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestRetrieveEvent.setSiteName(siteName);
            return requestRetrieveEvent;
        } else if (BearingConfiguration.OperationType.RESCAN_SIGNAL_FOR_DELTA.equals(operationType)) {
            final Set<BearingConfiguration.Approach> approachList = BearingRequestParser.getApproach(bearingConfiguration);

            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            List<BearingConfiguration.SensorType> sensorList = new ArrayList<>();
            requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.SCAN_SENSOR);
            if (approachList.contains(BearingConfiguration.Approach.DATA_CAPTURE)) {
                sensorList.addAll(BearingRequestParser.getSensorList(bearingConfiguration, BearingConfiguration.Approach.DATA_CAPTURE));
                requestRetrieveEvent.setApproach(BearingConfiguration.Approach.DATA_CAPTURE);
            } else if (approachList.contains(BearingConfiguration.Approach.THRESHOLDING)) {
                sensorList.addAll(BearingRequestParser.getSensorList(bearingConfiguration, BearingConfiguration.Approach.THRESHOLDING));
                requestRetrieveEvent.setApproach(BearingConfiguration.Approach.THRESHOLDING);
            }
            requestRetrieveEvent.setSensors(sensorList);
            requestRetrieveEvent.setSiteName(siteName);
            return requestRetrieveEvent;
        }
        return requestRetrieveEvent;
    }


    private BearingOutput buildBearingOutputForSyncResponse(BearingOperations.Response response) {

        BearingOutput bearingOutput = new BearingOutput();
        Header header = new Header();
        header.setBearingMode(BearingMode.SITE);
        header.setStatusCode(StatusCode.OK);
        Body body = new Body();
        if (response.getOperationType().equals(BearingConfiguration.OperationType.SNAPSHOT_FETCH)) {
            body.setSnapshotObservations(response.getSnapshotObservations());
        } else {
            body.setResponseList(response.getStringsList());
        }
        bearingOutput.setHeader(header);
        bearingOutput.setBody(body);
        return bearingOutput;
    }


}
