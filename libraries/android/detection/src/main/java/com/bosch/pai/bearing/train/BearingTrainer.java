package com.bosch.pai.bearing.train;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.persistence.util.Util;
import com.bosch.pai.bearing.train.errorcode.Codes;
import com.bosch.pai.bearing.train.operations.Create;
import com.bosch.pai.bearing.train.operations.Retrieve;
import com.bosch.pai.bearing.train.operations.Update;
import com.bosch.pai.bearing.train.operations.Upload;
import com.bosch.pai.bearing.train.operations.Validation;
import com.bosch.pai.comms.exception.CertificateLoadException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

/**
 * The type Bearing train.
 */
public class BearingTrainer {
    private final Logger logger = LoggerFactory.getLogger(BearingTrainer.class);
    private static final String TAG = BearingTrainer.class.getSimpleName();
    private static BearingTrainer bearingTrain;
    private BearingHandler bearingHandler;
    private Create bearingCreate;
    private Retrieve bearingRetrieve;
    private Update bearingUpdate;
    private Upload bearingUpload;

    private BearingTrainer(Context appContext) {
        try {
            ConfigurationSettings.setConfigFileLocation(appContext.getFilesDir().getPath());
            ConfigurationSettings.getConfiguration().deleteConfigObject();
            if (Build.VERSION.SDK_INT > 22) {
                Sensor sensor = ConfigurationSettings.getConfiguration().getSensorPreferences().withProperty(Property.Sensor.BLE_ACTIVE_MODE_INTERVAL, 10000)
                        .withProperty(Property.Sensor.BLE_SCAN_TIMEOUT, 9000).withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 8000)
                        .withProperty(Property.Sensor.IMU_SCAN_TIMEOUT, 8000).withProperty(Property.Sensor.MAGNETO_SCAN_TIMEOUT, 8000);
                ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().withSensorPreferences(sensor));
            } else {
                Sensor sensor = ConfigurationSettings.getConfiguration().getSensorPreferences().withProperty(Property.Sensor.BLE_ACTIVE_MODE_INTERVAL, 5000)
                        .withProperty(Property.Sensor.BLE_SCAN_TIMEOUT, 4000).withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 5000)
                        .withProperty(Property.Sensor.IMU_SCAN_TIMEOUT, 5000).withProperty(Property.Sensor.MAGNETO_SCAN_TIMEOUT, 5000);
                ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().withSensorPreferences(sensor));
            }
            BearingHandler.init(appContext);
        } catch (CertificateLoadException e) {
            logger.error(TAG, "Error loading certificate", e);
        }
        bearingHandler = BearingHandler.getInstance();
        if (!bearingHandler.isAlive()) {
            startBearingHandler(bearingHandler);
        }
        if (!AlgorithmLifeCycleHandler.getInstance().isAlive()) {
            startAlgorithmHandler(AlgorithmLifeCycleHandler.getInstance());
        }
        Util.setInternalStoragePath(appContext.getFilesDir().getPath() + File.separator + "DataStore" + File.separator);
        Util.setExternalStoragePath(File.separator + "sdcard" + File.separator + "BearingData" + File.separator + "DataStore" + File.separator);
        bearingCreate = new Create(bearingHandler);
        bearingRetrieve = new Retrieve(bearingHandler);
        bearingUpdate = new Update(bearingHandler);
        bearingUpload = new Upload(bearingHandler);
    }

    private void startBearingHandler(BearingHandler bearingHandler) {
        bearingHandler.start();
    }

    private void startAlgorithmHandler(AlgorithmLifeCycleHandler algorithmLifeCycleHandler) {
        algorithmLifeCycleHandler.start();
    }

    /**
     * Gets instance.
     *
     * @param appContext the app context
     * @return the instance
     */
    public static synchronized BearingTrainer getInstance(Context appContext) {
        if (bearingTrain == null)
            bearingTrain = new BearingTrainer(appContext);
        return bearingTrain;
    }

    /**
     * Create int.
     *
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingData     the bearing data
     * @param syncWithServer  the boolean to trigger cell hierarchy on the server.
     * @param bearingCallBack the bearing call back
     * @return the int <p> Creation of Bearing site , location and cell hierarchy. The syncWithServer is limited only to cell hierarchy creation . Cell hierarchy is supported on both the device and on the server. Refer CREATE for complete documentation.
     */


    public int create(@NonNull BearingConfiguration bearingConfiguration, @NonNull BearingData bearingData, @NonNull boolean syncWithServer, @NonNull BearingCallBack bearingCallBack) {

        if (Validation.validateCreateInput(bearingConfiguration, bearingData)) {
            final UUID requestID = UUID.randomUUID();

            for (BearingConfiguration.Approach bearingApproach : BearingRequestParser.parseConfigurationForApproachList(bearingConfiguration)) {
                BearingMode bearingMode = BearingRequestParser.getBearingModeForTraining(bearingData);
                if (bearingMode == null) {
                    return Codes.BAD_REQUEST;
                }
                switch (bearingMode) {
                    case SITE:
                        return bearingCreate.triggerSiteCreation(requestID.toString(), bearingConfiguration, bearingData, bearingApproach, syncWithServer, bearingCallBack);
                    case LOCATION:
                        return bearingCreate.triggerLocationCreation(requestID.toString(), bearingConfiguration, bearingData, bearingApproach, syncWithServer, bearingCallBack);
                    default:
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "create: Not a valid bearingData mode in location ");
                        return Codes.BAD_REQUEST;
                }
            }

        }
        return Codes.BAD_REQUEST;
    }


    /**
     * Retrieve bearing output.
     *
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingData     the bearing data
     * @param syncWithServer  the sync with server
     * @param bearingCallback the bearing callback
     * @return the bearing output
     * @parms bearingData : data container for all read operation. BearingData contains specific information whether SiteList , or location :List needs to be downloaded.
     * @parms syncWithServer : this boolean is used to validate if the data on the local storage needs to be in with server . If miss match is found, the local is overridden.
     * @parms bearingCallBack : callback object -indicating synchronicity or asynchronicity. <p> Retrieve is implemented : 1. GET ALL SITE NAMES 2. GET ALL LOCATION NAMES 3. GET SCAN RESULTS FOR SPECIFIC SENSOR Combination               (ONLY 2 SUPPORTED) 4. RETRIEVE WILL SUPPORT SNAPSHOT RETRIEVAL FOR A SPECIFIC SITE   (ONLY 4 SUPPORTED).
     */
    public BearingOutput retrieve(BearingConfiguration bearingConfiguration, BearingData bearingData, @NonNull boolean syncWithServer, BearingCallBack bearingCallback) {


        if (bearingCallback == null) {
            return bearingRetrieve.synchronousResponse(syncWithServer, bearingConfiguration, bearingData);
        } else {
            bearingRetrieve.asynchronousResponse(syncWithServer, bearingConfiguration, bearingData, bearingCallback);
            return null;
        }
    }

    /**
     * Update boolean.
     *
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingDataOld  the bearing data old
     * @param bearingDataNew  the bearing data new
     * @param syncWithServer  the sync with server
     * @param bearingCallBack the bearing call back
     * @return the boolean
     * @parms bearingData : data container for all update operation. BearingData contains specific information with the site name to rename and also signal values to merge.
     * @parms syncWithServer : this boolean is used to validate if the data on the local storage needs to be in with server . If miss match is found, the local is overridden.
     * @parms bearingCallBack : callback object -indicating synchronicity or asynchronicity. <p> The update API is designed to update : 1. SITE RENAME ON LOCAL AND SERVER(based on syncWithServer boolean) 2. MERGE SIGNALS WITH EXISTING SIGNALS(on local device)
     */


    public boolean update(BearingConfiguration bearingConfiguration, BearingData bearingDataOld, BearingData bearingDataNew, boolean syncWithServer, BearingCallBack bearingCallBack) {

        final BearingConfiguration.OperationType operationType = BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration);

        if (bearingCallBack == null) {
            return bearingUpdate.synchronousResponse(syncWithServer, bearingDataOld, bearingDataNew, operationType);
        } else {
            bearingUpdate.asynchronousResponse(syncWithServer, bearingDataOld, bearingDataNew, operationType, bearingCallBack);
        }

        return false;
    }

    /**
     * The upload api will help user to set server endpoint, upload Site and location data explicitly . This api is kept independant so that it will cater to all upload server operations.
     * <p>
     * 1. Edit and change the serverUpload url
     * 2. Upload site data
     * 3. Upload specific location or all locations.
     * <p>
     * The response can be synchronous or asynchronous.
     *
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingData     the bearing data
     * @param bearingCallBack the bearing call back
     * @return the boolean
     */
    public boolean upload(BearingConfiguration bearingConfiguration, BearingData bearingData, BearingCallBack bearingCallBack) {

        if (bearingCallBack == null) {
            return bearingUpload.synchronousResponse(bearingConfiguration, bearingData);

        } else {
            bearingUpload.asynchronousResponse(bearingConfiguration, bearingData, bearingCallBack);
            return true;
        }
    }


}
