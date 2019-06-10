package com.bosch.pai.bearing.core.operation;


import android.os.Looper;
import android.support.annotation.NonNull;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.config.AlgorithmConfiguration;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.operation.detection.location.ClusterDataDownloaderTest;
import com.bosch.pai.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.bearing.sensordatastore.event.DataCaptureResponseEvent;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingRESTClient;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.api.SensorObservation;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceStateManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


//@PrepareForTest({ObservationHandlerAndListener.class, SensorObservation.class, Looper.class})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ObservationHandlerAndListener.class, SensorObservation.class, Looper.class})
public class ObservationHandlerAndListenerTest {

    private SensorObservation sensorObservation;
    private ObservationHandlerAndListener observationHandlerAndListener;

    @Mock
    private ObservationHandlerAndListener mockObservationHandlerListener;
    @Mock
    private SensorObservation mockSensorObservation;


    public ObservationHandlerAndListenerTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        initMockEnvironment();
        observationHandlerAndListener = new ObservationHandlerAndListener();
        sensorObservation = SensorObservation.getInstance();
    }

    private void initMockEnvironment() throws Exception {

        PowerMockito.mockStatic(ObservationHandlerAndListener.class);
        PowerMockito.mockStatic(SensorObservation.class);
        PowerMockito.whenNew(ObservationHandlerAndListener.class).withNoArguments().thenReturn(mockObservationHandlerListener);
        PowerMockito.whenNew(SensorObservation.class).withArguments(Looper.class).thenReturn(mockSensorObservation);

    }


    @Test
    public void setObservationDataTypeTest() {
        observationHandlerAndListener.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_DETECTION);
        Mockito.verify(mockObservationHandlerListener, Mockito.times(1)).setObservationDataType(RequestDataHolder.ObservationDataType.SITE_DETECTION);

    }

    @Test
    public void registerSensorObservationListenerTest() {
        observationHandlerAndListener.registerSensorObservationListener();
        Mockito.verify(mockObservationHandlerListener, Mockito.times(1)).registerSensorObservationListener();
    }

    @Test
    public void removeAndUnregisterSensorObservationTest() {
        observationHandlerAndListener.removeAndUnregisterSensorObservation(null);
        Mockito.verify(mockObservationHandlerListener, Mockito.times(1)).removeAndUnregisterSensorObservation(null);
    }

    @Test
    public void setApproachTest() {

        observationHandlerAndListener.setApproach(BearingConfiguration.Approach.DATA_CAPTURE);
        Mockito.verify(mockObservationHandlerListener, Mockito.times(1)).setApproach(BearingConfiguration.Approach.DATA_CAPTURE);
    }


//    @Test
//    public void addObservationSourceTestTrue() {
//
//        UUID test_uuid = UUID.randomUUID();
//        List<BearingConfiguration.SensorType> testSensorTypeList = new ArrayList<>();
//        testSensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
//        when(mockSensorObservation.addSource(test_uuid, testSensorTypeList)).thenReturn(true);
//        final boolean testResponse = observationHandlerAndListener.addObservationSource(test_uuid, testSensorTypeList, true);
//        Assert.assertTrue(testResponse);
//
//
//    }


}
