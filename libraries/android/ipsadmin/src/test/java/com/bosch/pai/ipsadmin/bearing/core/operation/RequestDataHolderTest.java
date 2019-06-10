package com.bosch.pai.ipsadmin.bearing.core.operation;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestDataHolder.class, ObservationHandlerAndListener.class})
public class RequestDataHolderTest {

    @Mock
    private RequestDataHolder mockRequestDataHolder;
    private RequestDataHolder requestDataHolder;


    public RequestDataHolderTest() throws Exception {

        MockitoAnnotations.initMocks(this);
        initMockEnvironment();
        PowerMockito.mockStatic(RequestDataHolder.class);

        UUID test_uuid = UUID.randomUUID();
        ObservationHandlerAndListener observationHandlerAndListener = new ObservationHandlerAndListener();
        requestDataHolder = new RequestDataHolder(test_uuid, RequestDataHolder.ObservationDataType.SITE_DETECTION, observationHandlerAndListener);


    }

    private void initMockEnvironment() throws Exception {
        PowerMockito.mockStatic(RequestDataHolder.class);
        PowerMockito.whenNew(RequestDataHolder.class).withAnyArguments().thenReturn(mockRequestDataHolder);

    }

    @Test
    public void getObservationDataTypeTest() {
        requestDataHolder.getObservationDataType();
        Mockito.verify(mockRequestDataHolder, times(1)).getObservationDataType();
    }

    @Test
    public void getObservationHandlerAndListenerTest() {

        requestDataHolder.getObservationHandlerAndListener();
        Mockito.verify(mockRequestDataHolder, times(1)).getObservationHandlerAndListener();

    }

    @Test
    public void setActiveModeOnTest() {

        requestDataHolder.setActiveModeOn(true);
        Mockito.verify(mockRequestDataHolder, times(1)).setActiveModeOn(true);
    }

    @Test
    public void isActiveModeOnTest() {
        requestDataHolder.isActiveModeOn();
        Mockito.verify(mockRequestDataHolder, times(1)).isActiveModeOn();
    }


    @Test
    public void setSiteNameTest() {
        requestDataHolder.setSiteName("TEST");
        Mockito.verify(mockRequestDataHolder, times(1)).setSiteName("TEST");
    }


    @Test
    public void setLocationNameTest() {
        requestDataHolder.setLocationName("TEST_LOCATION");
        Mockito.verify(mockRequestDataHolder, times(1)).setLocationName("TEST_LOCATION");
    }


    @Test
    public void getSiteNameTest() {
        requestDataHolder.getSiteName();
        Mockito.verify(mockRequestDataHolder, times(1)).getSiteName();
    }

    @Test
    public void getLocationNameTest() {
        requestDataHolder.getLocationName();
        Mockito.verify(mockRequestDataHolder, times(1)).getLocationName();
    }

    @Test
    public void getApproachTest() {
        requestDataHolder.getApproach();
        Mockito.verify(mockRequestDataHolder, times(1)).getApproach();
    }

    @Test
    public void setApproachTest() {
        requestDataHolder.setApproach(BearingConfiguration.Approach.DATA_CAPTURE);
        Mockito.verify(mockRequestDataHolder, times(1)).setApproach(BearingConfiguration.Approach.DATA_CAPTURE);
    }

    @Test
    public void getNoOfFloorsTest() {
        requestDataHolder.getNoOfFloors();
        Mockito.verify(mockRequestDataHolder, times(1)).getNoOfFloors();
    }


    public void getSensorTypeListTest() {
        requestDataHolder.getSensorTypeList();
        Mockito.verify(mockRequestDataHolder, times(1)).getSensorTypeList();
    }

    @Test
    public void setSensorTypeListTest() {
        requestDataHolder.setSensorTypeList(new ArrayList<BearingConfiguration.SensorType>());
        Mockito.verify(mockRequestDataHolder, times(1)).setSensorTypeList(new ArrayList<BearingConfiguration.SensorType>());
    }

    @Test
    public void getRequestIdTest() {
        requestDataHolder.getRequestId();
        Mockito.verify(mockRequestDataHolder, times(1)).getRequestId();
    }

    @Test
    public void setNoOfFloorsTest() {

        requestDataHolder.setNoOfFloors(1);
        Mockito.verify(mockRequestDataHolder, times(1)).setNoOfFloors(1);

    }


}
