package com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.SiteDetectionConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
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
import com.bosch.pai.bearing.persistence.datastore.LocalAppDataStore;
import com.bosch.pai.bearing.persistence.util.CryptoException;
import com.bosch.pai.bearing.persistence.util.PersistenceResult;
import com.bosch.pai.bearing.persistence.util.Util;
import com.bosch.pai.bearing.util.SnapshotItemManager;
import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * The type Data downloader.
 */
final class DataDownloader {
    private static final String TAG = DataDownloader.class.getName();
    private final PersistenceHandler persistenceHandler;
    private final CommsManager commsManager;
    private String certificateStream;
    private final Gson gson;

    /**
     * Instantiates a new Data downloader.
     *
     * @param commsManager the comms manager
     */
    DataDownloader(CommsManager commsManager) {
        this.persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        this.commsManager = commsManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new GsonUTCAdapter())
                .create();
    }

    void setCertificateStream(String certificateStream) {
        this.certificateStream = certificateStream;
    }

    synchronized InputStream getCertificate() {

        if (this.certificateStream != null) {
            return new ByteArrayInputStream(this.certificateStream.getBytes());
        } else {
            return null;
        }
    }

    /**
     * Download site data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    void downloadSiteData(final String siteName, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject;

        requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());

        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    Site retrievedSiteData = null;
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        int i = 0;
                        while (i < jsonArray.length()) {
                            retrievedSiteData = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            if (retrievedSiteData.getSiteName().equals(siteName))
                                break;
                            i++;
                        }
                    } catch (JSONException | IndexOutOfBoundsException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "JSONException", e);
                    }
                    if (retrievedSiteData != null) {
                        saveSiteDataOnDevice(siteName, retrievedSiteData);
                        bearingClientCallback.onRequestSuccess("Site data downloaded successfully: " + siteName);
                        return;
                    }
                }
                bearingClientCallback.onRequestFailure(responseObject.getStatusMessage() == null ?
                        "No site data for site: " + siteName : responseObject.getStatusMessage());

            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure("Status:" + i + " Response: " + s);
            }
        });
    }

    private void saveSiteDataOnDevice(String siteName, Object retrievedSiteData) {
        final Snapshot snapshot;
      /*  if (BearingConfiguration.ServerExistence.OLD == serverExistence) {
            final TrainedSiteData trainedSiteData = (TrainedSiteData) retrievedSiteData;
            snapshot = new Snapshot();
            snapshot.setDocVersion(trainedSiteData.getDocVersion());
            snapshot.setSchemaVersion(trainedSiteData.getSchemaVersion());
            snapshot.setTimeStamp(trainedSiteData.getTimestamp());
            snapshot.setSensors(trainedSiteData.getSnapshotObservationList());
        } else {*/
        final Site site = (Site) retrievedSiteData;
        snapshot = new Snapshot();
        snapshot.setDocVersion(site.getDocVersion());
        snapshot.setSchemaVersion(site.getSchemaVersion());
        snapshot.setTimeStamp(new Date(site.getEpochMilliSeconds()).toString());
        List<SnapshotObservation> snapshotObservationList = new ArrayList<SnapshotObservation>();
        for (Sensor sensor : site.getSensors()) {
            SnapshotObservation snapshotObservation = new SnapshotObservation();
            snapshotObservation.setSensorType(BearingConfiguration.SensorType.valueOf(sensor.getSensorType().toString()));
            snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.valueOf(sensor.getDetectionLevel().toString()));
            List<com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem> listOfSnapShotItem = new ArrayList<>();
            for (SnapShotItem snapShotItem : sensor.getSnapShotItemList()) {
                com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem snapShotItemEntity = new com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem();
                List<String> customFieldList = snapShotItem.getCustomFields();
                String[] customFields = new String[customFieldList.size()];
                for (int j = 0; j < customFieldList.size(); j++)
                    customFields[j] = customFieldList.get(j);
                snapShotItemEntity.setCustomField(customFields);
                List<Double> listOfMeasuredValues = snapShotItem.getMeasuredValues();
                double[] measuredValues = new double[listOfMeasuredValues.size()];
                for (int j = 0; j < listOfMeasuredValues.size(); j++)
                    measuredValues[j] = listOfMeasuredValues.get(j);
                snapShotItemEntity.setMeasuredValues(measuredValues);
                snapShotItemEntity.setSourceId(snapShotItem.getSourceId());
                listOfSnapShotItem.add(snapShotItemEntity);
            }
            snapshotObservation.setSnapShotItemList(listOfSnapShotItem);
            snapshotObservationList.add(snapshotObservation);
        }
        snapshot.setSensors(snapshotObservationList);
        // }
        persistenceHandler.deleteDataPersistenceSpace(siteName);
        persistenceHandler.writeSnapShot(siteName, snapshot);
    }

    /**
     * Download location data.
     *
     * @param siteName              the site name
     * @param locationName          the location name
     * @param bearingClientCallback the bearing client callback
     */
    void downloadLocationData(final String siteName, final String locationName, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject;

        requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/");
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
                        downloadLocationDataFromNEWServer(id, siteName, locationName, bearingClientCallback);
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
                bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
            }
        });
    }

    private void downloadLocationDataFromNEWServer(final Long id, final String siteName, final String locationName, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/locations/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    try {
                        final Type type = new TypeToken<List<Location>>() {
                        }.getType();
                        final List<Location> locations = gson.fromJson(new JSONArray(responseObject.getResponseBody().toString()).toString(), type);
                        for (Location location : locations) {
                            if (location.getLocationName().equals(locationName)) {
                                final RequestObject requestObject1 = new RequestObject(RequestObject.RequestType.GET,
                                        ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/locations/" + location.getLocationId() + "/document");
                                requestObject1.setNonBezirkRequest(true);
                                requestObject1.setCertFileStream(getCertificate());
                                commsManager.processRequest(requestObject1, new CommsListener() {
                                    @Override
                                    public void onResponse(ResponseObject responseObject) {
                                        if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                                            persistenceHandler.deleteDataPersistenceSpace(siteName, locationName);
                                            final PersistenceResult persistenceResult = persistenceHandler.writeLocationFingerPrintData(siteName, locationName, responseObject.getResponseBody().toString());
                                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Persisting location data for siteName: " + siteName + " locationName: " + locationName + " Result: " + persistenceResult);
                                            bearingClientCallback.onRequestSuccess("Successfully downloaded location data site: " + siteName + " locationName: " + locationName);
                                        } else {
                                            bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
                                    }
                                });
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err", e);
                        bearingClientCallback.onRequestFailure("Err fetching location data!");
                    }
                } else {
                    bearingClientCallback.onRequestFailure("Err fetching location data!");
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err getting location data Code: " + i + " Msg: " + s);
                bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
            }
        });
    }

    private List<double[]> parseCSVfromMap(Map<String, double[]> fingerPrintData) {
        final ArrayList<double[]> rawFingerPrintData = new ArrayList<>();
        final List<double[]> entryList = new LinkedList<>(fingerPrintData.values());
        final int size = entryList.size();
        final int numOfSamples = entryList.get(0).length;
        int rowCount = 0;
        for (int columnCount = 0; columnCount < numOfSamples; columnCount++) {
            final double[] tempDoubleArray = new double[size + 1];
            tempDoubleArray[0] = 1.0 + rowCount;
            int i = 1;
            for (double[] doubles : entryList) {
                tempDoubleArray[i++] = doubles[rowCount];
            }
            rawFingerPrintData.add(tempDoubleArray);
            rowCount++;
        }

        return rawFingerPrintData;

    }

    private String[] parseHeaderfromMap(Map<String, double[]> fingerPrintData) {
        final List<double[]> entryList = new LinkedList<>(fingerPrintData.values());
        final int size = entryList.size() + 1;
        final String[] rawFingerPrintData = new String[size];
        final Set<String> accessHeaders = fingerPrintData.keySet();
        rawFingerPrintData[0] = "S.NO";
        for (int i = 1; i < accessHeaders.size() + 1; i++) {
            rawFingerPrintData[i] = (String) fingerPrintData.keySet().toArray()[i - 1];
        }
        return rawFingerPrintData;
    }

    private boolean saveLocationDataOnDevice(final Object obj) {
        /*final TrainedLocationData trainedLocationData = (TrainedLocationData) obj;
        LinkedHashMap<String, double[]> accessPointMap = trainedLocationData.getAccesspointRssiMap();
        final List<double[]> doubles = parseCSVfromMap(accessPointMap);
        final String[] accessHeader = parseHeaderfromMap(accessPointMap);
        StringBuilder stringBuilder = new StringBuilder();
        String headerString = Arrays.toString(accessHeader);
        String regex = "\\[|\\]";
        headerString = headerString.replaceAll(regex, "");
        stringBuilder.append(headerString);
        stringBuilder.append("\n");
        for (double[] accessValue : doubles) {
            String accessValueList = Arrays.toString(accessValue);
            accessValueList = accessValueList.replaceAll(regex, "");
            stringBuilder.append(accessValueList);
            stringBuilder.append("\n");
        }
        final String csvContent = stringBuilder.toString();
        persistenceHandler.deleteDataPersistenceSpace(trainedLocationData.getSiteName(), trainedLocationData.getLocationName());
        final PersistenceResult persistenceResult = persistenceHandler.writeLocationFingerPrintData(trainedLocationData.getSiteName(),
                trainedLocationData.getLocationName(), csvContent);
        return persistenceResult != PersistenceResult.RESULT_CANCEL;*/
        return false;
    }

    /**
     * Download all location data for site from server.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    void downloadAllLocationDataForSiteFromServer(final String siteName, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject;
        requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());

        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                // if (serverExistence == BearingConfiguration.ServerExistence.NEW) {
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
                        downloadLocationsDataFromNEWServer(id, siteName, bearingClientCallback);
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
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err getting location data Code: " + i + " Msg: " + s);
                bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
            }
        });
    }

    private void downloadLocationsDataFromNEWServer(final Long id, final String siteName, final BearingClientCallback bearingClientCallback) {
        final int[] downloadCount = {0};
        final int[] availableCount = {0};
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/locations/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    try {
                        final Type type = new TypeToken<List<Location>>() {
                        }.getType();
                        final List<Location> locations = gson.fromJson(new JSONArray(responseObject.getResponseBody().toString()).toString(), type);
                        availableCount[0] = locations.size();
                        for (final Location location : locations) {
                            final RequestObject requestObject1 = new RequestObject(RequestObject.RequestType.GET,
                                    ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/locations/" + location.getLocationId() + "/document");
                            requestObject1.setNonBezirkRequest(true);
                            requestObject1.setCertFileStream(getCertificate());
                            commsManager.processRequest(requestObject1, new CommsListener() {
                                @Override
                                public void onResponse(ResponseObject responseObject) {
                                    if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                                        persistenceHandler.deleteDataPersistenceSpace(siteName, location.getLocationName());
                                        final PersistenceResult persistenceResult = persistenceHandler.writeLocationFingerPrintData(siteName, location.getLocationName(), responseObject.getResponseBody().toString());
                                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Persisting location data for siteName: " + siteName + " locationName: " + location.getLocationName() + " Result: " + persistenceResult);
                                        downloadCount[0]++;
                                    } else {
                                        bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                                    }
                                    if (availableCount[0] == downloadCount[0])
                                        bearingClientCallback.onRequestSuccess("Successfully downloaded locations data for site: " + siteName);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err", e);
                        bearingClientCallback.onRequestFailure("Err fetching location data!");
                    }
                } else {
                    bearingClientCallback.onRequestFailure("Err fetching location data!");
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err getting location data Code: " + i + " Msg: " + s);
                bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
            }
        });

    }

    /**
     * Download classifier data.
     *
     * @param siteName              the site name
     * @param isPersist             the is persist
     * @param bearingClientCallback the bearing client callback
     */
    void downloadClassifierData(final String siteName, final boolean isPersist, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject;

        requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());

        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                // if (serverExistence == BearingConfiguration.ServerExistence.NEW) {
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
                        getSVMTForSiteFromNEWServer(id, siteName, isPersist, bearingClientCallback);
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err parsing json", e);
                        bearingClientCallback.onRequestFailure("Err fetching SVMT data for site:" + siteName);
                    }
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err getting location data Code: " + i + " Msg: " + s);
                bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
            }
        });
    }

    private void getSVMTForSiteFromNEWServer(final Long id, final String siteName, final boolean isPersist, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/" + id + "/classifiers/versions");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    final Type type = new TypeToken<List<ClassifierVersion>>() {
                    }.getType();
                    final List<ClassifierVersion> classifierVersions = gson.fromJson(responseObject.getResponseBody().toString(), type);
                    if (classifierVersions == null || classifierVersions.isEmpty()) {
                        bearingClientCallback.onRequestFailure("No SVMT data available for site: " + siteName);
                        return;
                    }
                    final RequestObject requestObject1 = new RequestObject(RequestObject.RequestType.GET,
                            ConfigurationSettings.getConfiguration().getServerURL(),
                            "sites/" + id + "/classifiers/");
                    final Map<String, String> queryParams = new HashMap<>();
                    queryParams.put("schema", classifierVersions.get(0).getSchemaName());
                    queryParams.put("schemaVer", classifierVersions.get(0).getSchemaVersion());
                    requestObject1.setQueryParams(queryParams);
                    requestObject1.setNonBezirkRequest(true);
                    requestObject1.setCertFileStream(getCertificate());
                    commsManager.processRequest(requestObject1, new CommsListener() {
                        @Override
                        public void onResponse(ResponseObject responseObject) {
                            if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                                final Classifier classifier = gson.fromJson(responseObject.getResponseBody().toString(), Classifier.class);
                                final SvmClassifierData svmClassifierData = new SvmClassifierData(new Timestamp(classifier.getCreatedAt()),
                                        siteName, classifier.getContentData(), classifier.getLocationNames());
                                if (isPersist) {
                                    persistenceHandler.writeClassifiers(siteName, svmClassifierData);
                                } else {
                                    LocalAppDataStore.getInstance().setSvmClassifierData(svmClassifierData);
                                }
                                bearingClientCallback.onRequestSuccess("SVMT data successfully downloaded for site: " + siteName);
                            } else {
                                bearingClientCallback.onRequestFailure("No svmt data available for site: " + siteName);
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err getting location data Code: " + i + " Msg: " + s);
                            bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
                        }
                    });
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err getting location data Code: " + i + " Msg: " + s);
                bearingClientCallback.onRequestFailure("ErrCode: " + i + " StatusMsg: " + s);
            }
        });
    }

    /**
     * Gets all site names.
     *
     * @param getDataCallback the get data callback
     */
    void getAllSiteNames(final BearingClientCallback.GetDataCallback getDataCallback) {
        final RequestObject requestObject;

        requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    final List<Site> sites = new ArrayList<>();
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        int i = 0;
                        while (i < jsonArray.length()) {
                            final Site site = gson.fromJson(jsonArray.get(i).toString(), Site.class);
                            sites.add(site);
                            i++;
                        }
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "JSONException", e);
                    }

                    final List<String> siteNames = new ArrayList<>();
                    for (Site site : sites)
                        siteNames.add(site.getSiteName());
                    getDataCallback.onDataReceived(siteNames);
                } else {
                    getDataCallback.onDataReceivedError(responseObject.getStatusMessage());
                }

            }

            @Override
            public void onFailure(int i, String s) {
                getDataCallback.onDataReceivedError("Status: " + i + " ErrMsg:" + s);
            }
        });
    }

    /**
     * Gets all location names.
     *
     * @param siteName        the site name
     * @param getDataCallback the get data callback
     */
    void getAllLocationNames(final String siteName, final BearingClientCallback.GetDataCallback getDataCallback) {
        final RequestObject requestObject;
        requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/search/");
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", siteName);
        requestObject.setQueryParams(queryParams);
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
                        getLocationNamesFromNEWServer(id, getDataCallback);
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err parsing json", e);
                        getDataCallback.onDataReceivedError("Err fetching location data for site:" + siteName);
                    }
                } else {
                    getDataCallback.onDataReceivedError(responseObject.getStatusMessage());
                }

            }

            @Override
            public void onFailure(int i, String s) {
                getDataCallback.onDataReceivedError("Status: " + i + " Err: " + s);
            }
        });
    }

    private void getLocationNamesFromNEWServer(final Long id, final BearingClientCallback.GetDataCallback getDataCallback) {
        final RequestObject requestObject1 = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + id + "/locations/");
        requestObject1.setNonBezirkRequest(true);
        requestObject1.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject1, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK) {
                    try {
                        final JSONArray jsonArray = new JSONArray(responseObject.getResponseBody().toString());
                        final List<String> locationNames = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++)
                            locationNames.add(gson.fromJson(jsonArray.get(i).toString(), Location.class).getLocationName());
                        getDataCallback.onDataReceived(locationNames);
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err parsing json", e);
                        getDataCallback.onDataReceivedError("Err occurred fetching location names");
                    }
                } else {
                    getDataCallback.onDataReceivedError(responseObject.getStatusMessage());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                getDataCallback.onDataReceivedError("Status: " + i + " ErrMsg: " + s);
            }
        });
    }

    /**
     * Gets cluster data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    void getClusterData(final String siteName, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/search/");
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", siteName);
        requestObject.setQueryParams(queryParams);
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
                        downloadClusterData(id, bearingClientCallback);
                    } catch (JSONException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Err parsing json", e);
                        bearingClientCallback.onRequestFailure("Err fetching location data for site:" + siteName);
                    }
                } else {
                    bearingClientCallback.onRequestFailure(responseObject.getStatusMessage());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure("Error downloading cluster data Status Msg:: " + s);
            }
        });
    }

    private void downloadClusterData(Long id, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/" + id + "/clusterData/");
        final Map<String, String> headerMap = new HashMap<>();
        headerMap.put("accept", "application/zip");
        requestObject.setHeaders(headerMap);
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                try {
                    if (responseObject.getStatusCode() == HttpsURLConnection.HTTP_OK && responseObject.getResponseBody() != null) {
                        saveClusterData((byte[]) responseObject.getResponseBody(), bearingClientCallback);
                        //deletePreviousSiteDataIfPresent(siteName);
                    } else {
                        bearingClientCallback.onRequestFailure("Error :" + responseObject.getStatusMessage());
                    }
                } catch (IOException e) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "onResponse: Exception", e);
                }
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Downloaded cluster data ");
            }

            @Override
            public void onFailure(int i, String s) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Failure Downloading cluster data ");
                bearingClientCallback.onRequestFailure("Error :: " + s);
            }
        });
    }


    private void deletePreviousSiteDataIfPresent(String siteName) {
        final Set<String> siteNames = persistenceHandler.getSiteNames();
        for (String temp : siteNames) {
            if (!siteName.equals(temp)) {
                persistenceHandler.deletePreviousClusterData(temp);
            }
        }
    }


    private void saveClusterData(byte[] bytes, BearingClientCallback bearingClientCallback) throws IOException {
        final String baseDataStorePath = Util.getStoragePath();

        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ZipInputStream zipStream = new ZipInputStream(byteArrayInputStream);

        ZipEntry entry = null;
        FileOutputStream out = null;
        while ((entry = zipStream.getNextEntry()) != null) {

            String entryName = entry.getName().replaceAll("\\\\", "/");

            File file;
            if (entryName.contains("/")) {
                File dir = new File(baseDataStorePath + entryName.substring(0, entryName.lastIndexOf('/')));
                dir.mkdirs();
                file = new File(dir.getPath() + File.separator + entryName.substring(entryName.lastIndexOf('/') + 1));
                boolean isCreated = file.createNewFile();
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "File created status :: " + isCreated);
            } else {
                file = new File(entryName);
                boolean isCreated = file.createNewFile();
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "File created status :: " + isCreated);
            }
            try {
                out = new FileOutputStream(file);
                byte[] byteBuff = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = zipStream.read(byteBuff)) != -1) {
                    out.write(byteBuff, 0, bytesRead);
                }
                out.close();
                zipStream.closeEntry();
                // Encryption of file done here
                final File encryptedFile = new File(file.getPath() + ".encrypted");
                /*if (!file.getName().endsWith(".snapshot")) {*/ // Now all files downloaded from server are encrypted
                final boolean createStatus = encryptedFile.createNewFile();
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Encrypted file create status :" + createStatus);
                Util.encrypt(file, encryptedFile);
                final boolean deleted = file.delete();
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, TAG, "Deleted download file :" + deleted);
                /*}*/
            } catch (IOException e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "error closing stream " + e);
                bearingClientCallback.onRequestFailure("Error :: " + e.getMessage());
            } catch (CryptoException e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Error encrypting", e);
            } finally {
                if (out != null)
                    out.close();
            }
        }
        zipStream.close();
        bearingClientCallback.onRequestSuccess("Successfully downloaded cluster data");
    }

    /**
     * Download source id map.
     *
     * @param bearingClientCallback the bearing client callback
     */
    void downloadSourceIdMap(final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, ConfigurationSettings.getConfiguration().getServerURL(),
                "sites/getSourceIdMapWithSiteConfiguration/");
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(getCertificate());
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (HttpURLConnection.HTTP_OK == responseObject.getStatusCode()) {
                    Map<String, Set<SiteDetectionConfiguration>> sourceIdMap = new HashMap<>();
                    try {
                        Type type = new TypeToken<Map<String, Set<SiteDetectionConfiguration>>>() {
                        }.getType();
                        Map<String, Set<SiteDetectionConfiguration>> temp = gson.fromJson(responseObject.getResponseBody().toString(), type);
                        if (temp != null) {
                            sourceIdMap.putAll(temp);
                        }
                    } catch (JsonSyntaxException e) {
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Error parsing JSON", e);
                        //Log.e(TAG, "Error parsing JSON");
                        bearingClientCallback.onRequestFailure("Error downloading sourceIdMap");
                    }
                    if (saveSourceIDMap(sourceIdMap)) {
                        bearingClientCallback.onRequestSuccess("Downloaded sourceIdMap successfully");
                    } else {
                        bearingClientCallback.onRequestFailure("Error downloading sourceIdMap");
                    }
                } else {
                    bearingClientCallback.onRequestFailure("Error:" + responseObject.getStatusMessage());
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure("Error :: StatusCode :" + i + " msg :" + s);
            }
        });
    }

    private boolean saveSourceIDMap(Map<String, Set<SiteDetectionConfiguration>> sourceIdMap) {
        return persistenceHandler.saveSourceIdMapWithSiteConfiguration(sourceIdMap);
    }


    void downloadSiteThreshData(final String siteName, final BearingClientCallback bearingClientCallback) {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,
                ConfigurationSettings.getConfiguration().getServerURL(), "sites/" + siteName + "/downloadBLESourceIdMap");
        requestObject.setCertFileStream(getCertificate());
        requestObject.setNonBezirkRequest(true);
        commsManager.processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    final Type type = new TypeToken<ThreshData>() {
                    }.getType();
                    try {
                        final ThreshData threshData = gson.fromJson(responseObject.getResponseBody().toString(), type);
                        if (threshData.getThreshContent() != null &&
                                !new SnapshotItemManager().jsonStringToThreshSourceIdMap(threshData.getThreshContent()).isEmpty()) {
                            saveBLEThreshForSite(siteName, threshData, bearingClientCallback);
                        }
                        bearingClientCallback.onRequestSuccess("Successfully downloaded ble locations");
                    } catch (JsonSyntaxException e) {
                        bearingClientCallback.onRequestFailure("Error parsing thresh data for site:: " + siteName);
                    }
                } else {
                    bearingClientCallback.onRequestFailure("Error downloading thresh data for site:: " + siteName);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                bearingClientCallback.onRequestFailure("Error:: " + s);
            }
        });
    }

    private void saveBLEThreshForSite(final String siteName, final ThreshData threshData, final BearingClientCallback bearingClientCallback) {
        final boolean isSuccess = persistenceHandler.saveSiteThreshData(siteName, threshData);
        if (isSuccess) {
            bearingClientCallback.onRequestSuccess("Successfully downloaded thresh data for site:: " + siteName);
        } else {
            bearingClientCallback.onRequestFailure("Error downloading thresh data for site:: " + siteName);
        }
    }
}
