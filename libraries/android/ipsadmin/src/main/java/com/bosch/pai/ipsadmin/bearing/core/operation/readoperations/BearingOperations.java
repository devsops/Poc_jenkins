package com.bosch.pai.ipsadmin.bearing.core.operation.readoperations;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestReadEvent;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestRetrieveEvent;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestUpdateEvent;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestUploadEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.location.LocationTrainerUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingRESTClient;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestUploadEvent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The type Bearing read operation.
 */
public class BearingOperations {
    private SiteDetectorUtil detectSite;
    private LocationDetectorUtil locationDetector;
    private SiteTrainUtil siteTrainUtil;
    private LocationTrainerUtil locationTrainerUtil;
    private static final String TAG = BearingOperations.class.getSimpleName();
    private BearingOperationCallback bearingOperationCallback;
    private BearingRESTClient bearingRestClient;
    private String transactionId = null;
    private SynchronousServerCalls synchronousServerCalls;
    private List<String> downloadedSiteList;
    private UploadOperations uploadOperations;


    /**
     * Instantiates a new Bearing read operation.
     */
    public BearingOperations() {
        this.detectSite = new SiteDetectorUtil();
        this.locationDetector = new LocationDetectorUtil();
        bearingRestClient = BearingRESTClient.getInstance();
        this.siteTrainUtil = new SiteTrainUtil();
        this.locationTrainerUtil = new LocationTrainerUtil();
        synchronousServerCalls = new SynchronousServerCalls(bearingRestClient, detectSite, locationDetector);
        uploadOperations = new UploadOperations(bearingRestClient);
    }

    /**
     * Register listener.
     *
     * @param bearingOperationCallback the bearing read callback
     */
    public void registerListener(BearingOperationCallback bearingOperationCallback) {
        this.bearingOperationCallback = bearingOperationCallback;
        uploadOperations.registerBearingOperationCallback(bearingOperationCallback);
    }

    /**
     * Sets transaction id.
     *
     * @param transactionId the transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Sets the URL for server IP TO Upload and download
     *
     * @param serverUrl : server url passed as a string
     * @return the server url endpoint
     */
    public boolean setServerUrlEndpoint(String serverUrl, InputStream httpsCertificateInputStream) {

        if (serverUrl == null) {
            return false;
        }

        final boolean isValidUrl = serverUrl.trim().endsWith("/");
        if (!isValidUrl) {
            return false;
        }
        final String serverUrlToEdit = serverUrl.trim();
        final ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration();


        if (configurationSettings.withServerURL(serverUrlToEdit) == null) {
            return false;
        } else {
            bearingRestClient.setHttpsCertificate(httpsCertificateInputStream);
            return ConfigurationSettings.saveConfigObject(configurationSettings.withServerURL(serverUrlToEdit));
        }


    }

    /**
     * Read synchronous data without sync with server list.
     *
     * @param bearingData   the site name encapsulated with siteName
     * @param operationType the operation type
     * @return the list
     */


    public List<String> readSynchronousDataWithoutSyncWithServer(BearingData bearingData, BearingConfiguration.OperationType operationType) {
        String siteName;
        if (bearingData == null) {
            siteName = "";
        } else {
            siteName = bearingData.getSiteMetaData().getSiteName();
        }

        switch (operationType) {
            case READ_SITE_LIST:
                return detectSite.getSiteNames();
            case READ_LOC_LIST:
                List<String> locationNames = locationDetector.getLocationNamesFromClusterData(siteName);
                List<String> locationNamesFromCluster = locationDetector.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT);
                locationNamesFromCluster.addAll(locationNames);
                return locationNamesFromCluster;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "readSynchronousDataWithoutSyncWithServer: Unsupported read operation");
                return new ArrayList<>();
        }
    }

    /**
     * Read synchronous data after sync with server list.
     *
     * @param bearingData   the site name encapsulated in bearingData
     * @param operationType the operation type
     * @return the list
     */
    public List<String> readSynchronousDataAfterSyncWithServer(BearingData bearingData, BearingConfiguration.OperationType operationType) {
        String siteName = "";
        if (bearingData == null) {
            siteName = "";
        } else {
            siteName = bearingData.getSiteMetaData().getSiteName();
        }

        switch (operationType) {
            case READ_SITE_LIST:
                return synchronousServerCalls.getAllSitesSyncWithServer(true);
            case READ_LOC_LIST:
                return synchronousServerCalls.getAllLocationsSyncWithServer(siteName, true);
            case READ_SITE_LIST_ON_SERVER:
                return synchronousServerCalls.getAllSitesSyncWithServer(false);
            case READ_LOC_LIST_ON_SERVER:
                return synchronousServerCalls.getAllLocationsSyncWithServer(siteName, false);
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "readSynchronousDataAfterSyncWithServer: Unsupported operation");
                return new ArrayList<>();
        }

    }

    /**
     * This method is introduced to handle the differentiation in code for the read and retrive functionality
     *
     * @param bearingData   the bearing data
     * @param operationType the operation type
     * @return the response
     */
    public Response retrieveSynchronousDataAfterSyncWithServer(BearingData bearingData, BearingConfiguration.OperationType operationType, boolean isPersist) {
        String siteName = "";
        if (bearingData == null) {
            siteName = "";
        } else {
            siteName = bearingData.getSiteMetaData().getSiteName();
        }

        switch (operationType) {
            case READ_SITE_LIST:
                final List<String> allSitesSyncWithServer = synchronousServerCalls.getAllSitesSyncWithServer(isPersist);
                final Response readSiteListResponse = new Response(operationType);
                readSiteListResponse.setStringsList(allSitesSyncWithServer);
                return readSiteListResponse;
            case READ_LOC_LIST:
                final List<String> allLocationsSynWithServer = synchronousServerCalls.getAllLocationsSyncWithServer(siteName, isPersist);
                final Response readLocationListResponse = new Response(operationType);
                readLocationListResponse.setStringsList(allLocationsSynWithServer);
                return readLocationListResponse;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "readSynchronousDataAfterSyncWithServer: Unsupported operation");
                return null;
        }

    }


    /**
     * Retrieve synchronous data without sync with server list.
     *
     * @param bearingData   the site name encapsulated with siteName
     * @param operationType the operation type
     * @return the list
     */
    public Response retrieveSynchronousDataWithoutSyncWithServer(BearingData bearingData, BearingConfiguration.OperationType operationType) {
        String siteName;
        if (bearingData == null) {
            siteName = "";
        } else {
            siteName = bearingData.getSiteMetaData().getSiteName();
        }

        Response response = null;
        switch (operationType) {
            case READ_SITE_LIST:
                response = new Response(operationType);
                final List<String> siteList = new ArrayList<>(siteTrainUtil.getSiteNames() != null ?
                        siteTrainUtil.getSiteNames() : Collections.<String>emptyList());
                response.setStringsList(siteList);
                break;
            case READ_LOC_LIST:
                final List<String> locationNames = locationTrainerUtil.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT);
                final List<String> locationNamesFromClusterData = locationTrainerUtil.getLocationNamesFromClusterData(siteName);
                locationNames.addAll(locationNamesFromClusterData);
                response = new Response(operationType);
                response.setStringsList(locationNames);
                break;
            case READ_THRESH_LIST:
                final List<String> locationNames1 = locationTrainerUtil.getLocationNames(siteName, BearingConfiguration.Approach.THRESHOLDING);
                final List<String> locationNames2 = locationTrainerUtil.getLocationNamesFromBleThreshData(siteName);
                locationNames1.addAll(locationNames2);
                response = new Response(operationType);
                response.setStringsList(locationNames1);
                break;
            case SNAPSHOT_FETCH:
                final List<SnapshotObservation> snapshotObservations = siteTrainUtil.retrieveSiteData(siteName);
                response = new Response(operationType);
                response.setSnapshotObservations(snapshotObservations);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "readSynchronousDataWithoutSyncWithServer: Unsupported read operation");
        }
        return response;

    }


    /**
     * Updates data after updating the value on the server
     *
     * @param bearingDataOld the old bearing data present on he server
     * @param bearingDataNew the new bearingDaa passed to update server with
     * @param operationType  specifies the type of update operation to be performed.
     * @return the boolean
     */
    public boolean updateSynchronousDataAfterSyncWithServer(BearingData bearingDataOld, BearingData bearingDataNew, BearingConfiguration.OperationType operationType) {

        final String siteNameOld = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataOld, BearingMode.SITE);
        final String siteNameNew = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataNew, BearingMode.SITE);

        switch (operationType) {

            case SITE_RENAME:
                return synchronousServerCalls.siteRenameSynchronousServerCall(siteNameOld, siteNameNew);
            case SITE_UPDATE_ON_MERGE:
                //TODO not covered in the present scope (SPRINT 23- TASK(BEA-521))
                return false;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSynchronousDataAfterSyncWithServer: Unsupported operation");
                return false;
        }
    }


    /**
     * Updates data after updating the value on the server
     *
     * @param bearingDataOld the old bearing data present on the server
     * @param bearingDataNew the new bearingDaa passed to update server with
     * @param operationType  specifies the type of update operation to be performed.
     * @return the boolean
     */
    public boolean updateSynchronousDataWithoutSyncWithServer(BearingData bearingDataOld, BearingData bearingDataNew, BearingConfiguration.OperationType operationType) {
        boolean result = false;
        switch (operationType) {
            case SITE_RENAME:
                result = updateSiteName(bearingDataOld, bearingDataNew);
                break;
            case SITE_UPDATE_ON_MERGE:
                result = writeOrUpdateSnapshotForSite(bearingDataOld, bearingDataNew, true);
                break;
            case SNAPSHOT_UPDATE:
                result = writeOrUpdateSnapshotForSite(bearingDataOld, bearingDataNew, false);
                break;
            case UPDATE_SITE_CONFIG:
                result = updateSiteConfig(bearingDataOld, bearingDataNew);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSynchronousDataWithoutSyncWithServer: Unsupported operation for update");
                break;
        }
        return result;
    }

    private boolean updateSiteConfig(BearingData bearingDataOld, BearingData bearingDataNew) {
        if (bearingDataOld.getSiteMetaData() == null || bearingDataNew.getSiteMetaData() == null)
            return false;
        if (!bearingDataOld.getSiteMetaData().getSiteName().equals(bearingDataNew.getSiteMetaData().getSiteName())) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Update can not be called on different site(s)");
            return false;
        }
        final String siteName = bearingDataOld.getSiteMetaData().getSiteName();
        return siteTrainUtil.updateSiteConfig(siteName, bearingDataNew.getSiteMetaData().getRssiThreshHold(), bearingDataNew.getSiteMetaData().getProbabilityPercentage());
    }

    /**
     * Read data with asynchronous response.
     *
     * @param requestReadEvent the request read event
     */
    public void readDataWithAsynchronousResponse(RequestReadEvent requestReadEvent) {
        final UUID requestID = UUID.fromString(requestReadEvent.getRequestID());
        final String siteName = requestReadEvent.getSiteName();

        switch (requestReadEvent.getFetchRequest()) {
            case ALL_SITES_LOCAL:
                readSiteNames(requestID);
                break;
            case ALL_LOCATIONS_LOCAL:
                readLocationNames(requestID, siteName);
                break;
            case ALL_SITES_SYNC_WITH_SERVER:
                getAllSiteNamesAfterSyncWithServer();
                break;
            case ALL_LOCATIONS_SYNC_WITH_SERVER:
                getAllLocationNamesAfterSyncWithServer(siteName);
                break;
            case ALL_SITES_FROM_SERVER:
                getAllSiteNamesOnlyFromServer();
                break;
            case ALL_LOCATIONS_FROM_SERVER:
                getAllLocationNamesOnlyFromServer(siteName);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "readDataWithAsynchronousResponse: Unsupported read Operation");
        }

    }

    /*Reads the location names which is the combination of .csv files and content of cluster data */
    private void readLocationNames(UUID requestID, String siteName) {
        final List<String> locationNames = locationDetector.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT);
        final List<String> locationNamesFromCluster = locationDetector.getLocationNamesFromClusterData(siteName);
        locationNames.addAll(locationNamesFromCluster);

        bearingOperationCallback.onDataReceived(requestID, locationNames);
    }

    private void readSiteNames(UUID requestID) {
        final List<String> siteNames = detectSite.getSiteNames();
        if (siteNames != null) {
            bearingOperationCallback.onDataReceived(requestID, siteNames);
        } else {
            bearingOperationCallback.onDataReceivedError(requestID, BearingOperationCallback.Error);
        }
    }


    /**
     * Read data with asynchronous response.
     *
     * @param requestRetrieveEvent the request read event
     */
    public void retrieveDataWithAsynchronousResponse(RequestRetrieveEvent requestRetrieveEvent) {
        final UUID requestID = UUID.fromString(requestRetrieveEvent.getRequestID());
        final String siteName = requestRetrieveEvent.getSiteName();

        switch (requestRetrieveEvent.getFetchRequest()) {
            case ALL_SITES_LOCAL:
                readSiteNamesFromLocal(requestID);
                break;
            case ALL_LOCATIONS_LOCAL:
                readLocationNamesFromLocal(requestID, siteName);
                break;
            case ALL_THRESH_LOCAL:
                readThreshLocationNamesFromLocal(requestID, siteName);
                break;
            case ALL_SITES_SYNC_WITH_SERVER:
                getAllSiteNamesAfterSyncWithServer();
                break;
            case ALL_LOCATIONS_SYNC_WITH_SERVER:
                getAllLocationNamesAfterSyncWithServer(siteName);
                break;
            case ALL_SITE_NAMES_FROM_SERVER:
                getAllSiteNamesOnlyFromServer();
                break;
            case ALL_LOCATION_NAMES_FROM_SERVER:
                getAllLocationNamesOnlyFromServer(siteName);
                break;
            case SOURCE_ID_MAP:
                downloadSourceIdMapFromServer();
                break;
            case THRESH_SOURCE_ID_MAP:
                downloadThreshSourceIdMapFromServer(siteName);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "readDataWithAsynchronousResponse: Unsupported read Operation");
        }

    }

    private void downloadThreshSourceIdMapFromServer(final String siteName) {
        bearingRestClient.downloadSiteThreshData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
                List<String> locNames = persistenceHandler.getThreshLocationNames(siteName);
                bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), locNames);
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), "Error downloading site thresh data");
            }
        });
    }

    private void downloadSourceIdMapFromServer() {
        bearingRestClient.downloadSourceIdMap(new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), Collections.<String>emptyList());
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), "Error downloading sourceIdMap");
            }
        });
    }

    private void readThreshLocationNamesFromLocal(UUID requestID, String siteName) {
        final List<String> threshLocationNames = locationDetector.getLocationNames(siteName, BearingConfiguration.Approach.THRESHOLDING);
        if (threshLocationNames != null) {
            bearingOperationCallback.onDataReceived(requestID, threshLocationNames);
        } else {
            bearingOperationCallback.onDataReceivedError(requestID, BearingOperationCallback.Error);
        }
    }

    private void readLocationNamesFromLocal(UUID requestID, String siteName) {
        final List<String> locationNamesFromCluster = locationDetector.getLocationNamesFromClusterData(siteName);
        final List<String> locationNames = locationDetector.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT);
        locationNames.addAll(locationNamesFromCluster);
        bearingOperationCallback.onDataReceived(requestID, locationNames);
    }

    private void readSiteNamesFromLocal(UUID requestID) {
        final List<String> siteNames = detectSite.getSiteNames();
        if (siteNames != null) {
            bearingOperationCallback.onDataReceived(requestID, siteNames);
        } else {
            bearingOperationCallback.onDataReceivedError(requestID, BearingOperationCallback.Error);
        }
    }


    /**
     * Update the parameters asynchronously either after syncing data with server or without
     *
     * @param requestUpdateEvent the event specific for update
     */
    public void updateDataWithAsynchronousResponse(RequestUpdateEvent requestUpdateEvent) {

        final UUID requestID = UUID.fromString(requestUpdateEvent.getRequestID());

        switch (requestUpdateEvent.getFetchRequest()) {

            case SITE_RENAME_LOCAL:
                final String siteNameOld = requestUpdateEvent.getSiteNameOld();
                final String siteNameNew = requestUpdateEvent.getSiteNameNew();
                renameSiteOnLocalStorage(requestID, siteNameOld, siteNameNew);
                break;
            case SIGNAL_MERGE_LOCAL:
                final List<SnapshotObservation> snapshotObservations = requestUpdateEvent.getSnapshotObservations();
                final String siteName = requestUpdateEvent.getSiteNameNew();
                mergeSignals(requestID, siteName, snapshotObservations);
                break;
            case SITE_RENAME_SYNC_WITH_SERVER:
                siteRenameAfterSyncWithServer(requestID, requestUpdateEvent, bearingOperationCallback);
                break;
            case SIGNAL_MERGE_SYNC_WITH_SERVER:
                //TODO functionality not supported.
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "updateDataWithAsynchronousResponse:Functionality not supported");
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateDataWithAsynchronousResponse: Unsupported update operation");
                break;
        }


    }

    private void mergeSignals(UUID requestID, String siteName, List<SnapshotObservation> snapshotObservations) {
        final boolean mergeStatus = siteTrainUtil.addToSite(siteName, snapshotObservations);
        if (mergeStatus) {
            bearingOperationCallback.onDataReceived(requestID, new ArrayList<String>());
        } else {
            bearingOperationCallback.onDataReceivedError(requestID, "Error on merge");
        }
    }

    private void renameSiteOnLocalStorage(UUID requestID, String siteNameOld, String siteNameNew) {
        final boolean renameStatus = siteTrainUtil.renameSite(siteNameOld, siteNameNew);
        if (renameStatus) {
            bearingOperationCallback.onDataReceived(requestID, new ArrayList<String>());
        } else {
            bearingOperationCallback.onDataReceivedError(requestID, "Error on update");
        }
    }

    /**
     * The API is used to upload the site or location data for the corresponding site or location.
     * Each request contains a ServerFetch request Object . This object is used to trigger the corresponding uploads.
     *
     * @param requestUploadEvent the request upload event
     */
    public void uploadDataAsynchronousResponseServerCall(RequestUploadEvent requestUploadEvent) {
        uploadOperations.uploadDataAsynchronousResponseServerCall(requestUploadEvent);
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
        return uploadOperations.uploadSynchronousResponseServerCall(operationType, bearingData, bearingMode);
    }

    private void siteRenameAfterSyncWithServer(final UUID requestId, final RequestUpdateEvent requestUpdateEvent, final BearingOperationCallback bearingOperationCallback) {

        bearingRestClient.renameSiteOnServer(requestUpdateEvent.getSiteNameOld(), requestUpdateEvent.getSiteNameNew(), new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {

                final boolean localRename = siteTrainUtil.renameSite(requestUpdateEvent.getSiteNameOld(), requestUpdateEvent.getSiteNameNew());
                if (localRename) {
                    bearingOperationCallback.onDataReceived(requestId, new ArrayList<String>());
                } else {
                    bearingOperationCallback.onDataReceivedError(requestId, "Error in sync update");
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(requestId, errMessage);

            }
        });


    }


    private void getAllSiteNamesAfterSyncWithServer() {

        bearingRestClient.getAllSiteNames(new BearingClientCallback.GetDataCallback() {
            @Override
            public void onDataReceived(List<String> dataList) {
                downloadedSiteList = new ArrayList<>(dataList);
                if (!downloadedSiteList.isEmpty()) {
                    setNextSiteNameAndDownload(downloadedSiteList.get(0));
                } else {
                    bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), downloadedSiteList);
                }
            }

            @Override
            public void onDataReceivedError(String errorMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onDataReceivedError: " + errorMessage);
                downloadedSiteList = new ArrayList<>();
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), errorMessage);
            }
        });

    }


    private void getAllSiteNamesOnlyFromServer() {

        bearingRestClient.getAllSiteNames(new BearingClientCallback.GetDataCallback() {
            @Override
            public void onDataReceived(List<String> dataList) {
                if (dataList != null) {
                    bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), dataList);
                } else {
                    bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), "Error in Download");
                }
            }

            @Override
            public void onDataReceivedError(String errorMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, " Error in fetching siteNames onDataReceivedError: " + errorMessage);
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), errorMessage);
            }
        });
    }

    private void getAllLocationNamesOnlyFromServer(final String siteName) {

        bearingRestClient.getAllLocationNamesForSite(siteName, new BearingClientCallback.GetDataCallback() {
            @Override
            public void onDataReceived(List<String> dataList) {
                if (dataList != null) {
                    bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), dataList);
                } else {
                    bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), "Error in Download");
                }
            }

            @Override
            public void onDataReceivedError(String errorMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Error in fetching location Names onDataReceivedError: " + errorMessage);
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), errorMessage);

            }
        });


    }


    private void getAllLocationNamesAfterSyncWithServer(final String siteName) {


        bearingRestClient.getClusterData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                getAllLocationNamesOnDownload(siteName);
            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), errMessage);
            }
        });


    }


    /**
     * Trigger training on server
     * API is used to trigger the Thresh and csv upload on the server.*
     * Based on the approach the corresponding training get triggered.
     *
     * @param siteName the site name
     * @param approach the approach
     */
    public void triggerTrainingOnServer(String siteName, BearingConfiguration.Approach approach) {

        bearingRestClient.generateSVMTOnServer(siteName, approach, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), new ArrayList<String>());

            }

            @Override
            public void onRequestFailure(String errMessage) {
                bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), errMessage);

            }
        });


    }


    private void processRequest(final String siteName) {

        bearingRestClient.downloadSiteData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                int siteInList = downloadedSiteList.indexOf(siteName);
                if (siteInList + 1 < downloadedSiteList.size()) {
                    siteInList++;
                    setNextSiteNameAndDownload(downloadedSiteList.get(siteInList));
                } else if (siteInList + 1 == downloadedSiteList.size()) {
                    getAllSiteNamesOnDownload();
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                int siteInList = downloadedSiteList.indexOf(siteName);
                if (siteInList + 1 < downloadedSiteList.size()) {
                    siteInList++;
                    setNextSiteNameAndDownload(downloadedSiteList.get(siteInList));
                }

            }
        });
    }


    private void setNextSiteNameAndDownload(String nextSiteName) {
        processRequest(nextSiteName);
    }

    private void getAllSiteNamesOnDownload() {

        final List<String> siteNamesList = detectSite.getSiteNames();
        if (siteNamesList != null) {
            bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), siteNamesList);
        } else {
            bearingOperationCallback.onDataReceivedError(UUID.fromString(transactionId), "Error in Download");
        }
    }

    private void getAllLocationNamesOnDownload(String siteName) {
        final List<String> locationList = locationDetector.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT);
        final List<String> locationNamesFromCluster = locationDetector.getLocationNamesFromClusterData(siteName);
        locationNamesFromCluster.addAll(locationList);
        bearingOperationCallback.onDataReceived(UUID.fromString(transactionId), locationNamesFromCluster);
    }

    private boolean updateSiteName(BearingData bearingDataOld, BearingData bearingDataNew) {

        final String siteNameNew = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataNew, BearingMode.SITE);
        final String siteNameOld = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataOld, BearingMode.SITE);

        if (siteNameNew == null || siteNameOld == null) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSiteName: No valid site Name in request input");
            return false;
        }

        final boolean updateStatus = siteTrainUtil.renameSite(siteNameOld, siteNameNew);

        return updateStatus;
    }

    private boolean writeOrUpdateSnapshotForSite(BearingData bearingDataOld, BearingData bearingDataNew, boolean isUpdateSnapshotObservation) {

        final String siteNameOld = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingDataOld, BearingMode.SITE);
        final List<SnapshotObservation> snapshotObservationsToMerge = BearingRequestParser.parseBearingDataForSnapshotObservation(bearingDataNew, BearingMode.SITE);
        final int floors = BearingRequestParser.parseBearingDataForFloorCount(bearingDataNew);

        if (siteNameOld == null && snapshotObservationsToMerge == null && floors == 0) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSiteName: No valid site Name || SnapshotObservation in request input");
            return false;
        }
        if (isUpdateSnapshotObservation) {
            return siteTrainUtil.addToSite(siteNameOld, snapshotObservationsToMerge);
        } else {
            boolean status = siteTrainUtil.addTrainedSiteData(siteNameOld, floors, snapshotObservationsToMerge);
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "writeOrUpdateSnapshotForSite: status" + status);
            return status;

        }
    }


    /**
     * The type Response.
     */
    public static class Response {

        private List<SnapshotObservation> snapshotObservations;
        private List<String> stringsList;
        private BearingConfiguration.OperationType operationType;

        /**
         * Instantiates a new Response.
         *
         * @param operationType the operation type
         */
        public Response(BearingConfiguration.OperationType operationType) {
            this.operationType = operationType;
        }

        /**
         * Gets operation type.
         *
         * @return the operation type
         */
        public BearingConfiguration.OperationType getOperationType() {
            return operationType;
        }

        /**
         * Gets snapshot observations.
         *
         * @return the snapshot observations
         */
        public List<SnapshotObservation> getSnapshotObservations() {
            return Collections.unmodifiableList(snapshotObservations);
        }

        /**
         * Sets snapshot observations.
         *
         * @param snapshotObservations the snapshot observations
         */
        public void setSnapshotObservations(List<SnapshotObservation> snapshotObservations) {
            this.snapshotObservations = snapshotObservations != null ? new ArrayList<>(snapshotObservations) :
                    new ArrayList<SnapshotObservation>();
        }

        /**
         * Gets strings list.
         *
         * @return the strings list
         */
        public List<String> getStringsList() {
            return Collections.unmodifiableList(stringsList);
        }

        /**
         * Sets strings list.
         *
         * @param stringsList the strings list
         */
        public void setStringsList(List<String> stringsList) {
            this.stringsList = stringsList != null ? new ArrayList<>(stringsList) : new ArrayList<String>();
        }


    }


}
