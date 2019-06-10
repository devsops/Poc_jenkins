//package com.bosch.pai.ipsadmin.bearing.core.operation;
//
//import com.bosch.pai.bearing.datamodel.BearingOutput;
//import com.bosch.pai.bearing.core.BearingCallBack;
//import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
//import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
//import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
//import com.bosch.pai.bearing.enums.BearingMode;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.atLeastOnce;
//import static org.mockito.Mockito.times;
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({BearingResponseAggregator.class})
//public class BearingResponseAggregatorTest {
//
//    private BearingResponseAggregator bearingResponseAggregator;
//    @Mock
//    private BearingCallBack bearingCallBack;
//
//    Map<String,Double> map;
//
//    private UUID uuid;
//    private BearingConfiguration.Approach approach;
//    private String siteName;
//    private String locationName;
//    private String errorMessage;
//    private String localTime;
//    List<SnapshotObservation> snapshotObservations;
//    List<String> dataList;
//
//    @Before
//    public void init() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        initMockEnvironment();
//        bearingResponseAggregator = BearingResponseAggregator.getInstance();
//    }
//
//    private void initMockEnvironment() throws Exception {
//        uuid = UUID.randomUUID();
//        approach = BearingConfiguration.Approach.DATA_CAPTURE;
//        siteName = "DemoSite";
//        errorMessage = "errorMsg";
//        locationName = "DemoLocation";
//        localTime = "DemoTime";
//        snapshotObservations = new ArrayList<SnapshotObservation>();
//        map = new HashMap<String, Double>();
//        dataList = new ArrayList<String>();
//        BearingResponseAggregator mockBearingResponseAggregator = PowerMockito.mock(BearingResponseAggregator.class);
//        PowerMockito.whenNew(BearingResponseAggregator.class).withNoArguments().thenReturn(mockBearingResponseAggregator);
//    }
//
//    @Test
//    public void getTrainingCallbackMapTestNotNull() {
//        final Map<String, BearingCallBack> trainingCallbackMap = bearingResponseAggregator.getTrainingCallbackMap();
//        Assert.assertNotNull(trainingCallbackMap);
//    }
//
//    @Test
//    public void getTrainingCallbackMapIsEmpty() {
//        Map<String, BearingCallBack> testCallbackMap = new HashMap<>();
//        final Map<String, BearingCallBack> trainingCallbackMap = bearingResponseAggregator.getTrainingCallbackMap();
//        Assert.assertTrue(testCallbackMap.size() == trainingCallbackMap.size());
//    }
//
//   /* @Test
//    public void testOnTrainError() throws Exception {
//        bearingResponseAggregator.onTrainError(uuid,approach,siteName,errorMessage);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainError",any(StatusCode.class),any(BearingMode.class),anyString());
//    }*/
//
//    @Test
//    public void testOnTrainSuccess() throws Exception {
//        bearingResponseAggregator.onTrainSuccess(uuid,approach,siteName);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainSuccess",any(StatusCode.class),any(BearingMode.class));
//    }
//
//    @Test
//    public void testOnSignalMerge() throws Exception {
//        bearingResponseAggregator.onSignalMerge(uuid,approach,siteName,snapshotObservations);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainSuccess",any(StatusCode.class),any(BearingMode.class));
//    }
//
//    /*@Test
//    public void testOnDataRecordProgress() throws Exception {
//        bearingResponseAggregator.onDataRecordProgress(uuid,approach,1);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainProgress",anyInt(),any(StatusCode.class),any(BearingMode.class));
//    }*/
//
//    @Test
//    public void testOnLocationsTrained() throws Exception {
//        bearingResponseAggregator.onLocationsTrained(uuid,approach,siteName);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainSuccess",any(StatusCode.class),any(BearingMode.class));
//    }
//
//    @Test
//    public void testOnLocationTrainError() throws Exception {
//        bearingResponseAggregator.onLocationTrainError(uuid,approach,errorMessage);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainError",any(StatusCode.class),any(BearingMode.class),anyString());
//    }
//
//    @Test
//    public void testOnDataRecordError() throws Exception {
//        bearingResponseAggregator.onDataRecordError(uuid,approach,errorMessage);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainError",any(StatusCode.class),any(BearingMode.class),anyString());
//    }
//
//    @Test
//    public void testOnDataPersistError() throws Exception {
//        bearingResponseAggregator.onDataPersistError(uuid,approach,errorMessage);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainError",any(StatusCode.class),any(BearingMode.class),anyString());
//    }
//
//    @Test
//    public void testonDataPersistSuccess() throws Exception {
//        bearingResponseAggregator.onDataPersistSuccess(uuid,approach,errorMessage);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnTrainSuccess",any(StatusCode.class),any(BearingMode.class));
//    }
//
//    @Test
//    public void testOnSiteEntry() throws Exception {
//        bearingResponseAggregator.onSiteEntry(uuid,localTime,siteName);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnSiteDetect",anyString(),anyString());
//    }
//
//    @Test
//    public void testOnSiteExit() throws Exception {
//        bearingResponseAggregator.onSiteExit(uuid,localTime,siteName);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnSiteDetect",anyString(),anyString());
//    }
//
//    @Test
//    public void testUpdateReadResponseAggregatorMap() throws Exception {
//        bearingResponseAggregator.updateReadResponseAggregatorMap(uuid,bearingCallBack);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("updateReadResponseAggregatorMap",uuid,bearingCallBack);
//    }
//
//    @Test
//    public void testonDataReceived() throws Exception {
//        bearingResponseAggregator.onDataReceived(uuid,dataList);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnReadOperation",dataList);
//    }
//
//    @Test
//    public void testOnDataReceivedError() throws Exception {
//        bearingResponseAggregator.onDataReceivedError(uuid,errorMessage);
//        PowerMockito.verifyPrivate(bearingResponseAggregator,times(1)).invoke("bearingOutputOnError",any(StatusCode.class),anyString());
//    }
//
//}
