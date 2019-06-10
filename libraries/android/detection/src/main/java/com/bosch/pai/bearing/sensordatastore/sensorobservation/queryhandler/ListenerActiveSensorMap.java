package com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler;

import com.bosch.pai.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Listener active sensor map.
 */
public class ListenerActiveSensorMap {

    private List<SensorSelection> sensorSelections = new ArrayList<>();
    private SensorObservationListener ObservationListener;
    private Boolean isFirstObservation;

    /**
     * Gets sensor selections.
     *
     * @return the sensor selections
     */
    public List<SensorSelection> getSensorSelections() {
        return Collections.unmodifiableList(sensorSelections);
    }

    /**
     * Sets sensor selections.
     *
     * @param sensorSelections the sensor selections
     */
    public void setSensorSelections(List<SensorSelection> sensorSelections) {
        this.sensorSelections = sensorSelections != null ? new ArrayList<>(sensorSelections) :
                new ArrayList<SensorSelection>();
    }

    /**
     * Gets first observation.
     *
     * @return the first observation
     */
    public Boolean getFirstObservation() {
        return isFirstObservation;
    }

    /**
     * Sets first observation.
     *
     * @param firstObservation the first observation
     */
    public void setFirstObservation(Boolean firstObservation) {
        isFirstObservation = firstObservation;
    }

    /**
     * Gets observation listener.
     *
     * @return the observation listener
     */
    public SensorObservationListener getObservationListener() {
        return ObservationListener;
    }

    /**
     * Sets observation listener.
     *
     * @param observationListener the observation listener
     */
    public void setObservationListener(SensorObservationListener observationListener) {
        ObservationListener = observationListener;
    }


}