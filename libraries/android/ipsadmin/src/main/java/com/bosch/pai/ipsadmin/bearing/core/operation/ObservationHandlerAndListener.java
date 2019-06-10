package com.bosch.pai.ipsadmin.bearing.core.operation;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.location.LocationTrainerUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.ipsadmin.bearing.core.util.Constants;
import com.bosch.pai.ipsadmin.bearing.core.util.Helper;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.event.DataCaptureResponseEvent;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservation;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;
import com.bosch.pai.bearing.util.SnapshotUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Observation handler and listener.
 */
public class ObservationHandlerAndListener implements SensorObservationListener {

    private static final String TAG = ObservationHandlerAndListener.class.getName();
    private UUID bearingUUID;
    private UUID sensorObservationUUID;
    private List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
    private RequestDataHolder.ObservationDataType observationDataType;
    private SensorObservation sensorObservation;
    private BearingConfiguration.Approach approach;

    /**
     * Instantiates a new Observation handler and listener.
     */
    public ObservationHandlerAndListener() {
        this.sensorObservation = SensorObservation.getInstance();
    }

    /**
     * Sets observation data type.
     *
     * @param observationDataType the observation data type
     */
    public void setObservationDataType(RequestDataHolder.ObservationDataType observationDataType) {
        this.observationDataType = observationDataType;
    }

    /**
     * Register sensor observation listener.
     */
    public void registerSensorObservationListener() {
        this.sensorObservationUUID = sensorObservation.registerListener(this);
    }

    /**
     * Remove and unregister sensor observation.
     */
    public void removeAndUnregisterSensorObservation(BearingConfiguration.Approach approach) {
        if (sensorObservationUUID != null && sensorTypes != null && !sensorTypes.isEmpty()) {
            sensorObservation.disableActiveMode(sensorObservationUUID, sensorTypes);
            sensorObservation.removeSource(sensorObservationUUID, sensorTypes);/*Check if disable active mode is to be kept*/
            sensorObservation.unregisterListener(sensorObservationUUID);
        }
        BearingHandler.removeRequestFromRequestDataHolderMap(bearingUUID, approach);
    }

    /**
     * Add observation source boolean.
     *
     * @param bearingUUID    the bearing uuid
     * @param sensorTypes    the sensor types
     * @param isActiveModeON the is active mode on
     * @return the boolean
     */
    public boolean addObservationSource(UUID bearingUUID, final List<BearingConfiguration.SensorType> sensorTypes, boolean isActiveModeON) {
        this.sensorTypes = sensorTypes != null ? new ArrayList<>(sensorTypes) : new ArrayList<BearingConfiguration.SensorType>();
        this.bearingUUID = bearingUUID;
        if (observationDataType == RequestDataHolder.ObservationDataType.LOCATION_DATA_RECORD) {
            Helper.getInstance().setRecordCount(0);
        }
        SensorUtil.setShutdown(false);
        final boolean isSourceAdded = sensorObservation.addSource(sensorObservationUUID, this.sensorTypes);
        if (isSourceAdded && isActiveModeON) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!sensorObservation.accessSensorStateToEnableActiveMode(sensorTypes)) {
                            sensorObservation.enableActiveMode(sensorObservationUUID, ObservationHandlerAndListener.this.sensorTypes);
                            break;
                        }
                    }
                }
            }).start();
        }
        return isSourceAdded;
    }

    @Override
    public void onObservationReceived(List<SnapshotObservation> snapshotObservations) {
        final DataCaptureResponseEvent dataCaptureResponseEvent = new DataCaptureResponseEvent(sensorObservationUUID.toString(),
                EventType.CAPTURE_DATA_RESP,
                BearingResponseAggregator.getInstance().getTrainingCallbackMap().get(bearingUUID.toString()));
        dataCaptureResponseEvent.setSnapshotObservations(snapshotObservations);
        dataCaptureResponseEvent.setObserverID(sensorObservationUUID);
        dataCaptureResponseEvent.setApproach(approach);
        BearingHandler.getInstance().enqueue(bearingUUID.toString(), dataCaptureResponseEvent, EventType.CAPTURE_DATA_RESP);
    }

    @Override
    public void onSourceUnavailable(BearingConfiguration.SensorType obsSource, String message) {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Message: " + message + " Observation source: " + obsSource);
    }

    @Override
    public void onSourceAdded(BearingConfiguration.SensorType sensorType) {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Source added: " + sensorType);
    }

    /**
     * On sensor data received.
     *
     * @param siteName             the site name
     * @param noOfFloors           the no of floors
     * @param locationName         the location name
     * @param snapshotObservations the snapshot observations
     */
    public void onSensorDataReceived(String siteName, int noOfFloors, String locationName, List<SnapshotObservation> snapshotObservations) {
        if (areSensorDataItemsEmpty(sensorTypes, snapshotObservations) && !areHardwareAndPermissionEnabled()) {
            sendErrorCallback(siteName, observationDataType);
            return;
        }
        final List<SnapshotObservation> filteredObservations = rssiThresholdFilter(snapshotObservations);
        switch (observationDataType) {
            case SITE_RECORD:
                saveSiteRecordData(siteName, noOfFloors, filteredObservations, true);
                break;
            case SITE_RECORD_APPEND:
                saveSiteRecordData(siteName, noOfFloors, filteredObservations, false);
                break;
            case LOCATION_DATA_RECORD:
                saveLocationRecordData(siteName, locationName, filteredObservations);
                break;
            case SITE_DETECTION:
                final SiteDetectorUtil siteDetectorUtil = new SiteDetectorUtil();
                siteDetectorUtil.setTransitionID(bearingUUID);
                siteDetectorUtil.setCallback(BearingResponseAggregator.getInstance());
                siteDetectorUtil.matchWithSnapshot(filteredObservations);
                break;
            case LOCATION_DETECTION:
                final LocationDetectorUtil locationDetectorUtil = new LocationDetectorUtil();
                locationDetectorUtil.setCurrentSite(siteName);
                locationDetectorUtil.setTransactionID(bearingUUID);
                locationDetectorUtil.setLocationListenerCallback(BearingResponseAggregator.getInstance());
                locationDetectorUtil.detectLocation(filteredObservations, sensorTypes, approach);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onSensorDataReceived: Unsupported Type");
                break;
        }
    }

    private boolean areSensorDataItemsEmpty(List<BearingConfiguration.SensorType> sensorTypes, List<SnapshotObservation> snapshotObservations) {
        if (sensorTypes.contains(BearingConfiguration.SensorType.ST_WIFI)) {
            for (SnapshotObservation s : snapshotObservations) {
                if (BearingConfiguration.SensorType.ST_WIFI == s.getSensorType())
                    return s.getSnapShotItemList().isEmpty();
            }
        } else if (sensorTypes.contains(BearingConfiguration.SensorType.ST_BLE)) {
            for (SnapshotObservation s : snapshotObservations) {
                if (BearingConfiguration.SensorType.ST_BLE == s.getSensorType())
                    return s.getSnapShotItemList().isEmpty();
            }
        }
        return false;
    }

    private void sendErrorCallback(String siteName, RequestDataHolder.ObservationDataType observationDataType) {
        switch (observationDataType) {
            case SITE_RECORD:
            case SITE_RECORD_APPEND:
                BearingResponseAggregator.getInstance().onTrainError(bearingUUID, approach, siteName,
                        Constants.SENSOR_NOT_ENABLED + "/" + Constants.PERMISSION_NOT_ENABLED);
                break;
            case LOCATION_DATA_RECORD:
                BearingResponseAggregator.getInstance().onLocationTrainError(bearingUUID, approach,
                        Constants.SENSOR_NOT_ENABLED + "/" + Constants.PERMISSION_NOT_ENABLED);
                break;
            case SITE_DETECTION:
                BearingResponseAggregator.getInstance().onSiteDetectionStop(bearingUUID,
                        Constants.SENSOR_NOT_ENABLED + "/" + Constants.PERMISSION_NOT_ENABLED);
                break;
            case LOCATION_DETECTION:
                BearingResponseAggregator.getInstance().onErrorDetectingLocation(bearingUUID,
                        Constants.SENSOR_NOT_ENABLED + "/" + Constants.PERMISSION_NOT_ENABLED);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onSensorDataReceived: Unsupported Type");
                break;
        }
    }

    private boolean areHardwareAndPermissionEnabled() {
        final boolean areEnabled = checkAreSensorsEnabled(sensorTypes);
        final boolean arePermissionGranted = checkLocationPermission(sensorObservation.getContext());
        return areEnabled && arePermissionGranted;
    }

    private synchronized boolean checkAreSensorsEnabled(List<BearingConfiguration.SensorType> observationSourceList) {
        if (observationSourceList.contains(BearingConfiguration.SensorType.ST_WIFI)) {
            return SensorUtil.checkAreWIFISensorsEnabled(sensorObservation.getContext());
        } else if (observationSourceList.contains(BearingConfiguration.SensorType.ST_BLE)) {
            return SensorUtil.checkAreBLESenorsEnabled(sensorObservation.getContext());
        }
        return true;
    }

    private boolean checkLocationPermission(Context context) {
        final int accessFineLocation = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION);
        final int accessCoarseLocation = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION);
        return accessFineLocation == PackageManager.PERMISSION_GRANTED &&
                accessCoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    private List<SnapshotObservation> rssiThresholdFilter(List<SnapshotObservation> snapshotObservations) {
        final List<SnapshotObservation> observations = new ArrayList<>();
        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            final List<SnapshotItem> tempList = new ArrayList<>();
            final BearingConfiguration.DetectionLevel detectionLevel = snapshotObservation.getDetectionLevel();
            final BearingConfiguration.SensorType type = snapshotObservation.getSensorType();
            final List<SnapshotItem> filteredSnapshotItems = snapshotObservation.getSnapShotItemList();
            int filteredSnapshotItemSize = filteredSnapshotItems.size();
            for (int i = 0; i < filteredSnapshotItemSize; i++) {
                double[] measuredValues = filteredSnapshotItems.get(i).getMeasuredValues();
                double measuredRSSIvalue = measuredValues[0];
                final double rssiThreshold = Double.parseDouble(ConfigurationSettings.getConfiguration().getAlgorithmConfigurationPreferences()
                        .getProperty(Property.Algorithm.SNAPSHOT_THRESHOLD).toString());
                if (measuredRSSIvalue >= rssiThreshold) {
                    tempList.add(filteredSnapshotItems.get(i));
                }
            }
            final SnapshotObservation filteredSnapshotObservation = new SnapshotObservation();
            filteredSnapshotObservation.setDetectionLevel(detectionLevel);
            filteredSnapshotObservation.setSensorType(type);
            filteredSnapshotObservation.setSnapShotItemList(tempList);
            observations.add(filteredSnapshotObservation);
        }
        return observations;
    }

    /**
     * Sets approach.
     *
     * @param approach the approach
     */
    public void setApproach(BearingConfiguration.Approach approach) {
        this.approach = approach;
    }


    private void saveSiteRecordData(String siteName, int noOfFloors, List<SnapshotObservation> filteredObservations, boolean isCreate) {
        SiteTrainUtil siteTrainUtil = new SiteTrainUtil();
        siteTrainUtil.setSiteName(siteName);
        siteTrainUtil.setTransactionId(bearingUUID);
        siteTrainUtil.setApproach(approach);
        siteTrainUtil.setSiteTrainCallback(BearingResponseAggregator.getInstance());

        if (isCreate) {
            final Snapshot filteredSnapshot = SnapshotUtil.createSnapshot(filteredObservations, noOfFloors);
            siteTrainUtil.writeDataToPersistence(filteredSnapshot);
        } else {
            siteTrainUtil.sendMergeSignals(siteName, filteredObservations);
        }

        if (sensorObservationUUID != null) {
            sensorObservation.removeSource(sensorObservationUUID, sensorTypes);
            sensorObservation.unregisterListener(sensorObservationUUID);
            BearingHandler.removeRequestFromRequestDataHolderMap(bearingUUID, approach);
        }

    }


    private void saveLocationRecordData(String siteName, String locationName, List<SnapshotObservation> snapshotObservations) {
        final ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration();
        final LocationTrainerUtil locationTrainerUtil = new LocationTrainerUtil();
        locationTrainerUtil.setSiteName(siteName);
        locationTrainerUtil.setLocationName(locationName);
        locationTrainerUtil.setTransactionId(bearingUUID);
        locationTrainerUtil.setApproach(approach);
        locationTrainerUtil.setLocationTrainListener(BearingResponseAggregator.getInstance());
        final int sampleCount = Double.valueOf(configurationSettings.getAlgorithmConfigurationPreferences().getProperty(Property.Algorithm.FINGERPRINT_SAMPLE_COUNT).toString()).intValue();
        final Helper helper = Helper.getInstance();
        if (helper.getRecordCount() >= sampleCount) {
            if (sensorObservationUUID == null || sensorTypes == null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "terminateSensorObservations: Null UUID or sensortype in unregisteration");
                return;
            }
            sensorObservation.disableActiveMode(sensorObservationUUID, sensorTypes);
            sensorObservation.removeSource(sensorObservationUUID, sensorTypes);
            sensorObservation.unregisterListener(sensorObservationUUID);
            BearingHandler.removeRequestFromRequestDataHolderMap(bearingUUID, approach);
            return;
        }
        locationTrainerUtil.prepareBufferAndSave(snapshotObservations, sensorTypes);

    }
}
