package com.bosch.pai.ipsadmin.bearing.core.operation.readoperations;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestRetrieveEvent;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestReadEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.location.LocationTrainerUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingRESTClient;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingOperations.class, BearingRESTClient.class, ConfigurationSettings.class,BearingOperationCallback.class,
        LogAndToastUtil.class})
public class BearingOperationsTest {

    @Mock
    private BearingRESTClient bearingRESTClient;
    @Mock
    private SiteDetectorUtil siteDetectorUtil;
    @Mock
    private LocationDetectorUtil locationDetectorUtil;
    @Mock
    private SiteTrainUtil siteTrainUtil;
    @Mock
    private LocationTrainerUtil locationTrainerUtil;
    @Mock
    private SynchronousServerCalls synchronousServerCalls;
    @Mock
    private UploadOperations uploadOperations;
    @Mock
    private ConfigurationSettings configurationSettings;

    @Mock
    private BearingOperationCallback bearingOperationCallback;

    /*@Mock
    BearingResponseAggregator bearingResponseAggregator;*/

    RequestReadEvent requestReadEvent;
    String requestId;
    EventType eventType;
    Sender sender;

    private BearingOperations bearingOperations;
    private final List<String> siteNames = new ArrayList<>();
    private final List<String> locNames1 = new ArrayList<>();
    private final List<String> locNames2 = new ArrayList<>();

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(BearingRESTClient.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        when(BearingRESTClient.getInstance()).thenReturn(bearingRESTClient);
        when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        when(configurationSettings.withServerURL(Mockito.anyString())).thenReturn(configurationSettings);
        PowerMockito.whenNew(SiteDetectorUtil.class).withNoArguments().thenReturn(siteDetectorUtil);
        when(siteDetectorUtil.getSiteNames()).thenReturn(siteNames);
        PowerMockito.whenNew(LocationDetectorUtil.class).withNoArguments().thenReturn(locationDetectorUtil);
        when(locationDetectorUtil.getLocationNames(Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class)))
                .thenReturn(locNames1);
        when(locationDetectorUtil.getLocationNamesFromClusterData(Mockito.anyString()))
                .thenReturn(locNames2);
        PowerMockito.whenNew(SiteTrainUtil.class).withNoArguments().thenReturn(siteTrainUtil);
        final Set<String> stringSet = new HashSet<>(siteNames);
        when(siteTrainUtil.getSiteNames()).thenReturn(stringSet);
        PowerMockito.whenNew(LocationTrainerUtil.class).withNoArguments().thenReturn(locationTrainerUtil);
        when(locationTrainerUtil.getLocationNames(Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class)))
                .thenReturn(locNames2);
        PowerMockito.whenNew(SynchronousServerCalls.class)
                .withArguments(Mockito.any(BearingRESTClient.class), Mockito.any(SiteDetectorUtil.class), Mockito.any(LocationDetectorUtil.class))
                .thenReturn(synchronousServerCalls);
        when(synchronousServerCalls.getAllSitesSyncWithServer(Mockito.anyBoolean()))
                .thenReturn(siteNames);
        when(synchronousServerCalls.getAllLocationsSyncWithServer(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(locNames1);
        PowerMockito.whenNew(UploadOperations.class).withArguments(Mockito.any(BearingRESTClient.class))
                .thenReturn(uploadOperations);
        //
        requestId = UUID.randomUUID().toString();
        eventType = EventType.TRIGGER_TRAINING;
        sender = new Sender() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        };
        requestReadEvent = new RequestReadEvent(requestId,eventType,sender);
        requestReadEvent.setFetchRequest(RequestReadEvent.ServerFetch.ALL_SITES_LOCAL);

        this.siteNames.add("SITE_NAME");

        this.locNames2.add("LOC1");
        this.locNames2.add("LOC2");
        this.locNames1.add("LOC1");
        this.locNames1.add("LOC2");
        this.bearingOperations = new BearingOperations();
    }

    @Test
    public void testRegisterListener() {
        this.bearingOperations.registerListener(bearingOperationCallback);
        verify(uploadOperations, Mockito.times(1))
                .registerBearingOperationCallback(Mockito.any(BearingOperationCallback.class));
    }

    @Test
    public void setTransactionIdTest(){
        this.bearingOperations.setTransactionId("transactionId");
    }

    @Test
    public void testSetServerUrlEndpoint() {
        final String SERVER_URL = "http://www.TestURL.com/";
        this.bearingOperations.setServerUrlEndpoint(SERVER_URL, null);
        PowerMockito.verifyStatic(Mockito.times(1));
        ConfigurationSettings.saveConfigObject(configurationSettings);
    }

    @Test
    public void testReadSynchronousDataWithoutSyncWithServer() {
        final SiteMetaData siteMetaData = new SiteMetaData("SITE_NAME");
        final BearingData bearingData = new BearingData(siteMetaData);
        final List<String> strings1 = this.bearingOperations.readSynchronousDataWithoutSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_SITE_LIST);
        Assert.assertTrue(siteNames.containsAll(strings1));
        final List<String> strings2 = this.bearingOperations.readSynchronousDataWithoutSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_LOC_LIST);
        Assert.assertTrue(locNames2.containsAll(strings2));
    }

    @Test
    public void testReadSynchronousDataAfterSyncWithServer() {
        final SiteMetaData siteMetaData = new SiteMetaData("SITE_NAME");
        final BearingData bearingData = new BearingData(siteMetaData);
        final List<String> strings1 = this.bearingOperations.readSynchronousDataAfterSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_SITE_LIST);
        Assert.assertTrue(siteNames.containsAll(strings1));
        final List<String> strings2 = this.bearingOperations.readSynchronousDataAfterSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_LOC_LIST);
        Assert.assertTrue(locNames2.containsAll(strings2));
        final List<String> strings3 = this.bearingOperations.readSynchronousDataAfterSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_SITE_LIST_ON_SERVER);
        Assert.assertTrue(siteNames.containsAll(strings3));
        final List<String> strings4 = this.bearingOperations.readSynchronousDataAfterSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_LOC_LIST_ON_SERVER);
        Assert.assertTrue(locNames2.containsAll(strings4));

    }

    @Test
    public void testRetrieveSynchronousDataAfterSyncWithServer() {
        final SiteMetaData siteMetaData = new SiteMetaData("SITE_NAME");
        final BearingData bearingData = new BearingData(siteMetaData);
        final BearingOperations.Response response1 = this.bearingOperations.retrieveSynchronousDataAfterSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_SITE_LIST, false);
        Assert.assertTrue(BearingConfiguration.OperationType.READ_SITE_LIST == response1.getOperationType());
        Assert.assertTrue(siteNames.containsAll(response1.getStringsList()));

        final BearingOperations.Response response2 = this.bearingOperations.retrieveSynchronousDataAfterSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_LOC_LIST, false);
        Assert.assertTrue(BearingConfiguration.OperationType.READ_LOC_LIST == response2.getOperationType());
        Assert.assertTrue(locNames2.containsAll(response2.getStringsList()));
    }

    @Test
    public void testRetrieveSynchronousDataWithoutSyncWithServer() {
        final SiteMetaData siteMetaData = new SiteMetaData("SITE_NAME");
        final BearingData bearingData = new BearingData(siteMetaData);
        final BearingOperations.Response response1 = this.bearingOperations.retrieveSynchronousDataWithoutSyncWithServer
                (bearingData, BearingConfiguration.OperationType.READ_SITE_LIST);
        Assert.assertTrue(BearingConfiguration.OperationType.READ_SITE_LIST == response1.getOperationType());
        Assert.assertTrue(siteNames.containsAll(response1.getStringsList()));
    }

    @Test
    public void testreadDataWithAsynchronousResponse() throws Exception {
        PowerMockito.spy(BearingOperationCallback.class);
        bearingOperations.registerListener(bearingOperationCallback);
        PowerMockito.doNothing().when(bearingOperationCallback).onDataReceived(UUID.fromString(requestId),siteNames);
        bearingOperations.readDataWithAsynchronousResponse(requestReadEvent);
        verify(siteDetectorUtil,Mockito.times(1)).getSiteNames();
    }

    @Test
    public void updateSynchronousDataAfterSyncWithServerTest(){
        final SiteMetaData siteMetaDataOld = new SiteMetaData("SITE_NAME");
        final SiteMetaData siteMetaDataNew = new SiteMetaData("SITE_NAME_NEW");
        final BearingData bearingDataOld = new BearingData(siteMetaDataOld);
        final BearingData bearingDataNew = new BearingData(siteMetaDataNew);
        Assert.assertFalse(this.bearingOperations.updateSynchronousDataAfterSyncWithServer(bearingDataOld, bearingDataNew, BearingConfiguration.OperationType.SITE_RENAME));
        Assert.assertFalse(this.bearingOperations.updateSynchronousDataAfterSyncWithServer(bearingDataOld, bearingDataNew, BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE));
        Assert.assertFalse(this.bearingOperations.updateSynchronousDataAfterSyncWithServer(bearingDataOld, bearingDataNew, BearingConfiguration.OperationType.READ_LOC_LIST));
    }
}
