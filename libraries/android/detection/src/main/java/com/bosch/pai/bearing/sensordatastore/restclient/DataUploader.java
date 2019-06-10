package com.bosch.pai.bearing.sensordatastore.restclient;


import android.support.annotation.NonNull;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SvmClassifierData;
import com.bosch.pai.bearing.entity.Location;
import com.bosch.pai.bearing.entity.Site;
import com.bosch.pai.bearing.entity.ThreshData;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.bearing.sensordatastore.util.ClientUtil;
import com.bosch.pai.bearing.util.SnapshotItemManager;
import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * The type Data uploader.
 */
final class DataUploader {

    private static final String TAG = DataUploader.class.getName();
    private final PersistenceHandler persistenceHandler;
    private final CommsManager commsManager;
    private final Gson gson;
    private String certificateInputStreamString;

    /**
     * Instantiates a new Data uploader.
     *
     * @param commsManager the comms manager
     */
    DataUploader(@NonNull CommsManager commsManager) {
        this.persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        this.commsManager = commsManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new GsonUTCAdapter())
                .create();
    }

    /**
     * Sets certificate stream.
     *
     * @param certificateInputStreamString the certificate input stream string
     */
    void setCertificateStream(String certificateInputStreamString) {
        this.certificateInputStreamString = certificateInputStreamString;
    }


    /**
     * Gets certificate.
     *
     * @return the certificate
     */
    synchronized InputStream getCertificate() {

        if (this.certificateInputStreamString != null) {
            return new ByteArrayInputStream(this.certificateInputStreamString.getBytes());
        } else {
            return null;
        }
    }

    /**
     * Upload site data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    void uploadSiteData(@NonNull final String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final Snapshot snapshotForSite = persistenceHandler.readSnapShot(siteName);
        if (snapshotForSite == null || snapshotForSite.getSensors() == null) {
            bearingClientCallback.onRequestFailure(BearingClientCallback.Status.INVALID_DATA_FOR_SITE_PERSIST.toString());
            return;
        }
        final Site site = ClientUtil.createServerSiteFromTrainedSiteData(siteName, snapshotForSite);

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/");
        requestObject.setMessageBody(gson.toJson(site));
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_CONFLICT) {
                    updateExistingSiteData(siteName, requestObject, bearingClientCallback);
                } else if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_CREATED) {
                    bearingClientCallback.onRequestSuccess(responseObject.getResponseBody().toString());
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure(s);
            }
        });
    }

    private void updateExistingSiteData(final String siteName, final RequestObject siteRequestObject, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/search/");
        final Map<String, String> queryParamsMap = new HashMap<>();
        queryParamsMap.put("name", siteName);
        requestObject.setQueryParams(queryParamsMap);
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    Long id = -1L;
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final Site site = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            if (siteName.equals(site.getSiteName())) {
                                id = site.getSiteId();
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "", e);
                        bearingClientCallback.onRequestFailure("Err parsing data for site::" + siteName);
                    }
                    final RequestObject updateRequestObject = new RequestObject(RequestObject.RequestType.PUT,
                            ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/");
                    updateRequestObject.setMessageBody(siteRequestObject.getMessageBody());
                    updateRequestObject.setNonBezirkRequest(true);
                    updateRequestObject.setCertFileStream(getCertificate());
                    commsManager.processRequest(updateRequestObject, new CommsListener() {
                        @Override
                        public void onResponse(ResponseObject responseObject) {
                            if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                                bearingClientCallback.onRequestSuccess(responseObject.getResponseBody().toString());
                            } else {
                                bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            bearingClientCallback.onRequestFailure(s);
                        }
                    });
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure(s);
            }
        });
    }

    /**
     * Upload location data.
     *
     * @param siteName              the site name
     * @param locationName          the location name
     * @param approach              the approach
     * @param bearingClientCallback the bearing client callback
     */
    void uploadLocationData(@NonNull final String siteName, @NonNull final String locationName, @NonNull final BearingConfiguration.Approach approach, @NonNull final BearingClientCallback bearingClientCallback) {

        if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
            final List<SnapshotObservation> locationMap = persistenceHandler.readLocationThreshData(siteName, locationName);
            if (locationMap == null || locationMap.isEmpty()) {
                bearingClientCallback.onRequestFailure(BearingClientCallback.Status.INVALID_DATA_FOR_LOCATION_PERSIST.toString());
                return;
            }

        } else {
            final Map<String, double[]> locationMap = persistenceHandler.readLocationFingerPrintData(siteName, locationName);
            if (locationMap == null || locationMap.isEmpty()) {
                bearingClientCallback.onRequestFailure(BearingClientCallback.Status.INVALID_DATA_FOR_LOCATION_PERSIST.toString());
                return;
            }

        }

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    Site requiredSite = null;
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final Site site = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            if (siteName.equals(site.getSiteName())) {
                                requiredSite = site;
                                break;
                            }
                        }
                        createLocationDataSpaceForSite(requiredSite, locationName, approach, bearingClientCallback);
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err: ", e);
                        bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                    }
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, s);
                bearingClientCallback.onRequestFailure(s);
            }
        });
    }

    private void createLocationDataSpaceForSite(final Site requiredSite, final String locationName, final BearingConfiguration.Approach approach, final BearingClientCallback bearingClientCallback) {
        if (requiredSite == null) {
            bearingClientCallback.onRequestFailure("No site exists to upload location");
            return;
        }
        try {
            final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
                    ConfigurationSettings.getConfiguration().getServerURL(),
                    "sites/" + requiredSite.getSiteId() + "/locations/");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("locationName", locationName);
            jsonObject.put("docVersion", requiredSite.getDocVersion());
            jsonObject.put("schemaVersion", requiredSite.getDocVersion());
            requestObject.setMessageBody(jsonObject.toString());
            requestObject.setNonBezirkRequest(true);
            requestObject.setCertFileStream(getCertificate());
            commsManager.processRequest(requestObject, new CommsListener() {
                @Override
                public void onResponse(ResponseObject responseObject) {
                    if (responseObject.getStatusCode() == HttpURLConnection.HTTP_CREATED) {
                        try {
                            final Long locationId = Long.valueOf(String.valueOf(new JSONObject(responseObject.getResponseBody().toString()).get("id")));
                            uploadLocationDataFile(requiredSite, locationId, locationName, approach, bearingClientCallback);
                        } catch (JSONException e) {
                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err parsing JSON", e);
                            bearingClientCallback.onRequestFailure("Err parsing data for location::" + locationName);
                        }
                    } else if (responseObject.getStatusCode() == HttpURLConnection.HTTP_CONFLICT) {
                        final RequestObject requestObject1 = new RequestObject(RequestObject.RequestType.GET,
                                ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + requiredSite.getSiteId() + "/locations/");
                        requestObject1.setNonBezirkRequest(true);
                        requestObject1.setCertFileStream(getCertificate());
                        commsManager.processRequest(requestObject1, new CommsListener() {
                            @Override
                            public void onResponse(ResponseObject responseObject) {
                                try {
                                    final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                                    Long locationId = -1L;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        final Location location = gson.fromJson(jsonArray.get(i).toString(), Location.class);
                                        if (location.getLocationName().equals(locationName)) {
                                            locationId = location.getLocationId();
                                            break;
                                        }
                                    }
                                    uploadLocationDataFile(requiredSite, locationId, locationName, approach, bearingClientCallback);
                                } catch (JSONException e) {
                                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err parsing json", e);
                                    bearingClientCallback.onRequestFailure("Err occurred uploading location data");
                                }
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                bearingClientCallback.onRequestFailure(s);
                            }
                        });
                    } else {
                        bearingClientCallback.onRequestFailure("Err uploading location data for location: " + locationName);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, "", "");
                }
            });
        } catch (JSONException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err preparing request", e);
        }
    }

    private void uploadLocationDataFile(final Site requiredSite, final Long locationId, final String locationName, final BearingConfiguration.Approach approach, final BearingClientCallback bearingClientCallback) {
        if (requiredSite == null) {
            bearingClientCallback.onRequestFailure("No site exists with requested name!!");
            return;
        }
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.MULTIPART_POST,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/" + requiredSite.getSiteId() + "/locations/" + locationId + "/document/");


        if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
            final Map<String, String> threshRequestHeader = new HashMap<>();
            threshRequestHeader.put("approach", "thresholding");
            requestObject.setHeaders(threshRequestHeader);

            final File threshFile = persistenceHandler.readThreshDataForLocationFile(requiredSite.getSiteName(), locationName);
            requestObject.setMultipartFile(threshFile);
        } else {
            final File file = persistenceHandler.readFingerPrintDataFile(requiredSite.getSiteName(), locationName);
            requestObject.setMultipartFile(file);
        }

        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());

        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    bearingClientCallback.onRequestSuccess(responseObject.getResponseBody().toString());
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure(s);
            }
        });
    }

    /**
     * Upload locations for site.
     *
     * @param siteName              the site name
     * @param approach              the corresponding approach files
     * @param bearingClientCallback the bearing client callback
     */
    void uploadLocationsForSite(@NonNull String siteName, @NonNull BearingConfiguration.Approach approach, @NonNull final BearingClientCallback bearingClientCallback) {
        uploadLocationsData(siteName, approach, bearingClientCallback);
    }

    private void uploadLocationsData(final String siteName, final BearingConfiguration.Approach approach, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        final Set<String> locationNames = persistenceHandler.getLocationNames(siteName, approach);
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    Site requiredSite = null;
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final Site site = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            if (siteName.equals(site.getSiteName())) {
                                requiredSite = site;
                                break;
                            }
                        }
                        uploadAllLocationForSite(requiredSite, locationNames, approach, bearingClientCallback);
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err: ", e);
                        bearingClientCallback.onRequestFailure("Err parsing data for site::" + siteName);
                    }
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure(s);
            }
        });
    }

    private void uploadAllLocationForSite(final Site requiredSite, final Set<String> locationNames, final BearingConfiguration.Approach approach, final BearingClientCallback bearingClientCallback) {
        final int locationCount = locationNames.size();
        final int[] uploadCount = {0};
        if (locationNames.isEmpty()) {
            bearingClientCallback.onRequestFailure("No fingerprint locations to upload");
            return;
        }
        for (String locationName : locationNames) {
            createLocationDataSpaceForSite(requiredSite, locationName, approach, new BearingClientCallback() {
                @Override
                public void onRequestSuccess(String message) {
                    uploadCount[0]++;
                    if (uploadCount[0] == locationCount) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "All locations uploaded successfully");
                        bearingClientCallback.onRequestSuccess(message);
                    }
                }

                @Override
                public void onRequestFailure(String errMessage) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "onRequestFailure: upload failed for location");
                    bearingClientCallback.onRequestFailure(errMessage);
                }
            });
        }
    }

    /**
     * Upload learning data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    void uploadClassifierData(@NonNull String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final SvmClassifierData svmClassifierData = persistenceHandler.readClassifiers(siteName);
        if (svmClassifierData == null || svmClassifierData.getClassifierData() == null) {
            bearingClientCallback.onRequestFailure(BearingClientCallback.Status.INVALID_DATA_FOR_TRAINING_PERSIST.toString());
            return;
        }
        triggerLearningDataGenerationOnServer(siteName, BearingConfiguration.Approach.FINGERPRINT, bearingClientCallback);
        return;
    }

    /**
     * Generate svmt on server.
     *
     * @param siteName              the site name
     * @param approach              the approach
     * @param bearingClientCallback the bearing client callback
     */
    void generateClassifierDataOnServer(@NonNull String siteName, BearingConfiguration.Approach approach, @NonNull final BearingClientCallback bearingClientCallback) {
        triggerLearningDataGenerationOnServer(siteName, approach, bearingClientCallback);
        return;
    }

    private void triggerLearningDataGenerationOnServer(final String siteName, final BearingConfiguration.Approach approach, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/search/");
        final Map<String, String> queryParamsMap = new HashMap<>();
        queryParamsMap.put("name", siteName);
        requestObject.setQueryParams(queryParamsMap);
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    Long id = -1L;
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final Site site = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            if (siteName.equals(site.getSiteName())) {
                                id = site.getSiteId();
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "", e);
                        bearingClientCallback.onRequestFailure("Err parsing data for site::" + siteName);
                    }
                    final RequestObject generateClassifierRequestObj = new RequestObject(RequestObject.RequestType.GET,
                            ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/generate/");

                    if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
                        Map<String, String> queryParams = new HashMap<>();
                        queryParams.put("approach", "thresholding");
                        generateClassifierRequestObj.setQueryParams(queryParams);
                    }

                    generateClassifierRequestObj.setNonBezirkRequest(true);
                    generateClassifierRequestObj.setCertFileStream(getCertificate());

                    commsManager.processRequest(generateClassifierRequestObj, new CommsListener() {
                        @Override
                        public void onResponse(ResponseObject responseObject) {
                            if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                                bearingClientCallback.onRequestSuccess(responseObject.getResponseBody().toString());
                            } else {
                                bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            bearingClientCallback.onRequestFailure("Status: " + i + " Response Msg: " + s);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure("Error generating SVMT on server: " + s);
            }
        });
    }

    /**
     * Rename site on server.
     *
     * @param oldSiteName           the old site name
     * @param newSiteName           the new site name
     * @param bearingClientCallback the bearing client callback
     */
    void renameSiteOnServer(final String oldSiteName, final String newSiteName, final BearingClientCallback bearingClientCallback) {
        /*final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/search/");
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    Long id = -1L;
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final Site site = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            if (oldSiteName.equals(site.getSiteName())) {
                                id = site.getSiteId();
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "", e);
                        bearingClientCallback.onRequestFailure("Err parsing data for site::" + oldSiteName);
                    }
                    final SiteData siteData = new SiteData(newSiteName);
                    final RequestObject requestObject1 = new RequestObject(RequestObject.RequestType.PUT,
                            ConfigurationSettings.getConfiguration().getServerURL(),
                            "sites/" + id);
                    requestObject1.setMessageBody(gson.toJson(siteData));
                    commsManager.processRequest(requestObject1, new CommsListener() {
                        @Override
                        public void onResponse(ResponseObject responseObject) {
                            if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                                bearingClientCallback.onRequestSuccess(responseObject.getResponseBody().toString());
                            } else {
                                bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            bearingClientCallback.onRequestFailure("Status: " + i + "Msg: " + s);
                        }
                    });
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure("Status: " + i + "Msg: " + s);
            }
        });*/
    }

    /**
     * Process data on server.
     *
     * @param siteName                the site name
     * @param snapshotObservationList the snapshot observation list
     * @param sensorTypes             the sensor types
     * @param bearingClientCallback   the bearing client callback
     */
    void processDataOnServer(String siteName, List<SnapshotObservation> snapshotObservationList, List<BearingConfiguration.SensorType> sensorTypes, final BearingClientCallback bearingClientCallback) {
        RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/" + siteName + "/detectLocation");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        Type type = new TypeToken<List<SnapshotObservation>>() {
        }.getType();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("snapshotObservationList", gson.toJson(snapshotObservationList, type));
            jsonObject.put("sensorTypes", sensorTypes);
        } catch (JSONException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Error adding data to JSONObject", e);
        }
        requestObject.setMessageBody(jsonObject.toString());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getResponseBody() != null) {
                    bearingClientCallback.onRequestSuccess(responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "onFailure: " + s + " status code: " + i, null);
                bearingClientCallback.onRequestFailure(s);
            }
        });
    }

    /**
     * #  **************************************************** BLE Thresh data upload APIs **************************************************** #
     */

    void uploadSiteThreshLocationsData(final String siteName, final BearingClientCallback bearingClientCallback) {
        final Set<String> locationNames = persistenceHandler.getLocationNames(siteName, BearingConfiguration.Approach.THRESHOLDING);
        final int[] uploadCount = {0};
        if (locationNames.isEmpty()) {
            bearingClientCallback.onRequestFailure("No thresh locations to upload");
            return;
        }
        for (String loc : locationNames) {
            final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
                    ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + siteName + "/uploadBLELocationThreshData");
            requestObject.setCertFileStream(getCertificate());
            requestObject.setNonBezirkRequest(true);
            final ThreshData threshData = new ThreshData();
            threshData.setSiteName(siteName);
            threshData.setLocationName(loc);
            threshData.setThreshContent(new SnapshotItemManager().
                    snapshotObservationListToJSONString(persistenceHandler.readLocationThreshData(siteName, loc)));
            final String postData = gson.toJson(threshData);
            requestObject.setMessageBody(postData);
            commsManager.processRequest(requestObject, new CommsListener() {
                @Override
                public void onResponse(ResponseObject responseObject) {
                    if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                        uploadCount[0]++;
                        if (locationNames.size() == uploadCount[0]) {
                            bearingClientCallback.onRequestSuccess("All BLE locations uploaded successfully for site:: " + siteName);
                        }
                    } else {
                        bearingClientCallback.onRequestFailure("Error uploading BLE locations for site:: " + siteName);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    bearingClientCallback.onRequestFailure("Error:: " + s);
                }
            });
        }
    }
}
