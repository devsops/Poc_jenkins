package com.bosch.pai.bearing.train.operations;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.readoperations.SynchronousServerCalls;
import com.bosch.pai.bearing.core.operation.readoperations.UploadOperations;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingHandler.class, UploadOperations.class})
public class UploadTest {

    Upload upload;

    @Mock
    Upload uploadSpy;

    BearingConfiguration bearingConfiguration;
    BearingData bearingData;
    SiteMetaData siteMetaData;
    String siteName;

    @Mock
    SynchronousServerCalls synchronousServerCalls;

    @Mock
    BearingCallBack bearingCallBack;

    @Mock
    BearingHandler bearingHandler;

    @Before
    public void testBefore() throws Exception {
        MockitoAnnotations.initMocks(this);

        whenNew(SynchronousServerCalls.class).withAnyArguments().thenReturn(synchronousServerCalls);
        PowerMockito.when(synchronousServerCalls.uploadSiteData(anyString())).thenReturn(true);

        siteName = "DemoSite";
        siteMetaData = new SiteMetaData(siteName);
        bearingData = new BearingData(siteMetaData);
        upload = new Upload(bearingHandler);
        // uploadSpy = Mockito.spy(upload);
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_SITE);
    }

    @Test
    public void synchronousResponseUploadSiteTest()
    {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_SITE);
        boolean bb = upload.synchronousResponse(bearingConfiguration,bearingData);
        assertNotNull(bb);
        assertTrue(bb);
    }

    @Test
    public void synchronousResponseUploadLocationCsvTest()
    {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_LOCATION_CSV);
        boolean bb = upload.synchronousResponse(bearingConfiguration,bearingData);
        assertNotNull(bb);
        assertEquals(anyBoolean(),bb);
    }

    @Test
    public void synchronousResponseUploadLocationThreshTest()
    {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_LOCATION_THRESH);
        boolean bb = upload.synchronousResponse(bearingConfiguration,bearingData);
        assertNotNull(bb);
        assertEquals(anyBoolean(),bb);
    }

    @Test
    public void asynchronousResponseUploadSiteTest() throws Exception {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_SITE);
        upload.asynchronousResponse(bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(uploadSpy,times(1)).
                invoke("requestNonSyncResponseUploadForServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));


    }

    @Test
    public void asynchronousResponseUploadLocationCsvTest() throws Exception {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_LOCATION_CSV);
        upload.asynchronousResponse(bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(uploadSpy,times(1)).
                invoke("requestNonSyncResponseUploadForServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }

    @Test
    public void asynchronousResponseUploadLocationThreshTest() throws Exception {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_LOCATION_THRESH);
        upload.asynchronousResponse(bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(uploadSpy,times(1)).
                invoke("requestNonSyncResponseUploadForServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }


}
