package com.bosch.pai.bearing.core.operation.readoperations;


import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingRESTClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SynchronousServerCalls.class,BearingConfiguration.OperationType.class,BearingMode.class,BearingConfiguration.Approach.class})
public class UploadOperationsTest {

    BearingOperationCallback obj;

    @Mock
    BearingRESTClient bearingRESTClient;

    @Mock
    BearingOperationCallback bearingOperationCallback;

    @Mock
    SynchronousServerCalls synchronousServerCalls;

    @Mock
    UploadOperations getUploadOperations;

    @Mock
    BearingConfiguration.OperationType operationType;

    @Mock
    BearingData bearingData;

    @Mock
    BearingMode bearingMode;

    @Mock
    BearingConfiguration.Approach approach;

    /*@Mock
    BearingMode bearingModes;*/

    UploadOperations uploadOperations;

    @Before
    public void testBefore() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(SynchronousServerCalls.class);
        PowerMockito.mockStatic(BearingConfiguration.OperationType.class);
        uploadOperations = new UploadOperations(bearingRESTClient);
       // getUploadOperations = new UploadOperations(bearingRESTClient);
      //  PowerMockito.whenNew(SynchronousServerCalls.class).withAnyArguments().thenReturn(synchronousServerCalls);
    }

    @Test(timeout = 1000 * 10)
    public void registerBearingOperationCallbackTest()
    {
        getUploadOperations.registerBearingOperationCallback(bearingOperationCallback);
        Mockito.verify(getUploadOperations,Mockito.times(1)).registerBearingOperationCallback(bearingOperationCallback);
    }

    @Test(timeout = 1000*10)
    public void uploadSynchronousResponseServerCallTest()
    {
        boolean cc = getUploadOperations.uploadSynchronousResponseServerCall(operationType,bearingData,bearingMode);
        Mockito.verify(getUploadOperations,Mockito.times(1)).uploadSynchronousResponseServerCall(operationType,bearingData,bearingMode);
        assertFalse(cc);
    }

    /*@Test(timeout = 1000*10)
    public void uploadLocationsBasedOnApproachTest() throws Exception {
        //BearingConfiguration.Approach approach, BearingMode bearingMode, BearingData bearingData, String siteName
     //   PowerMockito.doReturn(true).when(UploadOperations.class,"uploadLocationsBasedOnApproach",(Object)approach,(Object)bearingMode,(Object)bearingData,(Object)"DemoSite");
        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {
                return null;
            }
        }).when(UploadOperations.class,"uploadLocationsBasedOnApproach",any(BearingConfiguration.Approach.class),any(BearingMode.class),any(BearingData.class),anyString());
     //   PowerMockito.doReturn(true).when(UploadOperations.class,"uploadLocationsBasedOnApproach",any(BearingConfiguration.Approach.class),any(BearingMode.class),any(BearingData.class),anyString());
      //  verifyPrivate(UploadOperations.class).invoke("uploadLocationsBasedOnApproach");
    }*/


}
