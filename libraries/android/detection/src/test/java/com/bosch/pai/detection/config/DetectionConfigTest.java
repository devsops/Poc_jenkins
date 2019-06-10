package com.bosch.pai.detection.config;

import com.bosch.pai.IeroIPSPlatformListener;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.detection.Util;
import com.bosch.pai.ipswrapper.Config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DetectionConfig.class)
public class DetectionConfigTest {

    @Mock
    private IeroIPSPlatformListener ieroIPSPlatformListener;
    @Mock
    Util util;

    private DetectionConfig detectionConfig;

    private Map<Config.Key, Object> detectionConfigMap = new HashMap<>();
    private Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap = new HashMap<>();
    private List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
    private List<BearingConfiguration.SensorType> sensorTypeList1 = new ArrayList<>();

    @Before
    public void init() throws Exception {
        detectionConfig = new DetectionConfig();
        PowerMockito.mock(IeroIPSPlatformListener.class);
    }

    @Test
    public void getApproachListMapTest(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfigMap.clear();
        detectionConfigMap.put(Config.Key.SENSOR_TYPE, Config.SensorType.WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);
        approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
        Assert.assertEquals(approachListMap, detectionConfig.getApproachListMap(detectionConfigMap,ieroIPSPlatformListener));
    }

    @Test
    public void getApproachListMapForDetectionTest(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfigMap.clear();
        detectionConfigMap.put(Config.Key.SENSOR_TYPE, Config.SensorType.BLE);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_BLE);
        approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypeList);
        Assert.assertEquals(approachListMap, detectionConfig.getApproachListMap(detectionConfigMap,ieroIPSPlatformListener));
    }

    @Test
    public void getApproachListMapForDetection2Test(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfigMap.clear();
        detectionConfigMap.put(Config.Key.SENSOR_TYPE, Config.SensorType.WIFI_BLE);
        sensorTypeList1.add(BearingConfiguration.SensorType.ST_BLE);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);
        approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
        approachListMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypeList1);
        Assert.assertEquals(approachListMap, detectionConfig.getApproachListMap(detectionConfigMap,ieroIPSPlatformListener));
    }

    @Test
    public void getApproachListMapForDetectionDefalutTest(){
        sensorTypeList.clear();
        approachListMap.clear();
        detectionConfigMap.clear();
        detectionConfigMap.put(Config.Key.SENSOR_TYPE, null);
        Assert.assertNull(detectionConfig.getApproachListMap(detectionConfigMap,ieroIPSPlatformListener));
    }
}
