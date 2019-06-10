package com.bosch.pai.bearing.detect;

import android.content.Context;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.detect.BearingDetector;
import com.bosch.pai.bearing.detect.operations.Detection;
import com.bosch.pai.bearing.detect.operations.Read;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.persistence.util.Util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.bosch.pai.*")
public class BearingDetectorTest {
    @Mock
    private Context context;
    @Mock
    private File dir;
    @Mock
    private BearingHandler bearingHandler;
    @Mock
    private AlgorithmLifeCycleHandler algoHandler;
    @Mock
    private BearingCallBack callback;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private Sensor sensor;
    @Mock
    private Read read;
    @Mock
    private Detection detection;

    private BearingDetector bearingDetector;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mockStatic(BearingHandler.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(AlgorithmLifeCycleHandler.class);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class), Mockito.any()))
                .thenReturn(sensor);
        PowerMockito.when(AlgorithmLifeCycleHandler.getInstance()).thenReturn(algoHandler);
        PowerMockito.when(algoHandler.isAlive()).thenReturn(false);
        PowerMockito.when(BearingHandler.getInstance()).thenReturn(bearingHandler);
        PowerMockito.when(bearingHandler.isAlive()).thenReturn(false);
        PowerMockito.when(context.getFilesDir()).thenReturn(dir);
        PowerMockito.when(dir.getPath()).thenReturn(File.separator);
        PowerMockito.whenNew(Read.class).withAnyArguments().thenReturn(read);
        PowerMockito.whenNew(Detection.class).withAnyArguments().thenReturn(detection);
        bearingDetector = BearingDetector.getInstance(context);
    }

    @Test
    public void testInvoke() {
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap = new HashMap<>();
        final List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        sensorTypeList.add(BearingConfiguration.SensorType.ST_IMU);
        approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypeList);
        final BearingConfiguration configuration = new BearingConfiguration(
                BearingConfiguration.OperationType.DETECT_SITE,
                approachListMap);
        bearingDetector.invoke(true, configuration, null, callback);
        Mockito.verify(detection)
                .invokeStartBearing(Mockito.any(BearingConfiguration.class), Mockito.isNull(BearingData.class), Mockito.any(BearingCallBack.class));
    }

}
