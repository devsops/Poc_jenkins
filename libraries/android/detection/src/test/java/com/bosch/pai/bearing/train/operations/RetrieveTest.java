package com.bosch.pai.bearing.train.operations;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.core.operation.readoperations.SynchronousServerCalls;
import com.bosch.pai.bearing.core.operation.training.location.LocationTrainerUtil;
import com.bosch.pai.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.StorageType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingHandler.class, SynchronousServerCalls.class, BearingOperations.class, SiteTrainUtil.class, LocationTrainerUtil.class})
public class RetrieveTest {

    Retrieve retrieve;
    Retrieve retrieveSpy;
    boolean syncServer = true;
    Set<String> sites;
    BearingConfiguration bearingConfiguration;
    BearingData bearingData;

    List<String> listOfSites;

    //  @Mock
    //  RequestRetrieveEvent requestRetrieveEvent;

    @Mock
    BearingCallBack bearingCallBack;

    @Mock
    PersistenceHandler persistenceHandler;

    @Mock
    BearingOperations bearingOperations;

    @Mock
    BearingHandler bearingHandler;

    @Mock
    SynchronousServerCalls synchronousServerCalls;

    @Before
    public void testBefore() throws Exception {
        MockitoAnnotations.initMocks(this);
        sites = new HashSet<String>();
        sites.add("DemoSite");
        listOfSites = new ArrayList<String>();
        listOfSites.add("DemoSite");
        //
        // requestRetrieveEvent = spy(RequestRetrieveEvent.class);
        //
        whenNew(PersistenceHandler.class).withArguments(any(StorageType.class)).thenReturn(persistenceHandler);
        when(persistenceHandler.getSiteNames()).thenReturn(sites);
        whenNew(SynchronousServerCalls.class).withAnyArguments().thenReturn(synchronousServerCalls);
        when(synchronousServerCalls.getAllSitesSyncWithServer(true)).thenReturn(listOfSites);
        retrieve = new Retrieve(bearingHandler);
        retrieveSpy = Mockito.spy(retrieve);
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));



        //  whenNew(SynchronousServerCalls.class).withAnyArguments().thenReturn(synchronousServerCalls);
        //  whenNew(BearingOperations.class).withAnyArguments().thenReturn(bearingOperations);
        //  when(synchronousServerCalls.getAllSitesSyncWithServer(true)).thenReturn(listOfSites);
        //  when(synchronousServerCalls.getAllSitesSyncWithServer(true)).thenReturn(listOfSites);

    }

    @Test
    public void synchronousResponseTest()
    {
        syncServer = true;
        BearingOutput bearingOutput = retrieve.synchronousResponse(syncServer,bearingConfiguration,bearingData);
        assertNotNull(bearingOutput);
        assertEquals(StatusCode.OK,bearingOutput.getHeader().getStatusCode());
        ///
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
        bearingOutput = retrieve.synchronousResponse(syncServer,bearingConfiguration,bearingData);
        assertNotNull(bearingOutput);
        assertEquals(StatusCode.OK,bearingOutput.getHeader().getStatusCode());


    }

    @Test
    public void synchronousResponseWithoutSyncTest() throws Exception {


        syncServer = false;
        BearingOutput bearingOutput = retrieve.synchronousResponse(syncServer,bearingConfiguration,bearingData);
        assertNotNull(bearingOutput);
        assertEquals(StatusCode.OK,bearingOutput.getHeader().getStatusCode());
        ///
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
        bearingOutput = retrieve.synchronousResponse(syncServer,bearingConfiguration,bearingData);
        assertNotNull(bearingOutput);
        assertEquals(StatusCode.OK,bearingOutput.getHeader().getStatusCode());

    }



    @Test
    public void responseTestReadSiteList() throws Exception {
        syncServer = true;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncResponseWithSyncWithServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }

    @Test
    public void responseTestReadLocList() throws Exception {
        syncServer = true;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncResponseWithSyncWithServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }

    @Test
    public void responseTestReadThreshList() throws Exception {
        syncServer = true;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_THRESH_LIST);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncResponseWithSyncWithServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }

    @Test
    public void responseTestReadSiteListServer() throws Exception {
        syncServer = true;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST_ON_SERVER);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncResponseWithSyncWithServer",anyString(),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }


    @Test
    public void asynchResponseTestReadSiteList() throws Exception {
        syncServer = false;
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncRetrieveRespWithoutSyncServer",anyString(),any(BearingConfiguration.class),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));

    }

    @Test
    public void asynchResponseTestReadLocList() throws Exception {
        syncServer = false;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncRetrieveRespWithoutSyncServer",anyString(),any(BearingConfiguration.class),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }

    @Test
    public void asynchResponseTestReadThreshList() throws Exception {
        syncServer = false;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_THRESH_LIST);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncRetrieveRespWithoutSyncServer",anyString(),any(BearingConfiguration.class),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }

    @Test
    public void asynchResponseTestReadSiteListServer() throws Exception {
        syncServer = false;
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST_ON_SERVER);
        retrieve.asynchronousResponse(syncServer,bearingConfiguration,bearingData,bearingCallBack);
        verifyPrivate(retrieveSpy,times(1)).
                invoke("requestForNonSyncRetrieveRespWithoutSyncServer",anyString(),any(BearingConfiguration.class),any(BearingConfiguration.OperationType.class),
                        any(BearingData.class),any(BearingCallBack.class));
    }


}
