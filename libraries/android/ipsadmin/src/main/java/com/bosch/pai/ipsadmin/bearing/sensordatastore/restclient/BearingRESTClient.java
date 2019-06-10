package com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient;


import android.content.Context;
import android.support.annotation.NonNull;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.persistence.StorageType;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.exception.CertificateLoadException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * BearingRESTClient for making bearing specific requests to server
 * 1. Upload Data
 * 2. Download Data
 * 3. Delete Data
 */
public class BearingRESTClient {

    private static final String TAG = BearingRESTClient.class.getSimpleName();
    private static BearingRESTClient bearingRESTClient;
    private String certificateInputStreamString = null;
    private final CommsManager commsManager;

    private BearingRESTClient(@NonNull final Context context) throws CertificateLoadException {
        this.commsManager = CommsManager.getInstance();
    }

    /**
     * Init coms with context.
     *
     * @param context the context
     * @throws CertificateLoadException the certificate load exception
     */
    public static void initComsWithContext(Context context) throws CertificateLoadException {

        if (bearingRESTClient == null) {
            try {
                bearingRESTClient = new BearingRESTClient(context);
            } catch (CertificateLoadException e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "init: ", e);
                throw new CertificateLoadException(e.toString());
            }
        }
    }


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BearingRESTClient getInstance() {
        return bearingRESTClient;
    }


    /**
     * Sets https certificate.
     *
     * @param httpsCertificate the https certificate
     */
/*When certificate is set , it is converted to String for storage. subsequently for every request , it is read from string and converted to inputStream */
    public synchronized void setHttpsCertificate(InputStream httpsCertificate) {

        if (httpsCertificate != null) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = httpsCertificate.read(buffer)) > -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Unable to store certificate. ", e);
            }
            this.certificateInputStreamString = byteArrayOutputStream.toString();
        }
    }


    /**
     * **************************************************************************************
     * ************************************** UPLOAD  ***************************************
     * **************************************************************************************
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void uploadSiteData(@NonNull final String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.uploadSiteData(siteName, bearingClientCallback);
    }

    /**
     * Upload location data.
     *
     * @param siteName              the site name
     * @param locationName          the location name
     * @param approach              the approach
     * @param bearingClientCallback the bearing client callback
     */
    public void uploadLocationData(@NonNull final String siteName, @NonNull final String locationName, @NonNull BearingConfiguration.Approach approach, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.uploadLocationData(siteName, locationName, approach, bearingClientCallback);
    }

    /**
     * Upload locations data for site.
     *
     * @param siteName              the site name
     * @param approach              the approach
     * @param bearingClientCallback the bearing client callback
     */
    public void uploadLocationsDataForSite(@NonNull String siteName, @NonNull BearingConfiguration.Approach approach, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.uploadLocationsForSite(siteName, approach, bearingClientCallback);
    }

    /**
     * Upload classifier data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void uploadClassifierData(@NonNull String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.uploadClassifierData(siteName, bearingClientCallback);
    }

    /**
     * Generate svmt on server.
     *
     * @param siteName              the site name
     * @param approach              the approach
     * @param bearingClientCallback the bearing client callback
     */
    public void generateSVMTOnServer(@NonNull String siteName, @NonNull BearingConfiguration.Approach approach, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.generateClassifierDataOnServer(siteName, approach, bearingClientCallback);
    }

    /**
     * Rename site on server.
     *
     * @param oldSiteName           the old site name
     * @param newSiteName           the new site name
     * @param bearingClientCallback the bearing client callback
     */
    public void renameSiteOnServer(@NonNull final String oldSiteName, @NonNull final String newSiteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.renameSiteOnServer(oldSiteName, newSiteName, bearingClientCallback);
    }

    /**
     * Process data on server.
     *
     * @param siteName                the site name
     * @param snapshotObservationList the snapshot observation list
     * @param sensorTypes             the sensor types
     * @param bearingClientCallback   the bearing client callback
     */
    public void processDataOnServer(String siteName, List<SnapshotObservation> snapshotObservationList, List<BearingConfiguration.SensorType> sensorTypes, BearingClientCallback bearingClientCallback) {
        DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.processDataOnServer(siteName, snapshotObservationList, sensorTypes, bearingClientCallback);
    }

    /**
     * Upload site thresh data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void uploadSiteThreshLocationsData(final String siteName, final BearingClientCallback bearingClientCallback) {
        DataUploader dataUploader = new DataUploader(commsManager);
        dataUploader.setCertificateStream(certificateInputStreamString);
        dataUploader.uploadSiteThreshLocationsData(siteName, bearingClientCallback);
    }

    /**
     * **************************************************************************************
     * ************************************* DOWNLOAD  **************************************
     * **************************************************************************************
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void downloadSiteData(@NonNull final String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.downloadSiteData(siteName, bearingClientCallback);
    }

    /**
     * Download location data.
     *
     * @param siteName              the site name
     * @param locationName          the location name
     * @param bearingClientCallback the bearing client callback
     */
    public void downloadLocationData(@NonNull final String siteName, @NonNull final String locationName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.downloadLocationData(siteName, locationName, bearingClientCallback);
    }

    /**
     * Download locations data for site.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void downloadLocationsDataForSite(@NonNull final String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.downloadAllLocationDataForSiteFromServer(siteName, bearingClientCallback);
    }

    /**
     * Download classifier data.
     *
     * @param siteName              the site name
     * @param isPersist             the is persist
     * @param bearingClientCallback the bearing client callback
     */
    public void downloadClassifierData(@NonNull final String siteName, final boolean isPersist, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.downloadClassifierData(siteName, isPersist, bearingClientCallback);
    }

    /**
     * Gets all site names.
     *
     * @param getDataCallback the get data callback
     */
    public void getAllSiteNames(@NonNull final BearingClientCallback.GetDataCallback getDataCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.getAllSiteNames(getDataCallback);
    }

    /**
     * Gets all location names for site.
     *
     * @param siteName        the site name
     * @param getDataCallback the get data callback
     */
    public void getAllLocationNamesForSite(@NonNull final String siteName, @NonNull final BearingClientCallback.GetDataCallback getDataCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.getAllLocationNames(siteName, getDataCallback);
    }

    /**
     * Gets cluster data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void getClusterData(@NonNull String siteName, @NonNull final BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.getClusterData(siteName, bearingClientCallback);
    }

    /**
     * Download source id map.
     *
     * @param bearingClientCallback the bearing client callback
     */
    public void downloadSourceIdMap(BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.downloadSourceIdMap(bearingClientCallback);
    }

    /**
     * Download site thresh data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    public void downloadSiteThreshData(final String siteName, final BearingClientCallback bearingClientCallback) {
        final DataDownloader dataDownloader = new DataDownloader(commsManager);
        dataDownloader.setCertificateStream(certificateInputStreamString);
        dataDownloader.downloadSiteThreshData(siteName, bearingClientCallback);
    }

    /**
     * **************************************************************************************
     * ************************************** DELETE  ***************************************
     * **************************************************************************************
     *
     * @param siteName              the site name
     * @param storage               the storage
     * @param bearingClientCallback the bearing client callback
     */
    public void deleteSiteData(String siteName, StorageType storage, final BearingClientCallback bearingClientCallback) {
        final DataTrimmer dataTrimmer = new DataTrimmer(commsManager);
        dataTrimmer.setCertificateStream(certificateInputStreamString);
        dataTrimmer.deleteSiteData(siteName, bearingClientCallback);
    }

    /**
     * Delete location data.
     *
     * @param siteName              the site name
     * @param locationName          the location name
     * @param storage               the storage
     * @param bearingClientCallback the bearing client callback
     */
    public void deleteLocationData(String siteName, String locationName, StorageType storage, final BearingClientCallback bearingClientCallback) {
        final DataTrimmer dataTrimmer = new DataTrimmer(commsManager);
        dataTrimmer.setCertificateStream(certificateInputStreamString);
        dataTrimmer.deleteLocationData(siteName, locationName, storage, bearingClientCallback);
    }
}
