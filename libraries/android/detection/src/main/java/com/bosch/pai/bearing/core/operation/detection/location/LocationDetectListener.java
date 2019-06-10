package com.bosch.pai.bearing.core.operation.detection.location;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import java.util.Map;
import java.util.UUID;

/**
 * The interface Location detect listener.
 */
public interface LocationDetectListener {

    /**
     * On error detecting location.
     *
     * @param uuid       the uuid
     * @param errMessage the err message
     */
    void onErrorDetectingLocation(UUID uuid, String errMessage);

    /**
     * On location update.
     *
     * @param uuid                     the uuid
     * @param approach                 the approach responding with data
     * @param siteName                 the site name
     * @param locationToProbabilityMap the location to probability map
     * @param localTime                the local time
     */
    void onLocationUpdate(UUID uuid, BearingConfiguration.Approach approach, String siteName, Map<String, Double> locationToProbabilityMap, String localTime);
}
