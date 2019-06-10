package com.bosch.pai.bearing.sensordatastore.sensorobservation.api;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;

import java.util.List;

/**
 * Listener class to receive Sensor Observation events
 */
public interface SensorObservationListener {

    /**
     * On observation received.
     *
     * @param snapshotObservations snapshot observations received
     */
    void onObservationReceived(List<SnapshotObservation> snapshotObservations);

    /**
     * Callback method to notify unavailability of a sensor type
     *
     * @param obsSource Type of sensor that is unavailable
     * @param message   the message
     */
    void onSourceUnavailable(BearingConfiguration.SensorType obsSource, String message);

    /**
     * Callback to notify on sensor fetch source being added
     *
     * @param sensorType {@link BearingConfiguration.SensorType}
     */
    void onSourceAdded(BearingConfiguration.SensorType sensorType);
}
