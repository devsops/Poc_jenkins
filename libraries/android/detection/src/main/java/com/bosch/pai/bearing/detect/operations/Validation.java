package com.bosch.pai.bearing.detect.operations;


import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Validation.
 */
public class Validation {

    /**
     * Is valid bearingConfiguration request boolean.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @return the boolean
     */
    public boolean isValidConfigurationRequest(BearingConfiguration bearingConfiguration) {

        if (validateSensorTypesWithRuleEngine(bearingConfiguration)) {
            final Set<BearingConfiguration.Approach> bearingApproach = BearingRequestParser.parseConfigurationForApproachList(bearingConfiguration);
            for (BearingConfiguration.Approach approach : bearingApproach) {
                final List<BearingConfiguration.SensorType> sensorTypes = BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);
                if (sensorTypes == null || sensorTypes.isEmpty())
                    return false;
            }

            final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);
            return operationType != null;
        } else {
            return false;
        }
    }


    private boolean validateSensorTypesWithRuleEngine(BearingConfiguration bearingConfiguration) {

        boolean isValidSensors = true;
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachSensorListMap = bearingConfiguration.getApproachSensorListMap();

        for (BearingConfiguration.Approach approach : approachSensorListMap.keySet()) {
            isValidSensors = isValidSensors && validateRules(approach, approachSensorListMap.get(approach));
        }
        return isValidSensors;
    }

    private boolean validateRules(BearingConfiguration.Approach approach, List<BearingConfiguration.SensorType> sensorTypeList) {

        boolean validSensor = false;
        if (approach.equals(BearingConfiguration.Approach.FINGERPRINT)) {
            for (ConfigurationSettings.FINGERPRINT_RULEBOOK fingerPrintRule : ConfigurationSettings.FINGERPRINT_RULEBOOK.values()) {
                validSensor = validSensor || compareList(sensorTypeList, fingerPrintRule.getSensors());
            }

        } else if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
            for (ConfigurationSettings.THRESHOLD_RULEBOOK thresholdRule : ConfigurationSettings.THRESHOLD_RULEBOOK.values()) {
                validSensor = validSensor || compareList(sensorTypeList, thresholdRule.getSensors());
            }
        }
        return validSensor;
    }

    /**
     * Compare list boolean.
     *
     * @param ls1 the ls 1
     * @param ls2 the ls 2
     * @return the boolean
     */
    public boolean compareList(List ls1, List ls2) {
        return ls1.containsAll(ls2) && ls1.size() == ls2.size() ? true : false;
    }


}
