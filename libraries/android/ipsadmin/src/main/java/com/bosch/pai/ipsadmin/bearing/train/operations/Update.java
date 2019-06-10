package com.bosch.pai.ipsadmin.bearing.train.operations;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestUpdateEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.enums.EventType;

import java.util.UUID;


/**
 * All updates to Bearing is via this api.(Supports both synchronous response and asynchronous response)
 * The main functionality  exposed :
 * 1.RENAME SITE
 * 2.SIGNAL MERGE for site with already existing site Snapshot.
 * Update Operation is designed to work with 4 combinations of syncWithServer and BearingCallback.
 * 1. Sync with server : <b>TRUE</b> and Callback :<b>NONNULL</b>
 * 2. Sync with server :<b>FALSE</b> and Callback :<b>NONNULL</b>
 * 3. Sync with server :<b>TRUE</b> and Callback :<b>NULL</b>
 * 4. Sync with server :<b>FALSE</b> and Callback :<b>NULL</b>
 * (1 & 2) are for synchronous responses
 * (3& 4) are for asynchronous responses.
 * ******************************************************************
 * Rename site On device(synchronous and asynchronous response)
 * SyncWithServer -> false
 * Operation_Type ->SITE_RENAME
 * bearingDataOld -> Pass the old siteName in siteMetaData BearingData object.
 * bearingDataNew -> Pass the new siteName in siteMetaData BearingData object.
 * ******************************************************************
 * Rename site on Server and device(synchronous and asynchronous response)
 * SyncWithServer ->true
 * OperationType ->SITE_RENAME
 * bearingDataOld -> Pass the old siteName in siteMetaData BearingData object.
 * bearingDataNew -> Pass the new siteName in siteMetaData BearingData object.
 * ******************************************************************
 * Merge signals for site (only asynchronous response)
 * SyncWithServer -> false (syncWithServer true is not supported)
 * OperationType -> SITE_UPDATE_ON_MERGE
 * bearingDataOld-> null
 * bearingDataNew-> Pass the old siteName in siteMataData and the merging snapshotObservations in siteMetaData BearingData object.
 * *******************************************************************
 * Site Snapshot update externally(only synchronous response)
 * SynWithServer ->false (syncWithServer true not supported)
 * OperationType ->SNAPSHOT_UPDATE
 * bearingDataOld -> site Name to be passed as siteName in BearingData for which the snapshot has to be updated.
 * bearingDataNew -> siteName along with the new List<SnapshotObservation> to be passed to update the snapshot.
 * ********************************************************************
 * Floor update (only synchronous response)
 * SyncWithServer ->false(syncWithServer true not supported)
 * OperationType ->SNAPSHOT_UPDATE
 * bearingDataOld ->siteName to be passed as siteName in BearingData for which the snapshot has to be updated.
 * bearingDataNew ->noOfFloors to be passed in siteMetaData in BearingData for which the snapshot will be updated to.
 * *********************************************************************
 */
public class Update {
    private BearingOperations bearingOperations;
    private BearingHandler bearingHandler;

    /**
     * Instantiates a new Update.
     *
     * @param bearingHandler the bearing handler
     */
    public Update(BearingHandler bearingHandler) {
        this.bearingOperations = new BearingOperations();
        this.bearingHandler = bearingHandler;
    }


    /**
     * Synchronous response boolean.
     *
     * @param syncWithServer the sync with server
     * @param bearingDataNew the bearing data new
     * @param bearingDataOld the bearing data old
     * @param operationType  the operation type
     * @return the boolean
     */
    public boolean synchronousResponse(boolean syncWithServer, BearingData bearingDataNew, BearingData bearingDataOld, BearingConfiguration.OperationType operationType) {

        if (syncWithServer) {
            final boolean serverUpdateStatus = bearingOperations.updateSynchronousDataAfterSyncWithServer(bearingDataNew, bearingDataOld, operationType);
            if (!serverUpdateStatus) {
                return false;
            }
        }
        return bearingOperations.updateSynchronousDataWithoutSyncWithServer(bearingDataNew, bearingDataOld, operationType);
    }


    /**
     * Asynchronous response boolean.
     *
     * @param syncWithServer  the sync with server
     * @param bearingDataOld  the bearing data old
     * @param bearingDataNew  the bearing data new
     * @param operationType   the operation type
     * @param bearingCallBack the bearing call back
     * @return the boolean
     */
    public boolean asynchronousResponse(boolean syncWithServer, BearingData bearingDataOld, BearingData bearingDataNew, BearingConfiguration.OperationType operationType, BearingCallBack bearingCallBack) {
        String requestId = String.valueOf(UUID.randomUUID());

        if (syncWithServer) {
            final RequestUpdateEvent asyncRequestUpdateEvent = requestForNonSyncUpdateRespWithSyncServer(requestId, operationType, bearingDataOld, bearingDataNew, bearingCallBack);
            bearingHandler.enqueue(requestId, asyncRequestUpdateEvent, EventType.ASYNC_UPDATE);

        } else {
            final RequestUpdateEvent asyncRequestUpdateEvent = requestForNonSyncUpdateRespWithoutSyncServer(requestId, operationType, bearingDataOld, bearingDataNew, bearingCallBack);
            bearingHandler.enqueue(requestId, asyncRequestUpdateEvent, EventType.ASYNC_UPDATE);
        }

        return false;
    }


    private RequestUpdateEvent requestForNonSyncUpdateRespWithSyncServer(String requestId, BearingConfiguration.OperationType operationType, BearingData bearingDataOld, BearingData bearingDataNew, BearingCallBack bearingCallBack) {
        RequestUpdateEvent requestUpdateEvent = new RequestUpdateEvent(requestId, EventType.ASYNC_UPDATE, bearingCallBack);
        if (BearingConfiguration.OperationType.SITE_RENAME.equals(operationType)) {
            requestUpdateEvent.setFetchRequest(RequestUpdateEvent.ServerFetch.SITE_RENAME_SYNC_WITH_SERVER);
            requestUpdateEvent.setSiteNameOld(BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataOld, BearingMode.SITE));
            requestUpdateEvent.setSiteNameNew(BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataNew, BearingMode.SITE));
            return requestUpdateEvent;
        }
        return requestUpdateEvent;
    }

    private RequestUpdateEvent requestForNonSyncUpdateRespWithoutSyncServer(String requestId, BearingConfiguration.OperationType operationType, BearingData bearingDataOld, BearingData bearingDataNew, BearingCallBack bearingCallBack) {
        RequestUpdateEvent requestUpdateEvent = new RequestUpdateEvent(requestId, EventType.ASYNC_UPDATE, bearingCallBack);
        if (BearingConfiguration.OperationType.SITE_RENAME.equals(operationType)) {
            requestUpdateEvent.setFetchRequest(RequestUpdateEvent.ServerFetch.SITE_RENAME_LOCAL);
            requestUpdateEvent.setSiteNameOld(BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataOld, BearingMode.SITE));
            requestUpdateEvent.setSiteNameNew(BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataNew, BearingMode.SITE));
            return requestUpdateEvent;
        } else if (BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE.equals(operationType)) {
            requestUpdateEvent.setFetchRequest(RequestUpdateEvent.ServerFetch.SIGNAL_MERGE_LOCAL);
            requestUpdateEvent.setSiteNameNew(BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataNew, BearingMode.SITE));
            requestUpdateEvent.setSnapshotObservations(BearingRequestParser.parseBearingDataForSiteSnapshotObservations(bearingDataNew));
            return requestUpdateEvent;
        }
        return requestUpdateEvent;
    }


}
