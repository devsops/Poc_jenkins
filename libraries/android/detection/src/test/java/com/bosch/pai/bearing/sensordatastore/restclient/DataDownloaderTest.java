package com.bosch.pai.bearing.sensordatastore.restclient;

import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.SiteDetectionConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SvmClassifierData;
import com.bosch.pai.bearing.entity.Classifier;
import com.bosch.pai.bearing.entity.ClassifierVersion;
import com.bosch.pai.bearing.entity.Location;
import com.bosch.pai.bearing.entity.Sensor;
import com.bosch.pai.bearing.entity.Site;
import com.bosch.pai.bearing.entity.SnapShotItem;
import com.bosch.pai.bearing.entity.ThreshData;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;
import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.tools.ant.taskdefs.condition.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.bosch.pai.*")
public class DataDownloaderTest {

    private final String SITE_NAME = "SITE_NAME";
    private final long DOC_VERSION = 1L;
    private final String schemaVersion = "1.0";
    private final int NO_OF_FLOOR = 1;
    private final List<Sensor> sensorList = new ArrayList<>();
    private long EPOCH_MILLI;
    private final boolean IS_ACTIVE = true;
    private final List<Location> locations = new ArrayList<>();

    @Mock
    private PersistenceHandler persistenceHandler;
    @Mock
    private CommsManager commsManager;
    @Mock
    private BearingClientCallback callback;
    @Mock
    private BearingClientCallback.GetDataCallback dataCallback;
    @Mock
    private ConfigurationSettings config;

    private DataDownloader dataDownloader;

    @Before
    public void init() throws Exception {
        final Sensor sensor = new Sensor();
        sensor.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        sensor.setDetectionLevel(BearingConfiguration.DetectionLevel.INTERMEDIATE);
        final SnapShotItem snapshotItem = new SnapShotItem();
        snapshotItem.setCustomFields(Collections.singletonList(""));
        snapshotItem.setMeasuredValues(Collections.singletonList(100.0));
        snapshotItem.setSourceId("00:00:00:00:00:00");
        sensor.setSnapShotItemList(Collections.singletonList(snapshotItem));
        sensorList.add(sensor);
        final String TEST_URL = "http://www.test.com/";
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments()
                .thenReturn(persistenceHandler);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(config);
        PowerMockito.when(config.getServerURL()).thenReturn(TEST_URL);
        dataDownloader = new DataDownloader(commsManager);
        EPOCH_MILLI = System.currentTimeMillis();
    }

    @Test
    public void testSetAndGetCrt() throws IOException {
        final String crtStr = "SAMPLE_STRING";
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(crtStr.getBytes());
        dataDownloader.setCertificateStream(crtStr);
        final InputStream inputStream = dataDownloader.getCertificate();
        Assert.assertNotNull(inputStream);
        Assert.assertEquals(byteArrayInputStream.available(), inputStream.available());
    }

    @Test
    public void testDownloadSiteData() throws Exception {
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataDownloader.downloadSiteData(SITE_NAME, callback);
        Mockito.verify(persistenceHandler, Mockito.atLeastOnce())
                .writeSnapShot(Mockito.anyString(), Mockito.any(Snapshot.class));
    }

    @Test
    public void testDownloadLocationData() throws Exception {
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
        final List<Location> locations = new ArrayList<>();
        final Location location = new Location();
        location.setLocationName("LOC_NAME");
        locations.add(location);
        PowerMockito.when(jsonArray.toString()).thenReturn(gson.toJson(locations));
        PowerMockito.when(persistenceHandler.writeLocationFingerPrintData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(PersistenceResult.RESULT_OK);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                responseObject.setResponseBody(gson.toJson(locations));
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        dataDownloader.downloadLocationData(SITE_NAME, "LOC_NAME", callback);
        Mockito.verify(persistenceHandler, Mockito.atLeastOnce())
                .writeLocationFingerPrintData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(callback, Mockito.atLeastOnce())
                .onRequestSuccess(Mockito.anyString());
    }

    @Test
    public void testDownloadLocationsDataFromNEWServer() throws Exception {
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
        final List<Location> locations = new ArrayList<>();
        final Location location = new Location();
        location.setLocationName("LOC_NAME");
        locations.add(location);
        PowerMockito.when(jsonArray.toString()).thenReturn(gson.toJson(locations));
        PowerMockito.when(persistenceHandler.writeLocationFingerPrintData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(PersistenceResult.RESULT_OK);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                responseObject.setResponseBody(gson.toJson(locations));
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        dataDownloader.downloadAllLocationDataForSiteFromServer(SITE_NAME, callback);
        Mockito.verify(persistenceHandler, Mockito.atLeastOnce())
                .writeLocationFingerPrintData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(callback, Mockito.atLeastOnce())
                .onRequestSuccess(Mockito.anyString());
    }

    @Test
    public void testDownloadClassifierData() throws Exception {
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ClassifierVersion classifierVersion = new ClassifierVersion();
                classifierVersion.setDocVersion(1);
                classifierVersion.setSchemaName("1.0");
                classifierVersion.setSchemaName("v1.0");
                classifierVersion.setSiteId(1L);
                final ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                responseObject.setResponseBody(gson.toJson(Collections.singletonList(classifierVersion)));
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final List<String> locationNames = new ArrayList<>();
                final Classifier classifier = new Classifier();
                classifier.setActive(true);
                classifier.setContentData("SAMPLE_DATA");
                classifier.setCreatedAt(1L);
                classifier.setUpdatedAt(1L);
                classifier.setLocationNames(locationNames);
                final ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                responseObject.setResponseBody(gson.toJson(classifier));
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        dataDownloader.downloadClassifierData(SITE_NAME, true, callback);
        Mockito.verify(persistenceHandler, Mockito.atLeastOnce())
                .writeClassifiers(Mockito.anyString(), Mockito.any(SvmClassifierData.class));
        Mockito.verify(callback, Mockito.atLeastOnce())
                .onRequestSuccess(Mockito.anyString());
    }

    @Test
    public void getAllSiteNamesTest() throws Exception{
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataDownloader.getAllSiteNames(dataCallback);
        Mockito.verify(dataCallback,Mockito.atLeastOnce()).onDataReceived(Mockito.anyList());
    }

    @Test
    public void getAllLocationNamesTest() throws Exception{
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataDownloader.getAllLocationNames(SITE_NAME, dataCallback);
        Mockito.verify(dataCallback,Mockito.atLeastOnce()).onDataReceived(Mockito.anyList());
    }

    @Test
    public void getClusterDataTest() throws Exception{
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        final Site[] sites = new Site[]{site};
                        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                }).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(HttpURLConnection.HTTP_BAD_GATEWAY);
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        dataDownloader.getClusterData(SITE_NAME, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestFailure(Mockito.anyString());
    }

    @Test
    public void downloadSourceIdMapTest(){
        final Gson gson = new GsonBuilder().create();
        final SiteDetectionConfiguration siteDetectionConfiguration = new SiteDetectionConfiguration("siteName", 1, 1, 1l);
        final Set<SiteDetectionConfiguration> siteDetectionConfigurations = new HashSet<>();
        siteDetectionConfigurations.add(siteDetectionConfiguration);
        final Map<String, Set<SiteDetectionConfiguration>> sourceIdMap = new HashMap<>();
        sourceIdMap.put("key",siteDetectionConfigurations);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        responseObject.setResponseBody(gson.toJson(sourceIdMap));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataDownloader.downloadSourceIdMap(callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestFailure(Mockito.anyString());
    }

    @Test
    public void downloadSiteThreshDataTest(){
        final Gson gson = new GsonBuilder().create();
        final ThreshData threshData = new ThreshData();
        threshData.setLocationName("locationName");
        threshData.setSiteName("siteName");
        threshData.setThreshContent("threshContent");
        threshData.setThreshId(100);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_OK);
                        responseObject.setResponseBody(gson.toJson(threshData));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataDownloader.downloadSiteThreshData(SITE_NAME, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestSuccess(Mockito.anyString());
    }
}
