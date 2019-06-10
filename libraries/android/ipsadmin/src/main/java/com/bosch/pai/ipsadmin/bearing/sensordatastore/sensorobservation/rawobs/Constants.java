package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs;

/**
 * The type Constants.
 */
public class Constants {
    /**
     * The constant GEOFENCE_RADIUS_IN_METERS.
     */
    public static final int GEOFENCE_RADIUS_IN_METERS = 1500; // 1.5 kms

    /**
     * The constant GEOFENCE_EXPIRATION_IN_HOURS.
     */
// Used to set an expiration time for a geofence. After this amount of time Location Services
    // stops tracking the geofence.
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * The constant GEOFENCE_EXPIRATION_IN_MILLISECONDS.
     */
// For this sample, geofences expire after twelve hours.
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    /**
     * The constant GEO_FENCE_LOITERING_DELAY.
     */
    public static final int GEO_FENCE_LOITERING_DELAY = 30000; // 0.5 minute

    /**
     * The constant START_SCAN.
     */
    public static final int START_SCAN = 6;
    /**
     * The constant RESPONSE_RECEIVED.
     */
    public static final int RESPONSE_RECEIVED = 7;

    /**
     * The constant SCAN_INTERVAL_WIFI.
     */
    public static final long SCAN_INTERVAL_WIFI = 2000;
    /**
     * The constant SCAN_INTERVAL_BLE.
     */
    public static final long SCAN_INTERVAL_BLE = 3000;
    /**
     * The constant SCAN_INTERVAL_GPS.
     */
    public static final long SCAN_INTERVAL_GPS = 10;


}
