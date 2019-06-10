package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.gps_geofence;

/**
 * The interface Sensor set up callback.
 */
public interface SensorSetUpCallback {
    /**
     * On sensor init.
     *
     * @param statusCode the status code
     */
    public void onSensorInit(int statusCode);

}
