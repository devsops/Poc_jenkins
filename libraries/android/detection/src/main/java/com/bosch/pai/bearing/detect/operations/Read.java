package com.bosch.pai.bearing.detect.operations;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.event.RequestReadEvent;
import com.bosch.pai.bearing.core.event.RequestRetrieveEvent;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.Header;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.enums.EventType;

import java.util.List;
import java.util.UUID;

/**
 * The type Read.
 */
public class Read {
    private BearingOperations bearingOperations;
    private BearingHandler bearingHandler;


    /**
     * Instantiates a new Read.
     *
     * @param bearingHandler the bearing handler
     */
    public Read(BearingHandler bearingHandler) {
        bearingOperations = new BearingOperations();
        this.bearingHandler = bearingHandler;
    }

    /**
     * Synchronous response read operation bearing output.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param syncWithServer       the sync with server
     * @param bearingData          the bearing data
     * @return the bearing output
     */
    public BearingOutput synchronousResponseReadOperation(BearingConfiguration bearingConfiguration, boolean syncWithServer, BearingData bearingData) {

        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);

        if (syncWithServer) {
            return buildBearingOutputForRead(bearingOperations.readSynchronousDataAfterSyncWithServer(bearingData, operationType));
        } else {
            if (operationType.equals(BearingConfiguration.OperationType.SET_SERVER_ENDPOINT)) {
                return buildBearingOutputForRead(bearingOperations.setServerUrlEndpoint(bearingConfiguration.getIpAddress(), bearingConfiguration.getCertificateInputStream()));

            } else {
                return buildBearingOutputForRead(bearingOperations.readSynchronousDataWithoutSyncWithServer(bearingData, operationType));
            }
        }
    }


    /**
     * Asynchronous response read operation.
     *
     * @param syncWithServer       the sync with server
     * @param bearingConfiguration the bearingConfiguration
     * @param bearingData          the bearing data
     * @param bearingCallBack      the bearing call back
     */
    public void asynchronousResponseReadOperation(boolean syncWithServer, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingCallBack bearingCallBack) {
        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);
        final String requestId = String.valueOf(UUID.randomUUID());

        if (syncWithServer) {

            final RequestReadEvent asyncRequestReadEvent = requestForNonSyncResponseWithSyncWithServer(requestId, operationType, bearingData, bearingCallBack);
            bearingHandler.enqueue(requestId, asyncRequestReadEvent, EventType.ASYNC_READ);
        } else {
            final RequestReadEvent asyncRequestReadEvent = requestForNonSyncRespWithoutSyncServer(requestId, operationType, bearingData, bearingCallBack);
            bearingHandler.enqueue(requestId, asyncRequestReadEvent, EventType.ASYNC_READ);
        }
    }

    private RequestReadEvent requestForNonSyncRespWithoutSyncServer(String requestId, BearingConfiguration.OperationType operationType, BearingData bearingData, BearingCallBack bearingCallBack) {
        RequestReadEvent requestReadEvent = new RequestReadEvent(requestId, EventType.ASYNC_READ, bearingCallBack);
        if (BearingConfiguration.OperationType.READ_SITE_LIST.equals(operationType)) {
            requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_SITES_LOCAL);
            return requestReadEvent;
        } else if (BearingConfiguration.OperationType.READ_LOC_LIST.equals(operationType)) {
            String siteName = null;
            requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_LOCATIONS_LOCAL);
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestReadEvent.setSiteName(siteName);
            return requestReadEvent;
        }
        return requestReadEvent;
    }

    private RequestReadEvent requestForNonSyncResponseWithSyncWithServer(String requestId, BearingConfiguration.OperationType operationType, BearingData bearingData, BearingCallBack bearingCallBack) {
        RequestReadEvent requestReadEvent = new RequestReadEvent(requestId, EventType.ASYNC_READ, bearingCallBack);
        if (BearingConfiguration.OperationType.READ_SITE_LIST.equals(operationType)) {
            requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_SITES_SYNC_WITH_SERVER);
            return requestReadEvent;

        } else if (BearingConfiguration.OperationType.READ_SITE_LIST_ON_SERVER.equals(operationType)) {
            requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_SITES_FROM_SERVER);
            return requestReadEvent;
        } else if (BearingConfiguration.OperationType.READ_LOC_LIST.equals(operationType)) {
            String siteName = null;
            requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_LOCATIONS_SYNC_WITH_SERVER);
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestReadEvent.setSiteName(siteName);
            return requestReadEvent;
        } else if (BearingConfiguration.OperationType.READ_LOC_LIST_ON_SERVER.equals(operationType)) {
            String siteName = null;
            requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_LOCATIONS_FROM_SERVER);
            if (bearingData != null && bearingData.getSiteMetaData() != null) {
                siteName = bearingData.getSiteMetaData().getSiteName();
            }
            requestReadEvent.setSiteName(siteName);
            return requestReadEvent;
        }

        return requestReadEvent;

    }


    private BearingOutput buildBearingOutputForRead(List<String> stringList) {

        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.OK);
        final Body body = new Body();
        body.setResponseList(stringList);
        bearingOutput.setHeader(header);
        bearingOutput.setBody(body);
        return bearingOutput;
    }

    private BearingOutput buildBearingOutputForRead(boolean readStatus) {
        final BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        if (readStatus) {
            header.setStatusCode(StatusCode.OK);
        } else {
            header.setStatusCode(StatusCode.BAD_REQUEST);
        }
        bearingOutput.setHeader(header);
        return bearingOutput;
    }

    /**
     * Download source id map.
     *
     * @param bearingCallBack the bearing call back
     */
    public void downloadSourceIdMap(BearingCallBack bearingCallBack) {
        String uuid = String.valueOf(UUID.randomUUID());
        RequestRetrieveEvent requestRetrieveEvent = new RequestRetrieveEvent(uuid, EventType.ASYNC_RETRIEVE, bearingCallBack);
        requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.SOURCE_ID_MAP);
        bearingHandler.enqueue(uuid, requestRetrieveEvent, EventType.ASYNC_RETRIEVE);
    }

    public void downloadSiteThreshData(String siteName, BearingCallBack bearingCallBack) {
        String uuid = String.valueOf(UUID.randomUUID());
        RequestRetrieveEvent requestRetrieveEvent = new RequestRetrieveEvent(uuid, EventType.ASYNC_RETRIEVE, bearingCallBack);
        requestRetrieveEvent.setFetchRequest(RequestRetrieveEvent.ServerFetch.THRESH_SOURCE_ID_MAP);
        bearingHandler.enqueue(uuid, requestRetrieveEvent, EventType.ASYNC_RETRIEVE);
    }

}
