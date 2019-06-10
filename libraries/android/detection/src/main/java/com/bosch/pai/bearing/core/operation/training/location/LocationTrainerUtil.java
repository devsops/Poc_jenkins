package com.bosch.pai.bearing.core.operation.training.location;

import android.support.annotation.NonNull;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.algorithm.BearingTrainer;
import com.bosch.pai.bearing.algorithm.event.TrainingCalculationEvent;
import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.core.util.Constants;
import com.bosch.pai.bearing.core.util.Helper;
import com.bosch.pai.bearing.core.util.TrainingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SvmClassifierData;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.logger.Logger;
import com.bosch.pai.bearing.logger.datamodel.TrainingLog;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;
import com.bosch.pai.bearing.persistence.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The type Location trainer.
 */
public class LocationTrainerUtil {
    private final ObservationHandlerAndListener observationHandlerAndListener;
    private final String LOG_TAG = LocationTrainerUtil.class.getSimpleName();
    private String currentTrainingSite;
    private String currentTrainingLocation;
    private TrainLocationListener locationTrainListener;
    private Logger logger;
    private UUID transactionId;
    private BearingConfiguration.Approach approach;

    /**
     * Instantiates a new Location trainer.
     */
    public LocationTrainerUtil() {
        this.observationHandlerAndListener = new ObservationHandlerAndListener();
        logger = new Logger("LocationTrainingPerformance");

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
     * Sets location train listener.
     *
     * @param locationTrainListener the location train listener
     */
    public void setLocationTrainListener(TrainLocationListener locationTrainListener) {
        this.locationTrainListener = locationTrainListener;
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
     * Gets location names.
     *
     * @param siteName the site name
     * @param approach the approach
     * @return the location names
     */
    public List<String> getLocationNames(String siteName, BearingConfiguration.Approach approach) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return new ArrayList<>(persistenceHandler.getLocationNames(siteName, approach));
    }

    /**
     * Gets site names.
     *
     * @return the site names
     */
    public List<String> getSiteNames() {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return new ArrayList<>(persistenceHandler.getSiteNames());
    }

    /**
     * Clear location data boolean.
     *
     * @param siteName     the site name
     * @param locationName the location name
     * @return the boolean
     */
    public boolean clearLocationData(String siteName, String locationName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.deleteDataPersistenceSpace(siteName, locationName);
    }

    /**
     * Add location to site.
     *
     * @param transactionId  the transaction id
     * @param siteName       the site name
     * @param locationName   the location name
     * @param initAutoMerge  initAutoMerge
     * @param sensorTypeList the sensor type list
     */
    public void addLocationToSite(UUID transactionId, String siteName, String locationName, boolean initAutoMerge, List<BearingConfiguration.SensorType> sensorTypeList) {

        this.transactionId = transactionId;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final PersistenceResult isCreated = persistenceHandler.createLocationDataSpace(siteName, locationName);
        if (isCreated == PersistenceResult.RESULT_CANCEL) {
            if (locationTrainListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Site not exists to add location. Site :: " + siteName + " " + Constants.SITE_NOT_EXIST);
                locationTrainListener.onDataRecordError(transactionId, approach, Constants.SITE_NOT_EXIST);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "There is not a callback registered to receive location detection information. Register one using TrainLocationMode.registerLocationTrainListener");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Site not exists to add location. Site :: " + siteName + " " + Constants.SITE_NOT_EXIST);
            }
            return;
        }
        if (isCreated == PersistenceResult.PERMISSION_DENIED) {
            if (locationTrainListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the location " + Constants.LOCATION_ALREADY_EXISTS);
                locationTrainListener.onDataRecordError(transactionId, approach, Constants.LOCATION_ALREADY_EXISTS);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get train locale callbacks");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the location " + Constants.SITE_NOT_EXIST);
            }
            return;
        }
        if (initAutoMerge) {
            persistenceHandler.deleteDataPersistenceSpace(siteName, locationName);
            initAutoMerge(transactionId, siteName, locationName, sensorTypeList);
            return;
        }
        final List<SnapshotObservation> snapshotObservations = persistenceHandler.readSnapShot(siteName).getSensors();
        if (snapshotObservations == null || snapshotObservations.isEmpty()) {
            persistenceHandler.deleteDataPersistenceSpace(siteName, locationName);
            if (locationTrainListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the location " + Constants.SITE_DATA_NOT_EXIST);
                locationTrainListener.onDataRecordError(transactionId, approach, Constants.SITE_DATA_NOT_EXIST);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get train locale callbacks");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the location :: " + locationName + " :: " + Constants.SITE_DATA_NOT_EXIST);
            }
            return;
        }
        final List<SnapshotItem> tempList1 = new LinkedList<>();
        for (BearingConfiguration.SensorType sensorType : sensorTypeList) {
            for (SnapshotObservation snapshotObservation : snapshotObservations) {
                if (snapshotObservation.getSensorType() == sensorType) {
                    tempList1.addAll(snapshotObservation.getSnapShotItemList());
                    break;
                }
            }
        }

        removeBLEIdsNotMatchingMacAddressFormat(tempList1);

        if (tempList1.isEmpty()) {
            persistenceHandler.deleteDataPersistenceSpace(siteName, locationName);
            if (locationTrainListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the location " + Constants.SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST);
                locationTrainListener.onDataRecordError(transactionId, approach, Constants.SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get train locale callbacks");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err training the location :: " + locationName + " :: " + Constants.SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST);
            }
            return;
        }
        Helper.getInstance().setSnapShotItems(tempList1);
        Helper.getInstance().setSnapshotSize(tempList1.size());

        /*    *//*Compute the number of measured values in the snapshotItems*- dynamic computation/
           *//*
        int tempListMeasuredVal = 0;
        for (int i = 0; i < tempList1.size(); i++) {
            tempListMeasuredVal += tempList1.get(i).getMeasuredValues().length;
        }
            *//*Add the snapshotItem lis size in helper*//*
        if (tempListMeasuredVal != 0) {
            Helper.getInstance().setSnapshotSize(tempListMeasuredVal);
        }*/

        final boolean isSourceAdded = observationHandlerAndListener.addObservationSource(transactionId, sensorTypeList, true);
        if (!isSourceAdded) {
            persistenceHandler.deleteDataPersistenceSpace(siteName, locationName);
            if (locationTrainListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Sensor not enabled !!! Sensor :: " + sensorTypeList);
                locationTrainListener.onDataRecordError(transactionId, approach, Constants.SENSOR_NOT_ENABLED);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get location detection info.");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Sensor not enabled !!! Sensor :: " + sensorTypeList + " " + Constants.SENSOR_NOT_ENABLED);
            }
            return;
        }
        final RequestDataHolder requestDataHolder = new RequestDataHolder(transactionId, RequestDataHolder.ObservationDataType.LOCATION_DATA_RECORD,
                observationHandlerAndListener);
        requestDataHolder.setSiteName(siteName);
        requestDataHolder.setLocationName(locationName);
        requestDataHolder.setApproach(approach);
        BearingHandler.addRequestToRequestDataHolderMap(requestDataHolder);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.INFO, LOG_TAG, "Started location record");
        final TrainingLog trainingLog = new TrainingLog(currentTrainingLocation);
        logger.log(TrainingLog.At.START, trainingLog);
    }

    private void removeBLEIdsNotMatchingMacAddressFormat(List<SnapshotItem> tempList1) {
        final String macRegex = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        final List<SnapshotItem> removeItems = new ArrayList<>();
        for(SnapshotItem snapshotItem : tempList1) {
            if(!snapshotItem.getSourceId().matches(macRegex)){
                removeItems.add(snapshotItem);
            }
        }
        tempList1.removeAll(removeItems);
    }

    private void initAutoMerge(UUID transactionId, String siteName, String locationName, List<BearingConfiguration.SensorType> sensorTypeList) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        persistenceHandler.updateFingerPrintData(siteName);
        addLocationToSite(transactionId, siteName, locationName, false, sensorTypeList);
    }


    /**
     * Retrain location.
     *
     * @param transactionId  the transaction id
     * @param siteName       the site name
     * @param locationName   the location name
     * @param sensorTypeList the sensor type list
     */
    public void retrainLocation(UUID transactionId, String siteName, String locationName, List<BearingConfiguration.SensorType> sensorTypeList) {
        final boolean isDeleted = clearLocationData(siteName, locationName);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.INFO, LOG_TAG, "Deleting old data for location: " + locationName + " IsDeleted: " + isDeleted);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.INFO, LOG_TAG, "Retraining location to capture fingerPrint data");
        addLocationToSite(transactionId, siteName, locationName, true, sensorTypeList);
    }

    /**
     * Write data to persistence.
     *
     * @param close the close
     */
    public void writeDataToPersistence(boolean close) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        if (close) {
            final PersistenceResult isSuccess = persistenceHandler.appendFingerPrintData(currentTrainingSite, currentTrainingLocation, Helper.getInstance().getRecordBuffer());
            if (isSuccess == PersistenceResult.RESULT_OK) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Data recording completed successfully !!");

                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Site name : " + currentTrainingSite + " Location name : " + currentTrainingLocation);
                if (locationTrainListener != null) {
                    locationTrainListener.onDataRecordingCompleted(transactionId, approach, currentTrainingSite, currentTrainingLocation);
                } else {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get train locale callbacks");
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Site name : " + currentTrainingSite + " Location name : " + currentTrainingLocation);
                }
                final TrainingLog trainingLog = new TrainingLog(currentTrainingLocation);
                logger.log(TrainingLog.At.STOP, trainingLog);

            }
        } else {
            final PersistenceResult isSuccess = persistenceHandler.appendFingerPrintData(currentTrainingSite, currentTrainingLocation, Helper.getInstance().getRecordBuffer());
            final int count = Helper.getInstance().getRecordCount();
            if (locationTrainListener != null) {
                locationTrainListener.onDataRecordProgress(transactionId, approach, count);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get train locale callbacks");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "onDataRecordProgress :: " + count);
            }
            if (isSuccess == PersistenceResult.RESULT_OK) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Buffer write successful!!");
            }

        }
    }


    /**
     * Execute training.
     *
     * @param transactionId the transaction id
     * @param siteName      the site name
     */
    public void executeTraining(UUID transactionId, final String siteName) {
        this.transactionId = transactionId;
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Set<String> siteNames = persistenceHandler.getSiteNames();
        if (!siteNames.contains(siteName)) {
            if (locationTrainListener != null) {
                locationTrainListener.onDataRecordError(transactionId, approach, Constants.SITE_UNKNOWN);
                return;
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site detection info.");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, Constants.SITE_UNKNOWN);
                return;
            }
        }

        TrainingData.getInstance().initializeTrainDataFileNames();
        TrainingData.getInstance().initializeTrainDataFileLocations();
        TrainingData.getInstance().clearTrainingData();

        final Map<String, String> roomNameToPathMap = new LinkedHashMap<>();
        for (String locationName : persistenceHandler.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT)) {
            //TODO fix needed for this when RawLabledData class is made independent of File IO so that roomNameToPathMap is to be get rid of
            final String path = Util.getStoragePath() + siteName + File.separator + locationName + Constants.LOCATION_FILE_EXTENSION;
            roomNameToPathMap.put(locationName, path);
        }
        if (roomNameToPathMap.isEmpty()) {
            if (locationTrainListener != null) {
                locationTrainListener.onLocationTrainError(transactionId, approach, Constants.NO_LOCATIONS_FOUND);
                return;
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site detection info.");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, Constants.NO_LOCATIONS_FOUND);
                return;
            }
        }
        if (roomNameToPathMap.size() <= 1) {
            if (locationTrainListener != null) {
                locationTrainListener.onLocationTrainError(transactionId, approach, Constants.MINIMUM_TWO_LOCATIONS_NEEDED);
                return;
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Register callback to get site detection info.");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, Constants.MINIMUM_TWO_LOCATIONS_NEEDED);
                return;
            }
        }
        for (Map.Entry<String, String> entry : roomNameToPathMap.entrySet()) {
            TrainingData.getInstance().addTrainDataFileName(entry.getKey());
            TrainingData.getInstance().addTrainDataFileLoc(entry.getValue());
        }
        TrainingData.getInstance().setNumTrainingClasses(TrainingData.getInstance().getTrainDataFileNamesSize());
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, LOG_TAG, "Num of training Classes: " + TrainingData.getInstance().getNumTrainingClasses());

        generateSVMTFromRawData(siteName);
    }


    private void generateSVMTFromRawData(final String siteName) {
        final UUID requestUUID = UUID.randomUUID();
        int numberOfClusters = 3;
        TrainingCalculationEvent trainingCalculationEvent = new TrainingCalculationEvent(requestUUID.toString(), TrainingData.getInstance()
                .getTrainDataFileNames(), siteName, EventType.TRIGGER_TRAINING, BearingTrainer.class.getName(), new EventSender() {
            @Override
            public void reply(String requestID, String reply) {
                if (requestID.equals(requestUUID.toString())) {
                    if (reply.equals(Boolean.TRUE.toString())) {
                        locationTrainListener.onLocationsTrained(transactionId, approach, siteName);
                    } else {
                        locationTrainListener.onLocationTrainError(transactionId, approach, "Error training locations!!");
                    }
                }
            }
        }, numberOfClusters);
        AlgorithmLifeCycleHandler.getInstance().enqueue(requestUUID.toString(), trainingCalculationEvent, EventType.TRIGGER_TRAINING, BearingTrainer.class.getName());
    }

    /**
     * Register sensor observation listener.
     */
    public void registerSensorObservationListener() {
        observationHandlerAndListener.registerSensorObservationListener();
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
     * Gets location finger print data.
     *
     * @param siteName     the site name
     * @param locationName the location name
     * @return the location finger print data
     */
    public Map<String, double[]> getLocationFingerPrintData(String siteName, String locationName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.readLocationFingerPrintData(siteName, locationName);
    }

    /**
     * Gets classifier data.
     *
     * @param siteName the site name
     * @return the classifier data
     */
    public SvmClassifierData getClassifierData(String siteName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.readClassifiers(siteName);
    }

    /**
     * Read location Names from the classifier zip downloaded
     * @param  siteName siteName of interest
     */

    public List<String> getLocationNamesFromClusterData(String siteName) {

        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.getLocationNamesFromClassifier(siteName);
    }

    /**
     * Prepare buffer and save.
     *
     * @param snapshotObservations the snapshot observations
     * @param sensorTypes          the sensor types
     */
    public void prepareBufferAndSave(List<SnapshotObservation> snapshotObservations, List<BearingConfiguration.SensorType> sensorTypes) {
        final ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration();
        final Helper helper = Helper.getInstance();
        if (helper.getRecordCount() == 0) {
            String tempHeader = prepareBufferData(snapshotObservations, sensorTypes, true);
            helper.addToBuffer("S.NO" + "," + tempHeader.substring(1, tempHeader.length() - 1));
            writeDataToPersistence(false);
            helper.clearBuffer();
            helper.setRecordBufferCount(0);
        }
        final String tempArray = prepareBufferData(snapshotObservations, sensorTypes, false);
        final int sampleCount = Double.valueOf(configurationSettings.getAlgorithmConfigurationPreferences().getProperty(Property.Algorithm.FINGERPRINT_SAMPLE_COUNT).toString()).intValue();
        if (helper.getRecordCount() < sampleCount && helper.getRecordBufferCount() < Constants.MAX_ARRAY_SIZE) {
            helper.setRecordCount(helper.getRecordCount() + 1);
            helper.setRecordBufferCount(helper.getRecordBufferCount() + 1);
            helper.addToBuffer(helper.getRecordCount() + "," + tempArray.substring(1, tempArray.length() - 1));
        } else {
            writeDataToPersistence(false);
            helper.clearBuffer();
            helper.setRecordBufferCount(0);
        }
        if (helper.getRecordCount() >= sampleCount) {
            writeDataToPersistence(true);
            helper.clearBuffer();
            helper.setRecordBufferCount(0);
        }
    }

    @NonNull
    private String prepareBufferData(List<SnapshotObservation> rawSnapshotObservations, List<BearingConfiguration.SensorType> sensorTypeList, Boolean isCSVHeader) {
        final List<SnapshotItem> mergedMACAddresses = new LinkedList<>();
        String bufferString;
        Helper helper = Helper.getInstance();
        for (BearingConfiguration.SensorType sensorType : sensorTypeList) {
            for (SnapshotObservation rawObservations : rawSnapshotObservations) {
                if (rawObservations.getSensorType() == sensorType && rawObservations.getSnapShotItemList() != null) {
                    mergedMACAddresses.addAll(rawObservations.getSnapShotItemList());
                    break;
                }
            }
        }
        final double[] rssiList;
        final String[] addressList;
        final int size;
        if (helper.getSnapShotItems() != null) {
            size = helper.getSnapShotItems().size();
            rssiList = new double[size];
            addressList = new String[size];
        } else {
            size = 0;
            rssiList = new double[0];
            addressList = new String[0];
        }
        for (int i = 0; i < size; i++) {
            rssiList[i] = -100.0;
        }
        if (isCSVHeader) {
            for (int cnt = 0; cnt < size; cnt++) {
                addressList[cnt] = helper.getSnapShotItems().get(cnt).getSourceId();
            }
            bufferString = Arrays.toString(addressList);
        } else {
            for (SnapshotItem snapshotItem : mergedMACAddresses) {
                for (int cnt = 0; cnt < size; cnt++) {
                    if (helper.getSnapShotItems().get(cnt).getSourceId().contains(snapshotItem.getSourceId())) {
                        rssiList[cnt] = snapshotItem.getMeasuredValues()[0];
                        break;
                    }
                }
            }
            bufferString = Arrays.toString(rssiList);
        }
        return bufferString;
    }

    /**
     * Sets site name.
     *
     * @param siteName the site name
     */
    public void setSiteName(String siteName) {
        this.currentTrainingSite = siteName;
    }

    /**
     * Sets location name.
     *
     * @param locationName the location name
     */
    public void setLocationName(String locationName) {
        this.currentTrainingLocation = locationName;
    }

    public List<String> getLocationNamesFromBleThreshData(String siteName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        return persistenceHandler.getThreshLocationNames(siteName);
    }
}