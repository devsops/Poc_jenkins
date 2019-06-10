package com.bosch.pai.bearing.core.operation.training.location;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SvmClassifierData;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.persistence.datastore.DataStoreInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocationTrainerUtil.class, PersistenceHandler.class,DataStore.StoreType.class,DataStoreInfo.class,BearingConfiguration.Approach.class})
public class LocationTrainerUtilTest {

    private UUID uuid;

    Map<String, double[]> map;
    double[] doubles;
    SvmClassifierData svmClassifierData;

    @Mock
    BearingConfiguration.Approach approach;

    Timestamp timestamp;

    private LocationTrainerUtil locationTrainerUtil;

    @Mock
    private PersistenceHandler persistenceHandler1;

    private Set<String> sites;

    private List<String> siteList;

    @Before
    public void testBeforeSetUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(LocationTrainerUtil.class);
        timestamp = new Timestamp(194012022019L);
        locationTrainerUtil = new LocationTrainerUtil();
        uuid = UUID.randomUUID();
        sites = new HashSet<>();
        siteList = new ArrayList<String>();
        sites.add("DemoSite");
        siteList.add("DemoSite1");
        doubles = new double[]{1.2};
        map = new HashMap<>();
        map.put("DemoSite",doubles);
        svmClassifierData = new SvmClassifierData(timestamp,"DemoSite","DemoClassifier",siteList);

        }


    @Test
    public void currentSiteTest() throws Exception {
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler1);
        PowerMockito.when(persistenceHandler1.getSiteNames()).thenReturn(sites);
        assertEquals("DemoSite",locationTrainerUtil.getSiteNames().get(0));
    }

    @Test
    public void testGetLocationNames() throws Exception {
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler1);
        PowerMockito.when(persistenceHandler1.getLocationNames(anyString(),any(BearingConfiguration.Approach.class))).thenReturn(sites);
        assertEquals("DemoSite",locationTrainerUtil.getLocationNames("DemoSite",approach).get(0));

    }

    @Test
    public void testGetClassifierData() throws Exception {
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler1);
        PowerMockito.when(persistenceHandler1.readClassifiers("DemoSite")).thenReturn(svmClassifierData);
        assertEquals(svmClassifierData,locationTrainerUtil.getClassifierData("DemoSite"));
    }

    @Test
    public void testGetLocationFingerPrintData() throws Exception {
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler1);
        PowerMockito.when(persistenceHandler1.readLocationFingerPrintData("DemoSite","DemoLocation")).thenReturn(map);
        assertEquals(map,locationTrainerUtil.getLocationFingerPrintData("DemoSite","DemoLocation"));
    }

    @Test
    public void testGetLocationNamesFromBleThreshData() throws Exception {
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler1);
        PowerMockito.when(persistenceHandler1.getThreshLocationNames("DemoSite")).thenReturn(siteList);
        assertEquals(siteList,locationTrainerUtil.getLocationNamesFromBleThreshData("DemoSite"));
       // locationTrainerUtil.getLocationNamesFromClusterData()
    }

    @Test
    public void testGetLocationNamesFromClusterData() throws Exception {
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler1);
        PowerMockito.when(persistenceHandler1.getLocationNamesFromClassifier("DemoSite")).thenReturn(siteList);
        assertEquals(siteList,locationTrainerUtil.getLocationNamesFromClusterData("DemoSite"));
        // locationTrainerUtil.getLocationNamesFromClusterData()
    }

}
