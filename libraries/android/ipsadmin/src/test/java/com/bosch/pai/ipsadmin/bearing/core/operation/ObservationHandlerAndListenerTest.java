package com.bosch.pai.ipsadmin.bearing.core.operation;


import android.os.Looper;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


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
