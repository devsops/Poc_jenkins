package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.config;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.detect.operations.Detection;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionMode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DetectionConfig.class, DetectionMode.class})
public class DetectionConfigTest {

    private DetectionMode detectionMode;

    private DetectionConfig detectionConfig;
    private Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap = new HashMap<>();
    private List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
    private List<BearingConfiguration.SensorType> sensorTypeList1 = new ArrayList<>();

    @Before
    public void init() throws Exception {
        detectionConfig = new DetectionConfig(detectionMode);
        detectionConfig.setDetectionMode(DetectionMode.BLE);
    }

    @Test
    public void getApproachListMapForDetectionTest(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfig.setDetectionMode(DetectionMode.BLE);
        Assert.assertEquals(DetectionMode.BLE,detectionConfig.getDetectionMode());
        sensorTypeList.add(BearingConfiguration.SensorType.ST_BLE);
        approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypeList);
        Assert.assertEquals(approachListMap, detectionConfig.getApproachListMapForDetection());
    }

    @Test
    public void getApproachListMapForDetection1Test(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfig.setDetectionMode(DetectionMode.WIFI);
        Assert.assertEquals(DetectionMode.WIFI,detectionConfig.getDetectionMode());
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);
        approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
        Assert.assertEquals(approachListMap, detectionConfig.getApproachListMapForDetection());
    }

    @Test
    public void getApproachListMapForDetection2Test(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfig.setDetectionMode(DetectionMode.WIFI_BLE);
        Assert.assertEquals(DetectionMode.WIFI_BLE,detectionConfig.getDetectionMode());
        sensorTypeList1.add(BearingConfiguration.SensorType.ST_BLE);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);
        approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
        approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypeList1);
        Assert.assertEquals(approachListMap, detectionConfig.getApproachListMapForDetection());
    }
}
