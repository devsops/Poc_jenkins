package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.config;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectionConfig {

    private DetectionMode detectionMode;

    public DetectionConfig(DetectionMode detectionMode) {
        this.detectionMode = detectionMode;
    }

    public void setDetectionMode(DetectionMode dm) {
        detectionMode = dm;
    }

    public DetectionMode getDetectionMode() {
        return detectionMode;
    }

    public Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> getApproachListMapForDetection() {

        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap = new HashMap<>();

        final List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);


        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
        sensorTypes.add(BearingConfiguration.SensorType.ST_BLE);

        switch (getDetectionMode()) {
            case WIFI:
                approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
                break;
            case BLE:
                approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypes);
                break;
            case WIFI_BLE:
                approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
                approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypes);
                break;
            default:
                approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
                break;
        }

        return approachListMap;
    }
}
