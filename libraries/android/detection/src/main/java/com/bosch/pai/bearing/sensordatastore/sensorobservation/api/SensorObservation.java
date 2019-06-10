package com.bosch.pai.bearing.sensordatastore.sensorobservation.api;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.RequestResponseHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorState;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceStateManager;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.wifi.WifiObserver;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Sensor observation.
 */
public class SensorObservation {
    private static final String TAG = SensorObservation.class.getName();
    private static SensorObservation sensorObservation;
    private SensorObservationListener sensorObservationCallback = null;
    // To indicate the status of the sensor like ENABLED/DISABLED
    private Map<BearingConfiguration.SensorType, Boolean> sensorStatusMap = new EnumMap<>(BearingConfiguration.SensorType.class);
    // To indicate the state of the sensor like in ACTIVE_MODE/INACTIVE_MODE
    private Map<BearingConfiguration.SensorType, Boolean> sensorStateMap = new EnumMap<>(BearingConfiguration.SensorType.class);

    private Context context;
    private final ResourceDataManager resourceDataManager;
    private final RequestResponseHandler responseHandler;
    private final ResourceStateManager resourceStateManager;

    private SensorObservation(Looper looper) {
        responseHandler = new RequestResponseHandler();
        final SensorObservationHandler sensorObservationHandler = new SensorObservationHandler(looper);
        resourceStateManager = new ResourceStateManager(sensorObservationHandler);
        resourceDataManager = new ResourceDataManager(responseHandler, resourceStateManager, sensorObservationHandler);
        sensorObservationHandler.registerResourceManagers(resourceDataManager, resourceStateManager);

        for (BearingConfiguration.SensorType sensorType : BearingConfiguration.SensorType.values()) {
            this.sensorStatusMap.put(sensorType, false);
            this.sensorStateMap.put(sensorType, false);
        }
    }

    /**
     * Init.
     *
     * @param looper the looper
     */
    public static void init(Looper looper) {
        if (sensorObservation == null)
            sensorObservation = new SensorObservation(looper);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized SensorObservation getInstance() {
        return sensorObservation;
    }


    /**
     * Sets context.
     *
     * @param context the context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public Context getContext() {
        return context;
    }


    /**
     * Register listener uuid.
     *
     * @param callback the callback
     * @return the uuid
     */
    public synchronized UUID registerListener(SensorObservationListener callback) {
        if (callback == null) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Callback Listener is Null");
            return null;
        }
        final UUID requestID = UUID.randomUUID();
        return responseHandler.createSensorRequestToListenerMap(requestID, callback) ? requestID : null;
    }


    /**
     * SensorObservation accepts a list of Sensors , Before each sensor is enabled to scan it is checked to see if it satisfies the sensor prerequisites.
     *
     * @param requestID             the request id
     * @param observationSourceList the observation source list
     * @return the boolean
     */
    public synchronized boolean addSource(@NonNull UUID requestID, @NonNull List<BearingConfiguration.SensorType> observationSourceList) {

        //Checks if prerequisites are meet.
        if (!checkSensorPrerequisites(observationSourceList))
            return false;

        //Triggers the sensor if enabled.
        for (BearingConfiguration.SensorType observationSource : observationSourceList) {
            sensorStatusMap.put(observationSource, false);
            if (resourceDataManager.setUpSensor(context, observationSource)) {
                sensorStatusMap.put(observationSource, responseHandler.updateSensorRequestToListenerMap(requestID, observationSource));
            } else {
                return false;
            }
        }
        return true;
    }

    private synchronized boolean checkSensorPrerequisites(List<BearingConfiguration.SensorType> observationSourceList) {
        if (observationSourceList.contains(BearingConfiguration.SensorType.ST_WIFI)) {
            return SensorUtil.checkAreWIFISensorsEnabled(context);
        } else if (observationSourceList.contains(BearingConfiguration.SensorType.ST_BLE)) {
            return SensorUtil.checkAreBLESenorsEnabled(context);
        }
        return true;
    }


    /**
     * Remove source.
     *
     * @param requestID             the request id
     * @param observationSourceList the observation source list
     */
    public synchronized void removeSource(@NonNull UUID requestID, @NonNull List<BearingConfiguration.SensorType> observationSourceList) {

        for (BearingConfiguration.SensorType observationSource : observationSourceList) {
            if (sensorStatusMap.get(observationSource)) {
                final boolean isSensorRemoved = responseHandler.removeSensorFromRequestToListenerMap(requestID, observationSource);
                if (isSensorRemoved) {
                    sensorStatusMap.put(observationSource, resourceStateManager.destroyState(observationSource));
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "removeSource: done");
                }
            }
        }
    }


    /**
     * Enable active mode.
     *
     * @param requestID             the request id
     * @param observationSourceList the observation source list
     */
    public synchronized void enableActiveMode(@NonNull UUID requestID, @NonNull List<BearingConfiguration.SensorType> observationSourceList) {
        for (BearingConfiguration.SensorType observationSource : observationSourceList) {
            if (sensorStatusMap.get(observationSource)) {
                sensorStateMap.put(observationSource, resourceStateManager.updateSensorStateOnActiveModeEnable(requestID, observationSource));
                responseHandler.updateActiveModeforSensor(requestID, observationSource, true);
            }
        }
    }


    /**
     * Disable active mode.
     *
     * @param requestID             the request id
     * @param observationSourceList the observation source list
     */
    public synchronized void disableActiveMode(@NonNull UUID requestID, @NonNull List<BearingConfiguration.SensorType> observationSourceList) {

        for (BearingConfiguration.SensorType observationSource : observationSourceList) {
            if (sensorStatusMap.get(observationSource) && sensorStateMap.get(observationSource)) {
                sensorStateMap.put(observationSource, resourceStateManager.updateSensorStateOnActiveModeDisable(requestID, observationSource));
                responseHandler.updateActiveModeforSensor(requestID, observationSource, false);
            }

        }
    }

    /**
     * Unregister listener.
     *
     * @param uuid the uuid
     */
    public synchronized void unregisterListener(@NonNull UUID uuid) {
        final boolean unregisterStatus = responseHandler.deleteSensorRequestToListenerMap(uuid);
        if (!unregisterStatus)
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "unregisterListener: Failed to unregister Listener");
    }

    /**
     * Register geo fence callback.
     *
     * @param sensorObservationCallback the sensor observation callback
     */
    public void registerGeoFenceCallback(@NonNull SensorObservationListener sensorObservationCallback) {
        this.sensorObservationCallback = sensorObservationCallback;
    }

    /**
     * Gets sensor observation callback.
     *
     * @return the sensor observation callback
     */
    public synchronized SensorObservationListener getSensorObservationCallback() {
        return sensorObservationCallback;
    }


    /**
     * Access sensor state to enable active mode boolean.
     *
     * @param sensorTypeList the sensor type list
     * @return the boolean
     */
    public synchronized Boolean accessSensorStateToEnableActiveMode(List<BearingConfiguration.SensorType> sensorTypeList) {
        for (BearingConfiguration.SensorType sensorType : sensorTypeList) {
            if (resourceStateManager.getResourceStateMap().get(sensorType) == null) {
                return false;
            }
            final SensorState sensorState = resourceStateManager.sensorToStateMap.get(sensorType);
            if (sensorState != null && sensorState.getPendingScan()) {
                return true;
            }

        }
        return false;
    }

}
