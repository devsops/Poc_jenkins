package com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient;

import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.entity.Location;
import com.bosch.pai.bearing.entity.Sensor;
import com.bosch.pai.bearing.entity.Site;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.bosch.pai.*")
public class DataUploaderTest {
    @Mock
    private PersistenceHandler persistenceHandler;
    @Mock
    private CommsManager commsManager;
    @Mock
    private BearingClientCallback callback;
    @Mock
    private ConfigurationSettings config;

    private final String SITE_NAME = "SITE_NAME";
    private DataUploader dataUploader;

    private final long DOC_VERSION = 1L;
    private final String schemaVersion = "1.0";
    private final int NO_OF_FLOOR = 1;
    private final List<Sensor> sensorList = new ArrayList<>();
    private long EPOCH_MILLI;
    private final boolean IS_ACTIVE = true;
    private final List<Location> locations = new ArrayList<>();

    private List<SnapshotObservation> snapshotObservationList = new ArrayList<>();
    private List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
    private Set<String> locationNames = new HashSet<>();
    private List<SnapshotObservation> locationMap = new ArrayList<>();

    @Before
    public void init() throws Exception {
        final String TEST_URL = "http://www.test.com/";
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments()
                .thenReturn(persistenceHandler);
        locationNames.add("LOC1");
        locationNames.add("LOC2");
        PowerMockito.when(persistenceHandler.getLocationNames(Mockito.anyString(),Mockito.any(BearingConfiguration.Approach.class)))
                .thenReturn(locationNames);
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotItem> list1 = new ArrayList<>();
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.MACRO);
        final SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setCustomField(new String[]{"site|GEO_FENCE_ENTERED"});
        list1.add(snapshotItem);
        snapshotObservation.setSnapShotItemList(list1);
        locationMap.add(snapshotObservation);
        PowerMockito.when(persistenceHandler.readLocationThreshData(Mockito.anyString(), Mockito.anyString())).thenReturn(locationMap);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(config);
        PowerMockito.when(config.getServerURL()).thenReturn(TEST_URL);
        dataUploader = new DataUploader(commsManager);
    }

    @Test
    public void testSetAndGetCrt() throws IOException {
        final String crtStr = "SAMPLE_STRING";
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(crtStr.getBytes());
        dataUploader.setCertificateStream(crtStr);
        final InputStream inputStream = dataUploader.getCertificate();
        Assert.assertNotNull(inputStream);
        Assert.assertEquals(byteArrayInputStream.available(), inputStream.available());
    }

    @Test
    public void testUploadSiteData() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setSensors(new ArrayList<>());
        PowerMockito.when(persistenceHandler.readSnapShot(Mockito.anyString())).thenReturn(snapshot);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_CREATED);
                        responseObject.setResponseBody("SUCCESS");
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });

        dataUploader.uploadSiteData(SITE_NAME, callback);
        Mockito.verify(callback, Mockito.atLeastOnce())
                .onRequestSuccess(Mockito.anyString());
    }

    @Test
    public void testUploadSiteDataWithConflict() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setSensors(new ArrayList<>());
        PowerMockito.when(persistenceHandler.readSnapShot(Mockito.anyString())).thenReturn(snapshot);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(HttpURLConnection.HTTP_CONFLICT);
                        responseObject.setResponseBody("CONFLICT");
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataUploader.uploadSiteData(SITE_NAME, callback);
        Mockito.verify(commsManager, Mockito.times(2))
                .processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
    }

    @Test
    public void uploadLocationDataTest() throws Exception{
        final Gson gson = new GsonBuilder().create();
        final Site site = new Site();
        site.setSiteId(1L);
        site.setSiteName(SITE_NAME);
        site.setActive(IS_ACTIVE);
        site.setDocVersion(DOC_VERSION);
        site.setEpochMilliSeconds(EPOCH_MILLI);
        final Location location = new Location();
        location.setLocationId(1L);
        location.setSubSiteId(1L);
        location.setSensorId(1L);
        location.setLocationName("location");
        locations.add(location);
        site.setLocations(locations);
        site.setNoOfFloors(NO_OF_FLOOR);
        site.setSchemaVersion(schemaVersion);
        site.setSensors(sensorList);
        final JSONArray jsonArray = PowerMockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(jsonArray);
        PowerMockito.when(jsonArray.length()).thenReturn(1);
        final JSONObject jsonObject = PowerMockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(jsonObject);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(200);
                        final Site[] sites = new Site[]{site};
                        PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(site));
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(HttpURLConnection.HTTP_CONFLICT);
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ResponseObject responseObject = new ResponseObject();
                final Location location = new Location();
                location.setLocationId(1L);
                location.setSubSiteId(1L);
                location.setSensorId(1L);
                location.setLocationName("location");
                PowerMockito.when(jsonArray.get(Mockito.anyInt())).thenReturn(gson.toJson(location));
                responseObject.setResponseBody(gson.toJson(location));
                ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        dataUploader.uploadLocationData(SITE_NAME, "location", BearingConfiguration.Approach.THRESHOLDING, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestFailure(Mockito.anyString());
    }

    @Test
    public void uploadLocationsForSiteTest() throws Exception{
        final Location location = new Location();
        location.setLocationName("LOC_NAME");
        locations.add(location);
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
        final JSONObject jsonObject = PowerMockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(jsonObject);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(200);
                        final Site[] sites = new Site[]{site};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataUploader.uploadLocationsForSite(SITE_NAME, BearingConfiguration.Approach.THRESHOLDING, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestFailure(Mockito.anyString());
    }

    @Test
    public void uploadClassifierDataTest(){
        dataUploader.uploadClassifierData(SITE_NAME, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestFailure(Mockito.anyString());
    }

    @Test
    public void generateClassifierDataOnServerTest() throws Exception{
        final Location location = new Location();
        location.setLocationName("LOC_NAME");
        locations.add(location);
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
        final JSONObject jsonObject = PowerMockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(jsonObject);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(200);
                        final Site[] sites = new Site[]{site};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataUploader.generateClassifierDataOnServer(SITE_NAME, BearingConfiguration.Approach.THRESHOLDING, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestSuccess(Mockito.anyString());

        dataUploader.renameSiteOnServer(SITE_NAME,"new", callback);
    }

    @Test
    public void processDataOnServerTest() throws Exception{
        final Gson gson = new GsonBuilder().create();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotItem> list1 = new ArrayList<>();
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.MACRO);
        final SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setCustomField(new String[]{"site|GEO_FENCE_ENTERED"});
        list1.add(snapshotItem);
        snapshotObservation.setSnapShotItemList(list1);
        snapshotObservationList.add(snapshotObservation);
        sensorTypes.add(BearingConfiguration.SensorType.ST_BLE);
        final JSONObject jsonObject = PowerMockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(jsonObject);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(200);
                        final Site[] sites = new Site[]{};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataUploader.processDataOnServer(SITE_NAME, snapshotObservationList, sensorTypes, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestSuccess(Mockito.anyString());
    }

    @Test
    public void uploadSiteThreshLocationsDataTest(){
        final Gson gson = new GsonBuilder().create();
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final ResponseObject responseObject = new ResponseObject();
                        responseObject.setStatusCode(200);
                        final Site[] sites = new Site[]{};
                        responseObject.setResponseBody(gson.toJson(sites));
                        ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
                        return null;
                    }
                });
        dataUploader.uploadSiteThreshLocationsData(SITE_NAME, callback);
        Mockito.verify(callback,Mockito.atLeastOnce()).onRequestSuccess(Mockito.anyString());
    }

}
