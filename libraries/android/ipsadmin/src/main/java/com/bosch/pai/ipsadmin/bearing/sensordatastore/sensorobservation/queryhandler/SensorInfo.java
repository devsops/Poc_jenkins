package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler;


import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

/**
 * The type Sensor info.
 */
public class SensorInfo {

    private BearingConfiguration.SensorType sensorType;
    private long scanInterval;
    private boolean isResponseBased;

    /**
     * Instantiates a new Sensor info.
     *
     * @param sensorType      the sensor type
     * @param isResponseBased the is response based
     * @param scanInterval    the scan interval
     */
    public SensorInfo(BearingConfiguration.SensorType sensorType, boolean isResponseBased, long scanInterval) {
        this.sensorType = sensorType;
        this.isResponseBased = isResponseBased;
        this.scanInterval = scanInterval;

    }

    /**
     * Is response based boolean.
     *
     * @return the boolean
     */
    public boolean isResponseBased() {
        return isResponseBased;
    }

    /**
     * Sets response based.
     *
     * @param responseBased the response based
     */
    public void setResponseBased(boolean responseBased) {
        isResponseBased = responseBased;
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
     * Sets scan interval.
     *
     * @param scanInterval the scan interval
     */
    public void setScanInterval(long scanInterval) {
        this.scanInterval = scanInterval;
    }

    /**
     * Gets scan interval.
     *
     * @return the scan interval
     */
    public long getScanInterval() {
        return scanInterval;
    }

    /**
     * Gets sensor type.
     *
     * @return the sensor type
     */
    public BearingConfiguration.SensorType getSensorType() {
        return sensorType;
    }
}
