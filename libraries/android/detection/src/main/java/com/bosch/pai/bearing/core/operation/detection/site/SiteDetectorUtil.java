package com.bosch.pai.bearing.core.operation.detection.site;

import android.support.annotation.NonNull;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.algorithm.event.SiteDetectionEvent;
import com.bosch.pai.bearing.algorithm.sitedetection.SiteDetection;
import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.benchmark.bearinglogger.profiling.ResourceProfiler;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.core.util.Constants;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.SiteDetectionConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.logger.Logger;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.gps_geofence.GeoFenceStatusCode;
import com.bosch.pai.bearing.util.SnapshotItemManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;


/**
 * The type Site detector util.
 */
public class SiteDetectorUtil {

    private static final String TAG = SiteDetectorUtil.class.getName();
    /**
     * The constant SITE_UNKNOWN.
     */
    public static final String SITE_UNKNOWN = Constants.SITE_UNKNOWN;
    private final ObservationHandlerAndListener observationHandlerAndListener;
    private String currentSite = Constants.SITE_UNKNOWN;
    private SiteDetectListener siteDetectListener;
    private Logger logger;
    private UUID registeredBearingUUID;


    /**
     * Instantiates a new Site detector util.
     */
    public SiteDetectorUtil() {
        observationHandlerAndListener = new ObservationHandlerAndListener();
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Initialized SiteDetectorUtil");
        logger = new Logger("SiteRankingInfo");
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
     * Register sensor observation listener.
     */
    public void registerSensorObservationListener() {
        observationHandlerAndListener.registerSensorObservationListener();
    }

    /**
     * Start site detection.
     *
     * @param uuid           the uuid
     * @param sensorTypeList the sensor type list
     */
    public void startSiteDetection(BearingConfiguration.Approach approach, UUID uuid, List<BearingConfiguration.SensorType> sensorTypeList) {
        new ResourceProfiler().writeDeviceInfo(null);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "startSiteDetection");
        this.registeredBearingUUID = uuid;
        final boolean sensorStatus = observationHandlerAndListener.addObservationSource(registeredBearingUUID, sensorTypeList, true);
        if (!sensorStatus) {
            if (siteDetectListener != null) {
                siteDetectListener.onSiteDetectionStop(registeredBearingUUID, Constants.SENSOR_NOT_ENABLED);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "startSiteDetection: ERROR register listener");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "startSiteDetection: Sensor not enabled");
            }
            return;
        }
        final RequestDataHolder requestDataHolder = new RequestDataHolder(uuid, RequestDataHolder.ObservationDataType.SITE_DETECTION,
                observationHandlerAndListener);
        requestDataHolder.setActiveModeOn(true);
        requestDataHolder.setApproach(approach);
        BearingHandler.addRequestToRequestDataHolderMap(requestDataHolder);
    }

    /**
     * Sets callback.
     *
     * @param callback the callback
     */
    public void setCallback(SiteDetectListener callback) {
        this.siteDetectListener = callback;
    }

    /**
     * Method to identify the user's current site based on the snapshotItems obtained from scan.
     *
     * @param snapshotObservations the snapshot observations
     */
    public void matchWithSnapshot(List<SnapshotObservation> snapshotObservations) {
        new ResourceProfiler().writeDeviceInfo(null);
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        if ((observationHandlerAndListener != null)) {
            if (!isGeoFenceTransition(snapshotObservations)) {
                final UUID requestUUID = UUID.randomUUID();
                final SiteDetectionEvent siteDetectionEvent = new SiteDetectionEvent(requestUUID.toString(), EventType.TRIGGER_DETECTION, new EventSender() {
                    @Override
                    public void reply(String requestID, String reply) {
                        if (requestUUID.toString().equals(requestID)) {
                            setCurrentSite(reply);
                        }
                    }
                });
                siteDetectionEvent.setObservedSnapshotObservations(snapshotObservations);
                Map<String, Set<SiteDetectionConfiguration>> map = persistenceHandler.readSourceIdMapWithConfiguration();
                if(map == null || map.isEmpty()) {
                    final SnapshotItemManager snapshotItemManager = new SnapshotItemManager();
                    map = snapshotItemManager.convertToSourceIdMapWithConfiguration(persistenceHandler.readSourceIdMap());
                }
                siteDetectionEvent.setSourceIdMap(map);
                AlgorithmLifeCycleHandler.getInstance().enqueue(requestUUID.toString(), siteDetectionEvent, EventType.TRIGGER_DETECTION, SiteDetection.class.getName());
            } else {
                final String optionalMessage = snapshotObservations.get(0).getSnapShotItemList().get(0).getCustomField()[0];
                final String entryExitStatus = getTransitionString(optionalMessage);
                final String siteName = optionalMessage.substring(0, optionalMessage.indexOf('|'));
                final boolean isDetected = entryExitStatus.equalsIgnoreCase(GeoFenceStatusCode.FENCE_ENTERED);
                if (isDetected) {
                    setCurrentSite(siteName);
                } else {
                    setCurrentSite(SiteDetectorUtil.SITE_UNKNOWN);
                }
            }
        }
    }

    private boolean isGeoFenceTransition(List<SnapshotObservation> snapshotObservations) {
        if (snapshotObservations != null && !(snapshotObservations.isEmpty())) {
            for (SnapshotObservation snapshotObservation : snapshotObservations) {
                if (BearingConfiguration.SensorType.ST_GPS.equals(snapshotObservation.getSensorType())) {
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    private String getTransitionString(String optionalMessage) {
        return optionalMessage.substring(optionalMessage.indexOf('|') + 1, optionalMessage.length());
    }

    /**
     * Gets current site.
     *
     * @return the current site
     */
    public String getCurrentSite() {
        return currentSite;
    }

    private void setCurrentSite(String siteName) {
        currentSite = siteName;
        final Calendar cal = Calendar.getInstance();
        final TimeZone tz = cal.getTimeZone();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        sdf.setTimeZone(tz);
        final String localTime = sdf.format(new Date());
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, TAG, "Time: " + localTime);

        if (siteName.equals(SiteDetectorUtil.SITE_UNKNOWN)) {
            if (siteDetectListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Site Unknown ");
                siteDetectListener.onSiteExit(registeredBearingUUID, localTime, SiteDetectorUtil.SITE_UNKNOWN);
            }
        } else {
            if (siteDetectListener != null) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Site detected: " + siteName);
                siteDetectListener.onSiteEntry(registeredBearingUUID, localTime, siteName);
            }
        }
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
     * Sets transition id.
     *
     * @param bearingUUID the bearing uuid
     */
    public void setTransitionID(UUID bearingUUID) {
        registeredBearingUUID = bearingUUID;
    }


    /**
     * Sets approach.
     *
     * @param approach the approach
     */
    public void setApproach(BearingConfiguration.Approach approach) {
        observationHandlerAndListener.setApproach(approach);
    }

}
