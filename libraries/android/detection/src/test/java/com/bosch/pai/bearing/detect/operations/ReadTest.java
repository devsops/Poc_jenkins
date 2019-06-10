package com.bosch.pai.bearing.detect.operations;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.comms.exception.CertificateLoadException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HandlerThread.class, BearingHandler.class})
public class ReadTest {

    Read read;

    @Mock
    BearingHandler bearingHandler;

    BearingConfiguration bearingConfiguration;
    boolean syncWithServer = true;
    String ipAddress;
    BearingData bearingData;

    @Mock
    InputStream inputStream;



    @Mock
    HandlerThread handlerThread;

    @Mock
    Looper looper;

    @Mock
    BearingCallBack bearingCallBack;

    @Mock
    BearingOperations bearingOperations;



    @Mock
    Context context;

    @Before
    public void testBeforeSetUp() throws CertificateLoadException {
        MockitoAnnotations.initMocks(this);

      //  handlerThread = new HandlerThread("SensorObservation Handler");
         /*BearingHandler.init(context);
         bearingHandler = BearingHandler.getInstance();*/

        when(handlerThread.getLooper()).thenReturn(looper);
        ipAddress = "123.123.124.15";
        bearingData = new BearingData(new SiteMetaData("DemoSite"));

        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.SET_SERVER_ENDPOINT,ipAddress,inputStream);
        bearingOperations = new BearingOperations();

    }

    @Test
    public void testConstructor()
    {
        read = new Read(bearingHandler);
        assertNotNull(read);

    }

    @Test
    public void synchronousResponseReadOperationTest()
    {
        read = new Read(bearingHandler);
        BearingOutput bearingOutput = read.synchronousResponseReadOperation(bearingConfiguration,syncWithServer,bearingData);
     //   Mockito.verify(bearingOperations,Mockito.times(1)).readSynchronousDataAfterSyncWithServer(any(),any());
      //  bearingOutput.getBody().getDetectionLevel()
        assertEquals(StatusCode.OK,bearingOutput.getHeader().getStatusCode());
    }

    @Test
    public void synchronousResponseReadStateFalseTest()
    {
        syncWithServer = false;
        read = new Read(bearingHandler);
        BearingOutput bearingOutput = read.synchronousResponseReadOperation(bearingConfiguration,syncWithServer,bearingData);
        assertEquals(StatusCode.BAD_REQUEST,bearingOutput.getHeader().getStatusCode());
    }

    @Test
    public void synchronousResponseDifferentEndpointTest()
    {
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,ipAddress,inputStream);
        syncWithServer = false;
        read = new Read(bearingHandler);
        BearingOutput bearingOutput = read.synchronousResponseReadOperation(bearingConfiguration,syncWithServer,bearingData);
        assertEquals(StatusCode.OK,bearingOutput.getHeader().getStatusCode());
    }

    @Test
    public void asynchronousResponseReadOperationTest()
    {
        syncWithServer = true;
        read = new Read(bearingHandler);
        read.asynchronousResponseReadOperation(syncWithServer,bearingConfiguration,bearingData,bearingCallBack);
        verify(bearingHandler,Mockito.times(1)).enqueue(anyString(),any(Event.class),any(EventType.class));
    }

    @Test
    public void asynchronousResponseNotSyncTest()
    {
        syncWithServer = false;
        read = new Read(bearingHandler);
        read.asynchronousResponseReadOperation(syncWithServer,bearingConfiguration,bearingData,bearingCallBack);
        verify(bearingHandler,Mockito.times(1)).enqueue(anyString(),any(Event.class),any(EventType.class));
    }

    @Test
    public void downloadSourceIdMapTest()
    {
        read = new Read(bearingHandler);
        read.downloadSourceIdMap(bearingCallBack);
        verify(bearingHandler,Mockito.times(1)).enqueue(anyString(),any(Event.class),any(EventType.class));
    }

    @Test
    public void downloadSiteThreshDataTest()
    {
        read = new Read(bearingHandler);
        read.downloadSiteThreshData("DemoSite",bearingCallBack);
        verify(bearingHandler,Mockito.times(1)).enqueue(anyString(),any(Event.class),any(EventType.class));
    }


}
