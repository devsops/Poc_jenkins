package com.bosch.pai.ipsadmin.bearing.core.operation.detection.site;


import android.util.Log;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.algorithm.event.DetectionCalculationEvent;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.util.SnapshotItemManager;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SiteDetectorUtil.class, AlgorithmLifeCycleHandler.class,
        BearingHandler.class, ObservationHandlerAndListener.class, SiteDetectListener.class, Log.class, PersistenceHandler.class})
public class SiteDetectorUtilTest {

    private SiteDetectorUtil siteDetectorUtil;

    @Mock
    private SiteDetectListener siteDetectListenerMock;

    @Mock
    private ObservationHandlerAndListener observationHandlerAndListenerMock;

    @Mock
    private PersistenceHandler persistenceHandlerMock;

    @Mock
    private SnapshotItemManager snapshotManager;

    @Mock
    private AlgorithmLifeCycleHandler algorithmLifeCycleHandler;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(BearingHandler.class);
        PowerMockito.mockStatic(AlgorithmLifeCycleHandler.class);
        PowerMockito.whenNew(ObservationHandlerAndListener.class).withNoArguments().thenReturn(observationHandlerAndListenerMock);
        PowerMockito.whenNew(PersistenceHandler.class).withArguments(Mockito.any(DataStore.StoreType.class))
                .thenReturn(persistenceHandlerMock);
        PowerMockito.when(persistenceHandlerMock.readSourceIdMapWithConfiguration()).thenReturn(new HashMap<>());
        PowerMockito.when(persistenceHandlerMock.readSourceIdMap()).thenReturn(new HashMap<>());
        PowerMockito.whenNew(SnapshotItemManager.class).withNoArguments().thenReturn(snapshotManager);
        PowerMockito.when(snapshotManager.convertToSourceIdMapWithConfiguration(Mockito.any(Map.class)))
                .thenReturn(new HashMap<>());
        PowerMockito.when(AlgorithmLifeCycleHandler.getInstance()).thenReturn(algorithmLifeCycleHandler);
        PowerMockito.doNothing().when(algorithmLifeCycleHandler, "enqueue", Mockito.anyString(), Mockito.any(DetectionCalculationEvent.class),
                Mockito.any(EventType.class), Mockito.anyString());
        siteDetectorUtil = new SiteDetectorUtil();
        siteDetectorUtil.setCallback(siteDetectListenerMock);
    }

    @Test
    public void setObservationDataTypeTest() {
        siteDetectorUtil.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_DETECTION);
        Mockito.verify(observationHandlerAndListenerMock, times(1))
                .setObservationDataType(RequestDataHolder.ObservationDataType.SITE_DETECTION);
    }


    @Test
    public void registerSensorObservationListenerTest() {

        siteDetectorUtil.registerSensorObservationListener();
        Mockito.verify(observationHandlerAndListenerMock, times(1)).registerSensorObservationListener();

    }


    @Test
    public void startSiteDetectionTest() {
        UUID uuid = UUID.randomUUID();
        List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
        sensorTypeList.add(BearingConfiguration.SensorType.ST_WIFI);
        PowerMockito.when(observationHandlerAndListenerMock.addObservationSource(Mockito.any(UUID.class),
                Mockito.anyListOf(BearingConfiguration.SensorType.class), Mockito.anyBoolean())).thenReturn(true);

        siteDetectorUtil.startSiteDetection(BearingConfiguration.Approach.FINGERPRINT, uuid, sensorTypeList);
        PowerMockito.verifyStatic(Mockito.times(1));
        BearingHandler.addRequestToRequestDataHolderMap(Mockito.any(RequestDataHolder.class));
    }

    @Test
    public void getSiteNamesTest() {

        Mockito.when(persistenceHandlerMock.getSiteNames()).thenReturn(new HashSet<String>());
        final List<String> siteNamesTest = siteDetectorUtil.getSiteNames();
        Assert.assertNotNull(siteNamesTest);
    }

    @Test
    public void setApproachTest() {
        siteDetectorUtil.setApproach(BearingConfiguration.Approach.FINGERPRINT);
        Mockito.verify(observationHandlerAndListenerMock, times(1)).setApproach(BearingConfiguration.Approach.FINGERPRINT);
    }

    @Test
    public void testMatchWithSnapshot() {
        siteDetectorUtil.matchWithSnapshot(new ArrayList<>());
        Mockito.verify(algorithmLifeCycleHandler, Mockito.times(1))
                .enqueue(Mockito.anyString(), Mockito.any(DetectionCalculationEvent.class),
                        Mockito.any(EventType.class), Mockito.anyString());
    }

    @Test
    public void testSetCurrentSite() {
        // test for geofence and set current site
        siteDetectorUtil.setCallback(siteDetectListenerMock);
        final List<SnapshotItem> list1 = new ArrayList<>();
        final List<SnapshotObservation> list2 = new ArrayList<>();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_GPS);
        final SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setCustomField(new String[]{"site|GEO_FENCE_ENTERED"});
        list1.add(snapshotItem);
        snapshotObservation.setSnapShotItemList(list1);
        list2.add(snapshotObservation);
        siteDetectorUtil.matchWithSnapshot(list2);
        Mockito.verify(siteDetectListenerMock, Mockito.times(1))
                .onSiteEntry(Mockito.any(UUID.class), Mockito.anyString(), Mockito.anyString());
    }

}
