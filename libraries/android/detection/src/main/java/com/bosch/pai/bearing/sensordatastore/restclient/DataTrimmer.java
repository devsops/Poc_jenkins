package com.bosch.pai.bearing.sensordatastore.restclient;


import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.StorageType;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.comms.CommsManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.sql.Timestamp;

/**
 * The type Data trimmer.
 */
final class DataTrimmer {

    private static final String TAG = DataTrimmer.class.getName();
    private final PersistenceHandler persistenceHandler;
    private final CommsManager commsManager;
    private final Gson gson;
    private String certificateStream;

    /**
     * Instantiates a new Data trimmer.
     *
     * @param commsManager the comms manager
     */
    DataTrimmer(CommsManager commsManager) {
        this.persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        this.commsManager = commsManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new GsonUTCAdapter())
                .create();
    }

    void setCertificateStream(String certificateStream) {
        this.certificateStream = certificateStream;
    }

    /**
     * Delete site data.
     *
     * @param siteName              the site name
     * @param bearingClientCallback the bearing client callback
     */
    void deleteSiteData(String siteName, BearingClientCallback bearingClientCallback) {

    }

    /**
     * Delete location data.
     *
     * @param siteName              the site name
     * @param locationName          the location name
     * @param storage               the storage
     * @param bearingClientCallback the bearing client callback
     */
    void deleteLocationData(String siteName, String locationName, StorageType storage, BearingClientCallback bearingClientCallback) {

    }
}
