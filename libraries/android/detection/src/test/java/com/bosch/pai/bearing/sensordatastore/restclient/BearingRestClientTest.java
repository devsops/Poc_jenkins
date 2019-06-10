package com.bosch.pai.bearing.sensordatastore.restclient;

import android.content.Context;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.persistence.StorageType;
import com.bosch.pai.comms.CommsManager;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingRESTClient.class, CommsManager.class, DataUploader.class, DataDownloader.class, DataTrimmer.class})
public class BearingRestClientTest {

    @Mock
    private CommsManager commsManager;
    @Mock
    private Context context;
    @Mock
    private BearingClientCallback callback;
    @Mock
    private BearingClientCallback.GetDataCallback getDataCallback;
    @Mock
    private DataUploader dataUploader;
    @Mock
    private DataDownloader dataDownloader;
    @Mock
    private DataTrimmer dataTrimmer;

    private BearingRESTClient bearingRestClient;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.whenNew(DataUploader.class).withArguments(Mockito.any(CommsManager.class))
                .thenReturn(dataUploader);
        PowerMockito.whenNew(DataDownloader.class).withArguments(Mockito.any(CommsManager.class))
                .thenReturn(dataDownloader);
        PowerMockito.whenNew(DataTrimmer.class).withArguments(Mockito.any(CommsManager.class))
                .thenReturn(dataTrimmer);
        BearingRESTClient.initComsWithContext(context);
        bearingRestClient = BearingRESTClient.getInstance();
    }

    @Test
    public void testInitAndGetInstance() {
        Assert.assertNotNull(bearingRestClient);
    }

    @Test
    public void testUploads() {
        final String SITE_NAME = "SITE_NAME";
        bearingRestClient.uploadSiteData(SITE_NAME, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .uploadSiteData(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.uploadLocationData(SITE_NAME, "LOC_NAME", BearingConfiguration.Approach.FINGERPRINT, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .uploadLocationData(Mockito.anyString(), Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class), Mockito.any(BearingClientCallback.class));
        bearingRestClient.uploadLocationsDataForSite(SITE_NAME, BearingConfiguration.Approach.FINGERPRINT, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .uploadLocationsForSite(Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class), Mockito.any(BearingClientCallback.class));
        bearingRestClient.uploadClassifierData(SITE_NAME, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .uploadClassifierData(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.generateSVMTOnServer(SITE_NAME, BearingConfiguration.Approach.FINGERPRINT, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .generateClassifierDataOnServer(Mockito.anyString(), Mockito.any(BearingConfiguration.Approach.class), Mockito.any(BearingClientCallback.class));
        bearingRestClient.renameSiteOnServer(SITE_NAME, SITE_NAME, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .renameSiteOnServer(Mockito.anyString(), Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.processDataOnServer(SITE_NAME, new ArrayList<SnapshotObservation>(), new ArrayList<BearingConfiguration.SensorType>(), callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .processDataOnServer(Mockito.anyString(), Mockito.anyListOf(SnapshotObservation.class), Mockito.anyListOf(BearingConfiguration.SensorType.class), Mockito.any(BearingClientCallback.class));
        bearingRestClient.uploadSiteThreshLocationsData(SITE_NAME, callback);
        Mockito.verify(dataUploader, Mockito.atLeastOnce())
                .uploadSiteThreshLocationsData(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
    }

    @Test
    public void testDownloads() {
        final String SITE_NAME = "SITE_NAME";
        bearingRestClient.downloadSiteData(SITE_NAME, callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .downloadSiteData(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.downloadLocationData(SITE_NAME, "LOC_NAME", callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .downloadLocationData(Mockito.anyString(), Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.downloadLocationsDataForSite(SITE_NAME, callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .downloadAllLocationDataForSiteFromServer(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.downloadClassifierData(SITE_NAME, true, callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .downloadClassifierData(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.getAllSiteNames(getDataCallback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .getAllSiteNames(Mockito.any(BearingClientCallback.GetDataCallback.class));
        bearingRestClient.getAllLocationNamesForSite(SITE_NAME, getDataCallback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .getAllLocationNames(Mockito.anyString(), Mockito.any(BearingClientCallback.GetDataCallback.class));
        bearingRestClient.getClusterData(SITE_NAME, callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .getClusterData(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
        bearingRestClient.downloadSourceIdMap(callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .downloadSourceIdMap(Mockito.any(BearingClientCallback.class));
        bearingRestClient.downloadSiteThreshData(SITE_NAME, callback);
        Mockito.verify(dataDownloader, Mockito.atLeastOnce())
                .downloadSourceIdMap(Mockito.any(BearingClientCallback.class));

    }

    @Test
    public void deleteSiteDataTest(){
        StorageType storageType = null;
        bearingRestClient.deleteSiteData("siteName", storageType, callback);
        Mockito.verify(dataTrimmer,Mockito.atLeastOnce()).deleteSiteData(Mockito.anyString(), Mockito.any(BearingClientCallback.class));
    }

    @Test
    public void deleteLocationDataTest(){
        StorageType storageType = null;
        bearingRestClient.deleteLocationData("siteName", "locationName", storageType, callback);
        Mockito.verify(dataTrimmer,Mockito.atLeastOnce()).deleteLocationData(Mockito.anyString(), Mockito.anyString(), Mockito.any(StorageType.class), Mockito.any(BearingClientCallback.class));
    }


}
