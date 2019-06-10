package com.bosch.pai.bearing.train.operations;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.operation.readoperations.BearingOperations;
import com.bosch.pai.bearing.core.operation.readoperations.SynchronousServerCalls;
import com.bosch.pai.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingHandler.class, BearingOperations.class, SiteTrainUtil.class})
public class UpdateTest {

    @Mock
    SynchronousServerCalls synchronousServerCalls;

    SiteTrainUtil siteTrainUtil;
    SiteTrainUtil siteTrainUtilSpy;

    @Mock
    PersistenceHandler persistenceHandler;

    @Mock
    Snapshot snapshot;

    Update update;
    Update updateSpy;


    boolean syncWithServer = true;
    BearingData bearingDataNew;
    BearingData bearingDataOld;
    SiteMetaData siteMetaDataNew;
    SiteMetaData siteMetaDataOld;
    String siteNameNew;
    String siteNameOld;

    @Mock
    BearingHandler bearingHandler;

    @Mock
    BearingCallBack bearingCallBack;


    @Before
    public void testBeforeSetUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //  snapshot = Mockito.spy(Snapshot.class);
        ///  doNothing().when(snapshot.setNoOfFloors(anyInt()));
        ////
        /*siteTrainUtil = new SiteTrainUtil();
        whenNew(SiteTrainUtil.class).withNoArguments().thenReturn(siteTrainUtil);
        siteTrainUtilSpy = Mockito.spy(siteTrainUtil);*/
        ////
        whenNew(SynchronousServerCalls.class).withAnyArguments().thenReturn(synchronousServerCalls);
        when(synchronousServerCalls.siteRenameSynchronousServerCall(anyString(),anyString())).thenReturn(true);

        whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler);
        when(persistenceHandler.renameSite(anyString(),anyString())).thenReturn(true);

        when(persistenceHandler.readSnapShot(anyString())).thenReturn(snapshot);
        doNothing().when(snapshot).setNoOfFloors(anyInt());


        update = new Update(bearingHandler);
        updateSpy = Mockito.spy(update);
        siteNameNew = "DemoSiteNew";
        siteMetaDataNew = new SiteMetaData(siteNameNew);
        siteNameOld = "DemoSiteOld";
        siteMetaDataOld = new SiteMetaData(siteNameOld);

        bearingDataNew = new BearingData(siteMetaDataNew);
        bearingDataOld = new BearingData(siteMetaDataOld);
    }

    @Test
    public void synchronousResponseSiteRenameTest()
    {
        syncWithServer = true;
        boolean bb = update.synchronousResponse(syncWithServer,bearingDataNew,bearingDataOld,BearingConfiguration.OperationType.SITE_RENAME);
        assertTrue(bb);
    }

    @Test
    public void synchronousResponseSiteUpdateTest()
    {
        syncWithServer = true;
        boolean bb = update.synchronousResponse(syncWithServer,bearingDataNew,bearingDataOld,BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE);
        assertFalse(bb);
    }

    @Test
    public void synchronousSiteRenameTest()
    {
        syncWithServer = false;
        boolean bb = update.synchronousResponse(syncWithServer,bearingDataNew,bearingDataOld,BearingConfiguration.OperationType.SITE_RENAME);
        assertTrue(bb);
    }

    @Test
    public void synchronousSiteUpdateTest() throws Exception {
        syncWithServer = false;
        boolean bb = update.synchronousResponse(syncWithServer,bearingDataNew,bearingDataOld,BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE);
        assertEquals(anyBoolean(),bb);
        //  verifyPrivate(siteTrainUtilSpy,Mockito.times(1)).invoke("canMergeBLEData",anyString(),anyListOf(SnapshotObservation.class));


    }

    @Test
    public void synchronousSnapshotUpdateTest()
    {
        syncWithServer = false;
        boolean bb = update.synchronousResponse(syncWithServer,bearingDataNew,bearingDataOld,BearingConfiguration.OperationType.SNAPSHOT_UPDATE);
        assertEquals(anyBoolean(),bb);
    }

    @Test
    public void synchronousUpdateSiteConfigTest()
    {
        syncWithServer = false;
        boolean bb = update.synchronousResponse(syncWithServer,bearingDataNew,bearingDataOld,BearingConfiguration.OperationType.UPDATE_SITE_CONFIG);
        assertEquals(anyBoolean(),bb);
    }

    @Test
    public void asynchronousUpdateSiteRenameTest() throws Exception {
        syncWithServer = true;

        update.asynchronousResponse(syncWithServer,bearingDataOld,bearingDataNew,BearingConfiguration.OperationType.SITE_RENAME,bearingCallBack);
        verifyPrivate(updateSpy,times(2)).
                invoke("requestForNonSyncUpdateRespWithSyncServer",anyString(),any(BearingConfiguration.OperationType.class),any(BearingData.class),
                        any(BearingData.class),any(BearingCallBack.class));
        // assertEquals(anyBoolean(),bb);
    }

    @Test
    public void updateSiteRenameTest() throws Exception {
        syncWithServer = false;

        update.asynchronousResponse(syncWithServer,bearingDataOld,bearingDataNew,BearingConfiguration.OperationType.SITE_RENAME,bearingCallBack);
        verifyPrivate(updateSpy,times(1)).
                invoke("requestForNonSyncUpdateRespWithSyncServer",anyString(),any(BearingConfiguration.OperationType.class),any(BearingData.class),
                        any(BearingData.class),any(BearingCallBack.class));
        // assertEquals(anyBoolean(),bb);
    }

    @Test
    public void updateSiteOnMergeTest() throws Exception {
        syncWithServer = false;

        update.asynchronousResponse(syncWithServer,bearingDataOld,bearingDataNew,BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE,bearingCallBack);
        verifyPrivate(updateSpy,times(1)).
                invoke("requestForNonSyncUpdateRespWithSyncServer",anyString(),any(BearingConfiguration.OperationType.class),any(BearingData.class),
                        any(BearingData.class),any(BearingCallBack.class));
        // assertEquals(anyBoolean(),bb);
    }



}
