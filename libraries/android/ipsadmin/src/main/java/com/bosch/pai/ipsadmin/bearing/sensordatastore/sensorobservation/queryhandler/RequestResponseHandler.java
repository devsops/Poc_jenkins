package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;

import android.support.annotation.NonNull;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.ipsadmin.bearing.benchmark.bearinglogger.profiling.ResourceProfiler;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.bearing.util.SnapshotItemManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The type Request response handler.
 */
public class RequestResponseHandler {
    private static final String TAG = RequestResponseHandler.class.getSimpleName();
    private ResourceDataManager resourceDataManager;
    /**
     * The App id to sensor callback map.
     */
    protected final Map<UUID, ListenerActiveSensorMap> appIdToSensorCallbackMap = new HashMap<>();
    private List<SnapshotObservation> existingSnapshotObservations = new ArrayList<>();

    /**
     * Instantiates a new Request response handler.
     */
    public RequestResponseHandler() {

    }

    /**
     * Register listener.
     *
     * @param resourceDataManager the resource data manager
     */
    public void registerListener(ResourceDataManager resourceDataManager) {
        this.resourceDataManager = resourceDataManager;
    }

    /**
     * Method to add sensor to List<ISensorObsListener> and to identify each combination at application level with UUID and first Observation Flag as true
     * The first Observation Flag is Used to send out the first Observation Regardless if it is in Active Mode or not
     *
     * @param uuid                      the uuid
     * @param sensorObservationListener the sensor observation listener
     * @return the boolean
     */
    public boolean createSensorRequestToListenerMap(UUID uuid, SensorObservationListener sensorObservationListener) {
        if (uuid == null || sensorObservationListener == null) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "createSensorRequestToListenerMap: UUID or Listener is Null, Hence UUID to Listener map not created");
            return false;
        }
        if (appIdToSensorCallbackMap.get(uuid) == null) {
            final ListenerActiveSensorMap listenerActiveSensorMap = new ListenerActiveSensorMap();
            listenerActiveSensorMap.setObservationListener(sensorObservationListener);
            listenerActiveSensorMap.setFirstObservation(true);
            appIdToSensorCallbackMap.put(uuid, listenerActiveSensorMap);
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adding a new entry to the map with UUID for App Senor Mapping");
            return true;
        }
        return false;
    }

    /**
     * Method to update sensor to ApplicationMap with  Sensor type and flags (false by default)
     * whenever a senor is added, see that the first observation flag is set to true(ie send out the first observation if in active Mode or not )
     *
     * @param uuid       the uuid
     * @param sensorType the sensor type
     * @return the boolean
     */
    public boolean updateSensorRequestToListenerMap(UUID uuid, BearingConfiguration.SensorType sensorType) {
        if (appIdToSensorCallbackMap.isEmpty() || uuid == null || sensorType == null) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSensorRequestToListenerMap: Map empty or the function parameters null");
            return false;
        }
        final List<SensorSelection> sensorSelections;
        final ListenerActiveSensorMap listenerActiveSensorMap = appIdToSensorCallbackMap.get(uuid);

        if (listenerActiveSensorMap.getSensorSelections() == null) {
            sensorSelections = new ArrayList<>();
            sensorSelections.add(createSensorCombMap(sensorType, false));
        } else {
            sensorSelections = new ArrayList<>(listenerActiveSensorMap.getSensorSelections());
            if (!checkIfSensorPresent(sensorSelections, sensorType)) {
                sensorSelections.add(createSensorCombMap(sensorType, false));
            }
        }
        listenerActiveSensorMap.setFirstObservation(true);
        listenerActiveSensorMap.setSensorSelections(sensorSelections);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateSensorRequestToListenerMap: Appending sensortype to map with UUID present");
        return true;


    }

    /**
     * Update the Active Mode Status in Application Sensor Listener Map
     *
     * @param uuid         : specific to to each application
     * @param sensorType   the sensor type
     * @param isActiveMode the is active mode
     * @return the boolean
     */
    public boolean updateActiveModeforSensor(UUID uuid, BearingConfiguration.SensorType sensorType, boolean isActiveMode) {
        if (uuid == null || sensorType == null || appIdToSensorCallbackMap.get(uuid) == null) {
            return false;
        }
        final List<SensorSelection> sensorSelections = appIdToSensorCallbackMap.get(uuid).getSensorSelections();
        if (sensorSelections == null || sensorSelections.isEmpty()) {
            return false;
        }
        for (SensorSelection sensorSelection : sensorSelections) {
            if (sensorSelection.getSensorType() == sensorType) {
                sensorSelection.setActiveMode(isActiveMode);
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "updateActiveModeforSensor: Active mode updated for corresponding sensor");
                return true;
            }
        }
        return false;

    }

    /**
     * Remove sensor from request to listener map boolean.
     *
     * @param uuid       the uuid
     * @param sensorType the sensor type
     * @return the boolean
     */
    public synchronized boolean removeSensorFromRequestToListenerMap(UUID uuid, BearingConfiguration.SensorType sensorType) {
        if (uuid == null || sensorType == null) {
            return false;
        }
        if (appIdToSensorCallbackMap.get(uuid) == null)
            return false;
        final List<SensorSelection> sensorSelections = appIdToSensorCallbackMap.get(uuid).getSensorSelections();
        if (sensorSelections == null || sensorSelections.isEmpty()) {
            return false;
        }
        final List<SensorSelection> temp = new ArrayList<>(sensorSelections);
        boolean status = false;
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).getSensorType() == sensorType) {
                status = true;
                temp.remove(i);
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "removeSensorFromRequestToListenerMap: " +
                        sensorType + " sensor removed from  sensorMap");
            }
        }
        appIdToSensorCallbackMap.get(uuid).setSensorSelections(temp);
        return status;
    }

    /**
     * Delete sensor request to listener map boolean.
     *
     * @param uuid the uuid
     * @return the boolean
     */
    /*Remove the UUID and corresponding entry for listener Object*/
    public boolean deleteSensorRequestToListenerMap(UUID uuid) {
        if (uuid == null || appIdToSensorCallbackMap.get(uuid) == null) {
            return false;
        }
        appIdToSensorCallbackMap.remove(uuid);
        return true;
    }

    /*Check if the sensor is already present in the map for the uuid,if present don't add the new sensor*/
    private boolean checkIfSensorPresent(List<SensorSelection> sensorSelections, BearingConfiguration.SensorType sensorType) {

        for (SensorSelection sensorSelection : sensorSelections) {
            if (sensorSelection.getSensorType() == sensorType) {
                return true;
            }
        }
        return false;
    }


    /*Create each sensor with default values active mode disable and Observation received false*/
    private SensorSelection createSensorCombMap(BearingConfiguration.SensorType sensorType, boolean isActiveMode) {
        final SensorSelection sensorSelection = new SensorSelection();
        sensorSelection.setSensorType(sensorType);
        sensorSelection.setActiveMode(isActiveMode);
        sensorSelection.setObservationReceived(false);
        return sensorSelection;
    }


    /**
     * Notify observation and update.
     *
     * @param sensorType the sensor type
     */
    /*Update the queue on the status once a callback has come* for the specific callback and check if its time to send the response */
    public void notifyObservationAndUpdate(BearingConfiguration.SensorType sensorType) {
        if (sensorType == null)
            return;
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "notifyObservationAndUpdate: Notification on CallbackUpdate");
        // setObservationFlag(sensorType);
        switch (sensorType) {
            case ST_WIFI:
                setObservationFlag(BearingConfiguration.SensorType.ST_WIFI);
                break;
            case ST_BLE:
                setObservationFlag(BearingConfiguration.SensorType.ST_BLE);
                break;
            case ST_GPS:
                setObservationFlag(BearingConfiguration.SensorType.ST_GPS);
                break;
            case ST_MAGNETO:
                setObservationFlag(BearingConfiguration.SensorType.ST_MAGNETO);
                break;
            case ST_IMU:
                setObservationFlag(BearingConfiguration.SensorType.ST_IMU);
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "notifyObservationAndUpdate: defaults");
                break;
        }
        checkObservationReceivedFlagToMerge();
    }


    /*Set the flag for the sensor which has received data*/
    private boolean setObservationFlag(BearingConfiguration.SensorType sensorType) {
        if (appIdToSensorCallbackMap.isEmpty()) {
            return false;
        }
        final Set<UUID> uuids = appIdToSensorCallbackMap.keySet();
        for (UUID uuid : uuids) {
            ListenerActiveSensorMap sensorMap = appIdToSensorCallbackMap.get(uuid);
            List<SensorSelection> selections = sensorMap.getSensorSelections();
            if (selections != null) {
                for (SensorSelection sensorSelection : selections) {
                    if (sensorSelection.getSensorType() == sensorType) {
                        sensorSelection.setObservationReceived(true);
                    }
                }
            }
        }
        return true;
    }

    /*Checks if data is available for combinations selected and sends out response if matches*/
    private void checkObservationReceivedFlagToMerge() {
        if (appIdToSensorCallbackMap.isEmpty()) {
            return;
        }
        final Set<UUID> uuids = new HashSet<>(appIdToSensorCallbackMap.keySet());
        for (UUID uuid : uuids) {
            int observationReceivedCount = 0;
            ListenerActiveSensorMap obj = appIdToSensorCallbackMap.get(uuid);
            List<SensorSelection> selections = obj.getSensorSelections();
            if (selections != null) {
                for (SensorSelection sensorSelection : selections) {
                    if (sensorSelection.getObservationReceived()) {
                        observationReceivedCount++;
                    }
                }
                if ((observationReceivedCount == selections.size()) && !selections.isEmpty()) {
                    sendOutObservationsAndClearObservationFlag(uuid);
                }
            }
        }
    }

    /*This method will sendOut the observation Based on IsFirst or active mode Flag*/
    private void sendOutObservationsAndClearObservationFlag(UUID uuid) {

        if (appIdToSensorCallbackMap.get(uuid).getFirstObservation()) {
            /*If the first observation,then respond with the scan result without any checks*/
            sendSnapshotforMatch(uuid);
            appIdToSensorCallbackMap.get(uuid).setFirstObservation(false);
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "checkSensorCombinationsToMerge: Clearing the First Observation flag after serving first Observation");
        } else {
            /*If the first request is already served,then only transmit if the sensor is in active mode*/
            checkIfInActiveModeToSendOutObservation(uuid);
        }
        clearObservationFlag(uuid);

        new ResourceProfiler().writeDeviceInfo(null);
    }

    /*Whenever an observation is received, then check if active mode flag is enabled, If so send out the first observation and reset the Active mode flag*/
    private void checkIfInActiveModeToSendOutObservation(@NonNull UUID uuid) {
        if (appIdToSensorCallbackMap.get(uuid).getSensorSelections() == null) {
            return;
        }
        final List<SensorSelection> sensorSelection = appIdToSensorCallbackMap.get(uuid).getSensorSelections();
        int sensorInActiveModeCount = 0;
        if (sensorSelection == null) {
            return;
        }

        for (SensorSelection sensors : sensorSelection) {
            if (sensors.getActiveMode())
                sensorInActiveModeCount++;
        }


        if (sensorInActiveModeCount == sensorSelection.size()) {
            sendSnapshotforMatch(uuid);
        }
    }

    /*Sends out SnapshotObservation For the sensor combination and reset for new reception*/
    private void sendSnapshotforMatch(UUID uuid) {

        final ListenerActiveSensorMap listenerActiveSensorMap = appIdToSensorCallbackMap.get(uuid);
        final SensorObservationListener sensorObservationListener = listenerActiveSensorMap.getObservationListener();
        final List<SnapshotObservation> mergedSnapshotToRoute;
        final List<SensorSelection> selectedSensor = listenerActiveSensorMap.getSensorSelections();
        int count = selectedSensor.size();
        if (count == 1) {
            mergedSnapshotToRoute = resourceDataManager.getUpdatedDataValues(selectedSensor.get(0).getSensorType());
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "sendSnapshotforMatch: Observation merged and send out for single sensor");
        } else {
            mergedSnapshotToRoute = mergeDataforSensorCombinatn(selectedSensor);
            setExistingSnapshotObservations(new ArrayList<SnapshotObservation>());/*Clearing the previous Observations*/
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "sendSnapshotforMatch: Observation merged and send out for multiple sensor");
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "sendSnapshotforMatch: " + new SnapshotItemManager().snapshotObservationListToJSONString(mergedSnapshotToRoute));
        }
        sensorObservationListener.onObservationReceived(mergedSnapshotToRoute);
    }

    /*Merges the data for all sensor combinations in the SensorSelectionMapping after getting updated values from the Resource data manger*/
    private List<SnapshotObservation> mergeDataforSensorCombinatn(@NonNull List<SensorSelection> selectedSensor) {
        final SnapshotItemManager snapshotItemManager = new SnapshotItemManager();
        for (SensorSelection sensorSelection : selectedSensor) {
            final List<SnapshotObservation> mergedsnapshotObservations = snapshotItemManager.combineExistingObservations(getExistingSnapshotObservations(), resourceDataManager.getUpdatedDataValues(sensorSelection.getSensorType()));
            setExistingSnapshotObservations(mergedsnapshotObservations);
        }
        return getExistingSnapshotObservations();
    }

    /*Clears the sensors Selection Observation Received Flag after sending out observation*/
    private void clearObservationFlag(UUID uuid) {
        final ListenerActiveSensorMap map = appIdToSensorCallbackMap.get(uuid);
        final List<SensorSelection> sensorSelections = map.getSensorSelections();
        for (SensorSelection sensorSelection : sensorSelections) {
            sensorSelection.setObservationReceived(false);
        }
        map.setSensorSelections(sensorSelections);
        appIdToSensorCallbackMap.put(uuid, map);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "clearObservationFlag: checking onObservation flag");
    }


    /**
     * Gets existing snapshot observations.
     *
     * @return the existing snapshot observations
     */
    private List<SnapshotObservation> getExistingSnapshotObservations() {
        return Collections.unmodifiableList(existingSnapshotObservations);
    }

    /**
     * Sets existing snapshot observations.
     *
     * @param existingSnapshotObservations the existing snapshot observations
     */
    private void setExistingSnapshotObservations(List<SnapshotObservation> existingSnapshotObservations) {
        this.existingSnapshotObservations = existingSnapshotObservations != null ?
                new ArrayList<>(existingSnapshotObservations) : new ArrayList<SnapshotObservation>();
    }


}