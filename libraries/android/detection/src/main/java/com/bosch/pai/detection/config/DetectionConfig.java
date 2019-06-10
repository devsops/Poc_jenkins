package com.bosch.pai.detection.config;

import com.bosch.pai.IeroIPSPlatformListener;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.detection.Util;
import com.bosch.pai.ipswrapper.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectionConfig {

    private final String tag = "[Configuration]";

    public DetectionConfig() {
        //default constuctor
    }

    public Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>>
    getApproachListMap(Map<Config.Key, Object> keyObjectMap, IeroIPSPlatformListener listener) {
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap = new HashMap<>();
        final List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
        try {
            final Config.SensorType sensorType = (Config.SensorType) keyObjectMap.get(Config.Key.SENSOR_TYPE);
            switch (sensorType) {
                case BLE:
                    sensorTypeList.add(BearingConfiguration.SensorType.ST_BLE);
                    approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypeList);
                    break;
                case WIFI:
                    sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
                    sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);
                    approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
                    break;
                case WIFI_BLE:
                    final List<BearingConfiguration.SensorType> temp1 = new ArrayList<>();
                    temp1.add(BearingConfiguration.SensorType.ST_BLE);
                    approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, temp1);
                    final List<BearingConfiguration.SensorType> temp2 = new ArrayList<>();
                    temp2.add(BearingConfiguration.SensorType.ST_WIFI);
                    temp2.add(BearingConfiguration.SensorType.ST_IMU);
                    approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, temp2);
                    break;
                default:
                    Util.addLogs(Util.LOG_STATUS.ERROR, tag, "Not a valid sensor type", null);
            }
        } catch (ClassCastException | NullPointerException  e) {
            Util.addLogs(Util.LOG_STATUS.ERROR, tag, "Not a valid sensor type", null);
            listener.onFailure("Not a valid sensor type");
            return null;
        }
        return approachListMap;
    }
}
