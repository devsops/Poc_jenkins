package com.bosch.pai.ipsadmin.bearing.core.operation.training.site;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.ipsadmin.bearing.core.util.Constants;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;
import com.bosch.pai.bearing.util.SnapshotItemManager;
import com.bosch.pai.bearing.util.SnapshotUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The type Site trainer.
 */
public class SiteTrainUtil {

    private final String LOG_TAG = SiteTrainUtil.class.getSimpleName();

    private enum RecordType {
        /**
         * Site record record type.
         */
        SITE_RECORD, /**
         * Site record append record type.
         */
        SITE_RECORD_APPEND
    }

    private ObservationHandlerAndListener observationHandlerAndListener;
    private String siteName;
    private TrainSiteListener siteTrainCallback;
    private UUID transactionId;
    private BearingConfiguration.Approach approach;


    private boolean isAutoMergeEnabled;

    /**
     * Instantiates a new Site trainer.
     */
    public SiteTrainUtil() {
        this.observationHandlerAndListener = new ObservationHandlerAndListener();
    }

    /**
     * Sets site name.
     *
     * @param siteName the site name
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * Sets site train callback.
     *
     * @param siteTrainCallback the site train callback
     */
    public void setSiteTrainCallback(TrainSiteListener siteTrainCallback) {
        this.siteTrainCallback = siteTrainCallback;
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
     * Sets approach.
     *
     * @param approach the approach
     */
    public void setApproach(BearingConfiguration.Approach approach) {
        this.approach = approach;
        observationHandlerAndListener.setApproach(approach);

    }

    /**
     * Sets transaction id.
     *
     * @param transactionId the transaction id
     */
    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Create site.
     *
     * @param transactionId   the transaction id
     * @param siteName        the site name
     * @param noOfFloors      the no of floors
     * @param initObservation the init observation
     * @param sensorTypes     the sensor type
     */
    public void createSite(UUID transactionId, String siteName, int noOfFloors, boolean initObservation, List<BearingConfiguration.SensorType> sensorTypes) {
        this.siteName = siteName;
        this.transactionId = transactionId;
        if (initObservation) {
            observationHandlerAndListener.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_RECORD);
            final boolean sensorStatus = observationHandlerAndListener.addObservationSource(transactionId, sensorTypes, false);
            if (!sensorStatus) {
                if (siteTrainCallback != null) {
                    siteTrainCallback.onTrainError(transactionId, approach, siteName, Constants.SENSOR_NOT_ENABLED);
                } else {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site train callbacks");
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the site " + Constants.SENSOR_NOT_ENABLED);
                }
                return;
            }
            final RequestDataHolder requestDataHolder = new RequestDataHolder(transactionId, RequestDataHolder.ObservationDataType.LOCATION_DATA_RECORD,
                    observationHandlerAndListener);
            requestDataHolder.setSiteName(siteName);
            requestDataHolder.setApproach(approach);
            requestDataHolder.setNoOfFloors(noOfFloors);
            BearingHandler.addRequestToRequestDataHolderMap(requestDataHolder);
        }
    }


    /**
     * scanSensorForSignal is called to initiate a scan for the sensors selected :
     *
     * @param transactionId id to track the transaction
     * @param siteName      the site name
     * @param sensorTypes   sensors for signal merge
     */
    public void scanSensorForSignalMerge(UUID transactionId, String siteName, List<BearingConfiguration.SensorType> sensorTypes) {

        observationHandlerAndListener.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_RECORD_APPEND);
        final boolean sensorStatus = observationHandlerAndListener.addObservationSource(transactionId, sensorTypes, false);
        if (!sensorStatus) {
            if (siteTrainCallback != null) {
                siteTrainCallback.onTrainError(transactionId, approach, siteName, Constants.SENSOR_NOT_ENABLED);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site train callbacks");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the site " + Constants.SENSOR_NOT_ENABLED);
            }
            return;
        }
        final RequestDataHolder requestDataHolder = new RequestDataHolder(transactionId, RequestDataHolder.ObservationDataType.SITE_RECORD_APPEND,
                observationHandlerAndListener);
        requestDataHolder.setApproach(approach);
        requestDataHolder.setSiteName(siteName);
        BearingHandler.addRequestToRequestDataHolderMap(requestDataHolder);

    }

    /**
     * Add to site.
     *
     * @param siteName   the site name
     * @param sensorType the sensor type
     */
    public void addToSite(String siteName, BearingConfiguration.SensorType sensorType) {
        this.siteName = siteName;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Set<String> siteNames = persistenceHandler.getSiteNames();
        if (!siteNames.contains(siteName)) {
            if (siteTrainCallback != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the site " + Constants.SITE_NOT_EXISTS);
                siteTrainCallback.onTrainError(transactionId, approach, siteName, Constants.SITE_NOT_EXISTS);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site train callbacks");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the site " + Constants.SITE_NOT_EXISTS);
            }
            return;
        }
        observationHandlerAndListener.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_RECORD_APPEND);
    }


    /**
     * Add to site boolean.
     *
     * @param siteName                   the site name
     * @param mergedSnapshotObservations the merged snapshot observations
     * @return the boolean
     */
    public boolean addToSite(String siteName, List<SnapshotObservation> mergedSnapshotObservations) {

        this.siteName = siteName;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Set<String> siteNames = persistenceHandler.getSiteNames();
        return siteNames.contains(siteName) &&
                canMergeBLEData(siteName, mergedSnapshotObservations) &&
                appendDataToPersistence(siteName, mergedSnapshotObservations);
    }

    private boolean canMergeBLEData(String siteName, List<SnapshotObservation> snapshotObservations) {
        if(snapshotObservations.isEmpty() || BearingConfiguration.SensorType.ST_BLE != snapshotObservations.get(0).getSensorType())
            return true;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Snapshot snapshot = persistenceHandler.readSnapShot(siteName);
        final List<SnapshotItem> existingSnapshotItems = new SnapshotItemManager().getBLESnapshotItems(snapshot.getSensors());
        final List<SnapshotItem> toMergeSnapshotItems = new ArrayList<>(snapshotObservations.get(0).getSnapShotItemList());
        toMergeSnapshotItems.removeAll(existingSnapshotItems);
        if(existingSnapshotItems.isEmpty() || toMergeSnapshotItems.isEmpty())
            return true;
        final String macRegex = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        if(existingSnapshotItems.get(0).getSourceId().matches(macRegex)){
            return toMergeSnapshotItems.get(0).getSourceId().matches(macRegex);
        } else {
            return !toMergeSnapshotItems.get(0).getSourceId().matches(macRegex);
        }
    }

    /**
     * Add trained site data.
     *
     * @param siteName             the site name
     * @param noOfFloors           the no of floors
     * @param snapshotObservations the snapshot observations
     * @return the boolean
     */
    public boolean addTrainedSiteData(String siteName, int noOfFloors, List<SnapshotObservation> snapshotObservations) {
        this.siteName = siteName;
        Snapshot snapshot;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);

        if (snapshotObservations == null) {
            int noOfFloorsNew;
            if(noOfFloors < 2) {
                noOfFloorsNew = 2;
            } else {
                noOfFloorsNew = noOfFloors;
            }
            snapshot = persistenceHandler.readSnapShot(siteName);
            snapshot.setNoOfFloors(noOfFloorsNew);
        } else if (noOfFloors == -1) {
            snapshot = persistenceHandler.readSnapShot(siteName);
            snapshot.setSensors(snapshotObservations);
        } else {
            snapshot = SnapshotUtil.createSnapshot(snapshotObservations, noOfFloors);
        }

        persistenceHandler.deleteDataPersistenceSpace(siteName, siteName);

        final PersistenceResult isSuccess = persistenceHandler.writeSnapShot(siteName, snapshot);
        if (isSuccess == PersistenceResult.RESULT_OK) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Data added to site : " + siteName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update the snapshot for the number of floors.
     *
     * @param siteName   the site whose snapshot has to be updated.
     * @param noOfFloors the number of floor to update too.
     * @return the boolean
     */


    public boolean updateSnapshotForFloors(String siteName, int noOfFloors) {

        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Snapshot snapshot = persistenceHandler.readSnapShot(siteName);
        snapshot.setNoOfFloors(noOfFloors);
        persistenceHandler.deleteDataPersistenceSpace(siteName);
        final PersistenceResult isSuccess = persistenceHandler.writeSnapShot(siteName, snapshot);
        if (isSuccess == PersistenceResult.RESULT_OK) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Data added to site : " + siteName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clear site data boolean.
     *
     * @param siteName the site name
     * @return the boolean
     */
    public boolean clearSiteData(String siteName) {
        this.siteName = siteName;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.deleteDataPersistenceSpace(siteName);
    }

    /**
     * Gets site names.
     *
     * @return the site names
     */
    public Set<String> getSiteNames() {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.getSiteNames();
    }

    /**
     * Register sensor observation listener.
     */
    public void registerSensorObservationListener() {
        observationHandlerAndListener.registerSensorObservationListener();
    }

    /**
     * Write data to persistence.
     *
     * @param snapshot the snapshot
     */
    public void writeDataToPersistence(Snapshot snapshot) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final PersistenceResult isCreated = persistenceHandler.writeSnapShot(siteName, snapshot);
        switch (isCreated) {
            case RESULT_CANCEL:
                callbackOnTrain(Constants.SITE_ALREADY_EXISTS, false);
                break;
            case PERMISSION_DENIED:
                callbackOnTrain(Constants.PERMISSION_NOT_ENABLED, false);
                break;
            case RESULT_OK:
                callbackOnTrain(Constants.SITE_SUCCESS, true);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, LOG_TAG, "writeDataToPersistence: Unsupported operation in write to persistence");
        }
    }

    private void callbackOnTrain(String constants, boolean success) {
        if (siteTrainCallback != null) {
            if (success) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, LOG_TAG, "Train successful for site : " + siteName);
                siteTrainCallback.onTrainSuccess(transactionId, approach, siteName);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the site " + constants);
                siteTrainCallback.onTrainError(transactionId, approach, siteName, constants);
            }
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site train callbacks");
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Training Error or successful!! " + siteName + constants);
        }

    }

    /**
     * Append data to persistence.
     *
     * @param snapshotObservations the snapshot observations
     */
    private boolean appendDataToPersistence(String siteNameToMerge, final List<SnapshotObservation> snapshotObservations) {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Append data to site record file");
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        PersistenceResult isSuccess = persistenceHandler.appendSnapshotObservations(siteNameToMerge, snapshotObservations);
        if (isSuccess == PersistenceResult.RESULT_OK) {
            return true;
        }
        return false;
    }

    /**
     * Retrieve site data list.
     *
     * @param siteName the site name
     * @return the list
     */
    public List<SnapshotObservation> retrieveSiteData(String siteName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.readSnapShot(siteName).getSensors();
    }

    /**
     * Rename site boolean.
     *
     * @param oldName the old name
     * @param newName the new name
     * @return the boolean
     */
    public boolean renameSite(String oldName, String newName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.renameSite(oldName, newName);
    }


    /**
     * Send merge signals.
     *
     * @param siteName                the site name
     * @param newSnapshotObservations the new snapshot observations
     */
    public void sendMergeSignals(String siteName, List<SnapshotObservation> newSnapshotObservations) {

           /*If automerge boolean is enabled, pass the site data after a scan */
        final List<SnapshotObservation> deltaSnapshotObservations = new LinkedList<>();
        if (!isAutoMergeEnabled) {
            final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
            final Snapshot snapshot = persistenceHandler.readSnapShot(siteName);
            final List<SnapshotObservation> existingSensorsList = snapshot.getSensors();
            deltaSnapshotObservations.addAll(new SnapshotItemManager().
                    computeSnapshotObservationDelta(existingSensorsList, newSnapshotObservations));
        }

        if (siteTrainCallback != null) {
            siteTrainCallback.onSignalMerge(transactionId, approach, siteName, deltaSnapshotObservations);
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "sendMergeSignals: NO callback registered");
        }
    }

    /**
     * Is auto merge enabled boolean.
     *
     * @return the boolean
     */
    public boolean isAutoMergeEnabled() {
        return isAutoMergeEnabled;
    }

    /**
     * Sets auto merge enabled.
     *
     * @param autoMergeEnabled the auto merge enabled
     */
    public void setAutoMergeEnabled(boolean autoMergeEnabled) {
        isAutoMergeEnabled = autoMergeEnabled;
    }

    /**
     * Update site config boolean.
     *
     * @param siteName              the site name
     * @param rssiThreshHold        the rssi thresh hold
     * @param probabilityPercentage the probability percentage
     * @return the boolean
     */
    public boolean updateSiteConfig(String siteName, int rssiThreshHold, int probabilityPercentage) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Snapshot snapshot = persistenceHandler.readSnapShot(siteName);
        snapshot.setRssiThreshHold(rssiThreshHold);
        snapshot.setProbabilityPercentage(probabilityPercentage);
        return persistenceHandler.updateSiteSnapshot(siteName, snapshot);
    }

}
