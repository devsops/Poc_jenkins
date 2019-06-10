package com.bosch.pai.ipsadmin.bearing.core.operation.detection.location;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.algorithm.event.DetectionCalculationEvent;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SvmClassifierData;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;
import com.bosch.pai.ipsadmin.bearing.benchmark.bearinglogger.profiling.ResourceProfiler;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClusterDataDownloader.class, BearingHandler.class, AlgorithmLifeCycleHandler.class,
        LocationDetectorUtil.class, ResourceProfiler.class, ObservationHandlerAndListener.class,PersistenceHandler.class})
public class LocationDetectorUtilTest {

    @Mock
    PersistenceHandler persistenceHandler;

    LocationDetectListener locationDetectListener;

    Set<String> siteNames;
    List<String> siteList;

    @Mock
    SvmClassifierData svmClassifierData;

    @Mock
    private ClusterDataDownloader clusterDataDownloader;

    @Mock
    private ResourceProfiler resourceProfiler;
    @Mock
    private ObservationHandlerAndListener observationHandlerAndListener;
    @Mock
    private AlgorithmLifeCycleHandler algorithmLifeCycleHandler;

    private LocationDetectorUtil locationDetectorUtil;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(ClusterDataDownloader.class);
        PowerMockito.mockStatic(BearingHandler.class);
        PowerMockito.mockStatic(AlgorithmLifeCycleHandler.class);
        //
        siteNames = new HashSet<>();
        siteNames.add("DemoSite");

        siteList = new ArrayList<String>();
        siteList.add("DemoSite");
        PowerMockito.whenNew(PersistenceHandler.class).withArguments(Matchers.any(DataStore.StoreType.class)).thenReturn(persistenceHandler);
        PowerMockito.when(persistenceHandler.writeClassifiers(anyString(),Mockito.any(SvmClassifierData.class))).thenReturn(PersistenceResult.RESULT_OK);
        //
        PowerMockito.whenNew(ResourceProfiler.class).withNoArguments().thenReturn(resourceProfiler);
        PowerMockito.whenNew(ObservationHandlerAndListener.class).withNoArguments().thenReturn(observationHandlerAndListener);
        when(observationHandlerAndListener.addObservationSource(Mockito.any(UUID.class),
                Mockito.anyListOf(BearingConfiguration.SensorType.class), Mockito.anyBoolean())).thenReturn(true);
        when(AlgorithmLifeCycleHandler.getInstance()).thenReturn(algorithmLifeCycleHandler);
        PowerMockito.doNothing().when(algorithmLifeCycleHandler, "enqueue", anyString(), Mockito.any(DetectionCalculationEvent.class),
                Mockito.any(EventType.class), anyString());
        locationDetectorUtil = new LocationDetectorUtil();
    }

    @Test
    public void testSetCurrentSite() {
        locationDetectorUtil.setCurrentSite("SITE_NAME");
        PowerMockito.verifyStatic(Mockito.times(1));
        ClusterDataDownloader.validateClusterDataAndDownload(anyString());
    }

    @Test
    public void testStartLocationDetection() {
        final BearingConfiguration.Approach approach = BearingConfiguration.Approach.DATA_CAPTURE;
        locationDetectorUtil.startLocationDetection(approach, UUID.randomUUID(), new ArrayList<BearingConfiguration.SensorType>());
        verify(resourceProfiler, Mockito.times(1)).writeDeviceInfo(Mockito.any());
        verify(observationHandlerAndListener, Mockito.times(1))
                .addObservationSource(Mockito.any(UUID.class),
                        Mockito.anyListOf(BearingConfiguration.SensorType.class), Mockito.anyBoolean());
        PowerMockito.verifyStatic(Mockito.times(1));
        BearingHandler.addRequestToRequestDataHolderMap(Mockito.any(RequestDataHolder.class));
    }

    @Test
    public void testRegisterSensorObservationListener() {
        locationDetectorUtil.registerSensorObservationListener();
        verify(observationHandlerAndListener, Mockito.times(1))
                .registerSensorObservationListener();
    }

    @Test
    public void testDetectLocation() {
        locationDetectorUtil.detectLocation(new ArrayList<>(), new ArrayList<>(), BearingConfiguration.Approach.FINGERPRINT);
        locationDetectorUtil.detectLocation(new ArrayList<>(), new ArrayList<>(), BearingConfiguration.Approach.THRESHOLDING);
        verify(algorithmLifeCycleHandler, Mockito.times(2))
                .enqueue(anyString(), Mockito.any(DetectionCalculationEvent.class),
                        Mockito.any(EventType.class), anyString());

    }

    @Test
    public void testSetApproach() {
        locationDetectorUtil.setApproach(BearingConfiguration.Approach.FINGERPRINT);
        verify(observationHandlerAndListener, Mockito.times(1))
                .setApproach(Mockito.any(BearingConfiguration.Approach.class));
    }

    @Test
    public void testGetLocationNames() {
        when(persistenceHandler.getLocationNames(Mockito.anyString(),Mockito.any(BearingConfiguration.Approach.class))).thenReturn(siteNames);
        List<String> siteList = locationDetectorUtil.getLocationNames("DemoSite",BearingConfiguration.Approach.DATA_CAPTURE);
        verify(persistenceHandler,Mockito.times(1)).getLocationNames(Mockito.anyString(),Mockito.any(BearingConfiguration.Approach.class));
        assertEquals("DemoSite",siteList.get(0));
    }

    @Test
    public void testGetLocationNamesFromClusterData() {
        when(persistenceHandler.getLocationNamesFromClassifier(Mockito.anyString())).thenReturn(siteList);
        List<String> siteList = locationDetectorUtil.getLocationNamesFromClusterData("DemoSite");
        verify(persistenceHandler,Mockito.times(1)).getLocationNamesFromClassifier(Mockito.anyString());
        assertEquals("DemoSite",siteList.get(0));
    }

    @Test
    public void testWriteClassifierData()
    {
        boolean persistenceResultStatus = locationDetectorUtil.writeClassifierData("DemoSite",svmClassifierData);
        assertTrue(persistenceResultStatus);
    }


    @Test
    public void testLocationListener()
    {
        locationDetectListener = new LocationDetectListener() {
            @Override
            public void onErrorDetectingLocation(UUID uuid, String errMessage) {

            }

            @Override
            public void onLocationUpdate(UUID uuid, BearingConfiguration.Approach approach, String siteName, Map<String, Double> locationToProbabilityMap, String localTime) {

            }
        };
        locationDetectorUtil.setLocationListenerCallback(locationDetectListener);
        assertEquals(locationDetectListener,locationDetectorUtil.getLocationListenerCallback());
     //   boolean persistenceResultStatus = locationDetectorUtil.writeClassifierData("DemoSite",svmClassifierData);
      //  assertTrue(persistenceResultStatus);
    }

    @Test
    public void setTransactionIDTest(){
        locationDetectorUtil.setTransactionID(UUID.randomUUID());
        locationDetectorUtil.setObservationDataType(RequestDataHolder.ObservationDataType.SITE_DETECTION);
    }

    @Test
    public void getCurrentSiteTest(){
        Assert.assertNotNull(locationDetectorUtil.getCurrentSite());
    }

}
