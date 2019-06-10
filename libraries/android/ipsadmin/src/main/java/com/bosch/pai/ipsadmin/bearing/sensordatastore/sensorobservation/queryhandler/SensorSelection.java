package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;


import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

/**
 * The type Sensor selection.
 */
public class SensorSelection {
    private BearingConfiguration.SensorType sensorType;
    private Boolean isActiveMode;
    private Boolean isObservationReceived;

    /**
     * Gets active mode.
     *
     * @return the active mode
     */
    public Boolean getActiveMode() {
        return isActiveMode;
    }

    /**
     * Sets active mode.
     *
     * @param activeMode the active mode
     */
    public void setActiveMode(Boolean activeMode) {
        isActiveMode = activeMode;
    }

    /**
     * Gets sensor type.
     *
     * @return the sensor type
     */
    public BearingConfiguration.SensorType getSensorType() {
        return sensorType;
    }

    /**
     * Sets sensor type.
     *
     * @param sensorType the sensor type
     */
    public void setSensorType(BearingConfiguration.SensorType sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * Gets observation received.
     *
     * @return the observation received
     */
    public Boolean getObservationReceived() {
        return isObservationReceived;
    }

    /**
     * Sets observation received.
     *
     * @param observationReceived the observation received
     */
    public void setObservationReceived(Boolean observationReceived) {
        isObservationReceived = observationReceived;
    }


}
