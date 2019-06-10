package com.bosch.pai.ipsadmin.bearing.train.operations;


import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;

import java.util.List;
import java.util.Set;

/**
 * The type Validation.
 */
public class Validation {

    /**
     * Validate create input boolean.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param bearingData   the bearing data
     * @return the boolean
     */
    public static boolean validateCreateInput(BearingConfiguration bearingConfiguration, BearingData bearingData) {

        final BearingMode bearingMode = BearingRequestParser.getBearingModeForTraining(bearingData);
        final Set<BearingConfiguration.Approach> bearingApproachList = BearingRequestParser.getApproach(bearingConfiguration);
        final List<BearingConfiguration.SensorType> bearingSensorTypeList = BearingRequestParser.validateSensorTypeList(bearingConfiguration);
        final String siteName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.SITE);
        if (bearingMode == null || bearingApproachList == null || bearingSensorTypeList == null || siteName == null)
            return false;

        return true;
    }


}
