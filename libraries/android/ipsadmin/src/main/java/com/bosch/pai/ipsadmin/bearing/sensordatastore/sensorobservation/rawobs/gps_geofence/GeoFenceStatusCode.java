package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.gps_geofence;

/**
 * The type Geo fence status code.
 */
public class GeoFenceStatusCode {

    /**
     * The constant GEO_FENCE_ENTERED.
     */
    public static final int GEO_FENCE_ENTERED = 1;

    /**
     * The constant GEO_FENCE_EXITED.
     */
    public static final int GEO_FENCE_EXITED = 2;

    /**
     * The constant GEO_FENCE_DWELL.
     */
    public static final int GEO_FENCE_DWELL = 4;

    /**
     * The constant ERR_ADDING_GEO_FENCE.
     */
    public static final int ERR_ADDING_GEO_FENCE = 2;

    /**
     * The constant FENCE_ENTERED.
     */
    public static final String FENCE_ENTERED = "GEO_FENCE_ENTERED";

    /**
     * The constant FENCE_EXITED.
     */
    public static final String FENCE_EXITED = "GEO_FENCE_EXITED";

    /**
     * Get status code string string.
     *
     * @param transitionId the transition id
     * @return the string
     */
    public static String getStatusCodeString(int transitionId){
        switch (transitionId){
            case GEO_FENCE_ENTERED :
                return FENCE_ENTERED;
            case GEO_FENCE_EXITED :
                return FENCE_EXITED;
            case GEO_FENCE_DWELL :
                return "GEO_FENCE_DWELL";
            default:
                return "UNKNOWN_STATUS_CODE";
        }
    }
}
