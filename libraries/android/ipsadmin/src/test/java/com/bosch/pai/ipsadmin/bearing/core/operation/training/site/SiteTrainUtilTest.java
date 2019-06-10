package com.bosch.pai.ipsadmin.bearing.core.operation.training.site;

import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.persistence.PersistenceHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SiteTrainUtil.class,SnapshotObservation.class,})
public class SiteTrainUtilTest {
    SiteTrainUtil siteTrainUtil;

    @Mock
    SiteTrainUtil siteTrainUtil1;

    //@Mock
    PersistenceHandler persistenceHandler;


    @Mock
    List<SnapshotObservation> snapshotObservationList;

    @Mock
    SnapshotObservation snapshotObservation;

    @Mock
    TrainSiteListener trainSiteListener;

    Set<String> sites;


    @Before
    public void testBeforeSetUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(SiteTrainUtil.class);
        PowerMockito.mockStatic(SnapshotObservation.class);
        sites = new HashSet<String>();
        sites.add("DemoSite");
        siteTrainUtil = new SiteTrainUtil();
        siteTrainUtil.setSiteName("DemoSite");
        siteTrainUtil.setAutoMergeEnabled(true);
       // siteTrainUtil.setApproach(new BearingConfiguration.Approach())
      //  siteTrainUtil.setObservationDataType(new RequestDataHolder.ObservationDataType());
        siteTrainUtil.setTransactionId(UUID.randomUUID());
        persistenceHandler = PowerMockito.mock(PersistenceHandler.class);
        whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler);
        when(persistenceHandler.getSiteNames()).thenReturn(sites);

        when(snapshotObservationList.isEmpty()).thenReturn(false);
        when(snapshotObservationList.get(0)).thenReturn(snapshotObservation);
//        when(snapshotObservationList.get(0)).thenReturn(BearingConfiguration.SensorType.ST_BLE);
        //BearingConfiguration.SensorType.ST_BLE


      //  PowerMockito.doReturn(true).when(SiteTrainUtil.class,"canMergeBLEData",anyString(),anyListOf(SnapshotObservation.class));

      //  when(SiteTrainUtil.class,"canMergeBLEData",anyString(),any(List.class)).thenReturn(true);
      //  when(SiteTrainUtil.class,"appendDataToPersistence",anyObject(),anyObject()).thenReturn(true);

    }

    @Test
    public void siteTrainTest()
    {
      boolean bb = siteTrainUtil.addToSite("DemoSite",snapshotObservationList);
      assertNotNull(bb);
      //  Mockito.verify(siteTrainUtil1,Mockito.times(1)).addToSite(anyString(),anyListOf(SnapshotObservation.class));
    }

    @Test
    public void setSiteTrainCallbackTest(){
        siteTrainUtil.setSiteTrainCallback(trainSiteListener);
    }

}
