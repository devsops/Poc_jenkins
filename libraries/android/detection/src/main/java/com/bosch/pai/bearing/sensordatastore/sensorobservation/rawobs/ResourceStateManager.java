package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs;

import android.os.Message;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorInfo;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorState;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Resource state Manager class should be able to handle the following scan scenario states:
 * 1. Case 1: One Non active mode scan in progress, Another non active mode scan requested
 * 2. Case 2: One Non Active mode scan in progress ,Another Active mode scan is requested
 * 3. Case 3: Active mode scan in progress ,Another non active scan requested
 * 4. Case 4: Active mode scan in progress, Another active scan requested
 * In all the above scenarios,there should not be rescanning if  scan is already in progress for one sensor type.
 */
/*
* The Resource state manager will cater to two scan methods:
* 1. Repeated scan every fixed interval
* 2.Scan after response for another scan is received.
* */

public final class ResourceStateManager {
    private final static String TAG = ResourceStateManager.class.getSimpleName();
    /**
     * The Sensor to state map.
     */
    public final Map<BearingConfiguration.SensorType, SensorState> sensorToStateMap = new HashMap<>();
    private ResourceDataManager resourceDataManager = null;
    /**
     * The Available sensors list.
     */
    private List<SensorInfo> availableSensorsList;
    private SensorObservationHandler responseAndScanHandler;

    /**
     * Instantiates a new Resource state manager.
     *
     * @param responseAndScanHandler the response and scan handler
     */
    public ResourceStateManager(SensorObservationHandler responseAndScanHandler) {
        this.responseAndScanHandler = responseAndScanHandler;
    }

    /**
     * Register listener.
     *
     * @param resourceDataManager  the resource data manager
     * @param availableSensorsList the available sensors list
     */
    public void registerListener(ResourceDataManager resourceDataManager, List<SensorInfo> availableSensorsList) {
        this.resourceDataManager = resourceDataManager;
        this.availableSensorsList = new ArrayList<>(availableSensorsList);
    }


    /**
     * Gets available sensors list.
     *
     * @return the available sensors list
     */
    public List<SensorInfo> getAvailableSensorsList() {
        return new ArrayList<>(availableSensorsList);
    }

    /**
     * Create sensor state boolean.
     *
     * @param sensorType the sensor type
     * @return the boolean
     */
/*Create the sensorState as a Sensor is added default active Mode count 0,and pending request false and an empty Arraylist to get get activeModeCount
    * Active mode flag is introduced to prevent another active scan once a scan is already happening*/
    public boolean createSensorState(BearingConfiguration.SensorType sensorType) {
        if (sensorType == null)
            return false;

        if (!sensorToStateMap.containsKey(sensorType)) {
           /*If this is the first entry then add the sensor and start*/
            final SensorState sensorStateBuilder = new SensorState();
            sensorStateBuilder.setActiveModeScan(false);
            sensorStateBuilder.setActiveModeRequestCount(0);
            sensorStateBuilder.setPendingScan(false);
            sensorStateBuilder.setActiveModeList(new ArrayList<String>());
            sensorToStateMap.put(sensorType, sensorStateBuilder);
        }
          /* Check if the sensor already has an active scan in progress, then dnt start any new scan*/
        if (!sensorToStateMap.get(sensorType).isActiveModeScan()) {
            scanSensorWithHandler(sensorType, true);
        }
        return true;
    }


    /**
     * Update sensor state data on start scan.
     *
     * @param sensorType the sensor type
     */
/*Update the sensorState object with request pending scan state, as a request is made*/
    public void updateSensorStateDataOnStartScan(BearingConfiguration.SensorType sensorType) {
        final SensorState sensorStateData = sensorToStateMap.get(sensorType);
        if(SensorUtil.isShutDown()) {
            sensorStateData.setPendingScan(false);
        } else {
            sensorStateData.setPendingScan(true);
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSensorStateDataOnStartScan: " + getCurrentTimeStamp());
        }
    }

    /**
     * Notify sensor state on stop scan.
     *
     * @param sensorType the sensor type
     */
/*Update the sensor state by passing a message to the requestAndScanHandler to set the flag*/
    public void notifySensorStateOnStopScan(BearingConfiguration.SensorType sensorType) {
        Message message = responseAndScanHandler.obtainMessage(Constants.RESPONSE_RECEIVED, sensorType);
        responseAndScanHandler.sendMessage(message);

        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "notifySensorStateOnStopScan: " + getCurrentTimeStamp());

    }


    /**
     * Update sensor state on active mode enable boolean.
     *
     * @param uuid       the uuid
     * @param sensorType the sensor type
     * @return the boolean
     */
/*Get a list with UUID and sensor type as entry to identify the active mode sensor for each application
    **Check for two scenarios:
    * 1. Check if an active mode scan is already in progress
    * 2. Check if a normal scan has already been requested and is in process
    **/
    public boolean updateSensorStateOnActiveModeEnable(UUID uuid, BearingConfiguration.SensorType sensorType) {

        /*This check is implemented to prevent the user from checking active mode Multiple times*/
        final SensorState sensorState = sensorToStateMap.get(sensorType);
        if (sensorState == null) {
            return false;
        }

        final List<String> activeModeCounterList = new ArrayList<>(sensorState.getActiveModeList());
        if (activeModeCounterList.contains(uuid.toString() + sensorType.toString())) {
            return false;
        }

        activeModeCounterList.add(uuid.toString() + sensorType.toString());
        sensorState.setActiveModeRequestCount(activeModeCounterList.size());
        sensorState.setActiveModeList((ArrayList<String>) activeModeCounterList);


        if (!sensorToStateMap.get(sensorType).isActiveModeScan()) {
            scanSensorWithHandler(sensorType, true);
            sensorToStateMap.get(sensorType).setActiveModeScan(true);
        }

        return true;
    }

    /**
     * Update sensor state on active mode disable boolean.
     *
     * @param uuid       the uuid
     * @param sensorType the sensor type
     * @return the boolean
     */
/*Remove the sensor for application from the list and decrement the Active mode count */
    public boolean updateSensorStateOnActiveModeDisable(UUID uuid, BearingConfiguration.SensorType sensorType) {

        final SensorState sensorState = sensorToStateMap.get(sensorType);
        if (sensorState == null) {
            return true;
        }
        final List<String> activeModeCounter = new ArrayList<>(sensorState.getActiveModeList());

        int position = -1;
        for (int i = 0; i < activeModeCounter.size(); i++) {
            String identifier = activeModeCounter.get(i);
            if ((uuid.toString() + sensorType.toString()).equals(identifier)) {
                position = i;
            }
        }
        if (position == -1) {
            return true;
        } else {
            activeModeCounter.remove(position);
        }

        sensorState.setActiveModeRequestCount(activeModeCounter.size());
        sensorState.setActiveModeList(activeModeCounter);

        /*Check if all the active mode counts are decremented to zero to stop scan*/

        if (sensorState.getActiveModeRequestCount() == 0) {
            scanSensorWithHandler(sensorType, false);
            sensorToStateMap.get(sensorType).setActiveModeScan(false);
            return false;
        }

        return true;

    }

    /**
     * Destroy state boolean.
     *
     * @param sensorType the sensor type
     * @return the boolean
     */
/*Method to destroy the sensor state.Before we destroy the state, Check if any active mode scan is running else remove sensor type */
    public boolean destroyState(BearingConfiguration.SensorType sensorType) {

        if (sensorToStateMap.get(sensorType).getActiveModeRequestCount() == 0) {
            resourceDataManager.terminateSensor(sensorType);
            sensorToStateMap.remove(sensorType);
            return false;
        }
        return true;

    }


    private void scanSensorWithHandler(BearingConfiguration.SensorType sensorType, boolean start) {

        if (start) {
            if (!sensorToStateMap.get(sensorType).getPendingScan()) {
                updateSensorStateDataOnStartScan(sensorType);
                Message message = responseAndScanHandler.obtainMessage(Constants.START_SCAN, sensorType);
                responseAndScanHandler.sendMessage(message);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "ScanSensorWithHandler: One scan is already in progress , Scan not started ");
            }
        } else {
            for (SensorInfo sensorInfo : availableSensorsList) {
                if (sensorInfo.getSensorType() == sensorType)
                    if (sensorInfo.isResponseBased()) {
                        responseAndScanHandler.removeMessages(Constants.RESPONSE_RECEIVED, sensorType);
                    } else {
                        responseAndScanHandler.removeMessages(Constants.START_SCAN, sensorType);
                    }
                sensorToStateMap.get(sensorType).setPendingScan(false);
            }


        }
    }


    private Long getCurrentTimeStamp() {
        return new Date().getTime();
    }

    /**
     * Gets resource state map.
     *
     * @return the resource state map
     */
    public Map<BearingConfiguration.SensorType, SensorState> getResourceStateMap() {
        return sensorToStateMap;
    }


}
