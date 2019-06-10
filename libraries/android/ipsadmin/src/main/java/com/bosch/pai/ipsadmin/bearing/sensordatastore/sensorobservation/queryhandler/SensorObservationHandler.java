package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.Constants;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceStateManager;

/**
 * The type Sensor observation handler.
 */
public class SensorObservationHandler extends Handler {
    private ResourceDataManager resourceDataManager;
    private ResourceStateManager resourceStateManager;


    /**
     * Instantiates a new Sensor observation handler.
     *
     * @param looper the looper
     */
    public SensorObservationHandler(Looper looper) {
        super(looper);
    }

    /**
     * Register resource managers.
     *
     * @param resourceDataManager  the resource data manager
     * @param resourceStateManager the resource state manager
     */
    public void registerResourceManagers(ResourceDataManager resourceDataManager, ResourceStateManager resourceStateManager) {
        this.resourceDataManager = resourceDataManager;
        this.resourceStateManager = resourceStateManager;
    }

    /*    Handler will have to main functionality
    * 1. Send message to  start the scan
    * 2. Notify once the response is received
    *
    *
     * The  START SCAN handler will manage two scans:
     * 1. Active Mode scan
     * 2. Non active mode scan
     * ----------------------------------------------------------------
     * 1. Active mode scan
     * To start active mode scan:
     * a) Check if active mode count is above 0.
     * If no active mode scan was running, then set the active mode flag as true and start the first active mode scan .
     * There can be only one active mode scan for one sensor at a time.
     * -----------------------------------------------------------------
     * 2. Non Active scan:
     * This scan is a one time scan and will be triggered dependent on  on active scan count
     * ------------------------------------------------------------------
     * ResponseAndScanHandler manage 2 secenarios:
     * 1. The START SCAN explained above
     * 2.Recurssive scan which will only trigger a scan once a response is got for the scan
     **/


    @Override
    public void handleMessage(Message msg) {
        final int message = msg.what;
        final BearingConfiguration.SensorType sensorWithResponse = (BearingConfiguration.SensorType) msg.obj;

        for (SensorInfo sensorInfo : resourceStateManager.getAvailableSensorsList()) {
            if (sensorInfo.getSensorType() == sensorWithResponse) {
                final BearingConfiguration.SensorType sensorType = sensorInfo.getSensorType();
                final boolean isResponseBased = sensorInfo.isResponseBased();

                if (message == Constants.START_SCAN) {
                    if (resourceStateManager.sensorToStateMap.get(sensorType) == null) {
                        break;
                    }
                    if (resourceStateManager.sensorToStateMap.get(sensorType).getActiveModeRequestCount() > 0 && !isResponseBased) {// if active mode count above zero start repeated scan
                        final Message rescanMessage = this.obtainMessage(Constants.START_SCAN, sensorType);
                        this.sendMessageDelayed(rescanMessage, sensorInfo.getScanInterval());
                    }
                    resourceStateManager.updateSensorStateDataOnStartScan(sensorType);
                    resourceDataManager.scanSensor(sensorType);

                } else if (message == Constants.RESPONSE_RECEIVED) {
                    final SensorState sensorStateData = resourceStateManager.sensorToStateMap.get(sensorType);
                    if (sensorStateData != null) {
                        sensorStateData.setPendingScan(false);
                        if (resourceStateManager.sensorToStateMap.get(sensorType) != null && resourceStateManager.sensorToStateMap.get(sensorType).getActiveModeRequestCount() > 0 && isResponseBased) {
                            final Message responsebasedMessage = this.obtainMessage(Constants.START_SCAN, sensorType);
                            this.sendMessageDelayed(responsebasedMessage, sensorInfo.getScanInterval());
                        }
                    }

                }
            }
        }
    }


}
