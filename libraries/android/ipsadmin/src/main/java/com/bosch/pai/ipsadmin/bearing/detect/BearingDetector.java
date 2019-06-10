package com.bosch.pai.ipsadmin.bearing.detect;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.detect.errorcode.Constants;
import com.bosch.pai.ipsadmin.bearing.detect.operations.Detection;
import com.bosch.pai.ipsadmin.bearing.detect.operations.Read;
import com.bosch.pai.ipsadmin.bearing.detect.operations.Validation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.persistence.util.Util;
import com.bosch.pai.ipsadmin.comms.exception.CertificateLoadException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * The type Bearing detector.
 */
public final class BearingDetector {
    private final Logger logger = LoggerFactory.getLogger(BearingDetector.class);
    private static final String TAG = BearingDetector.class.getSimpleName();
    private static BearingDetector bearingDetector;
    private BearingHandler bearingHandler;
    private Detection bearingDetection;
    private Read bearingRead;
    private Validation validation;

    private BearingDetector(final Context appContext) {
        /*Adding the processing in a thread as it was found that any handler focked by the application was running on the BearingHandler thread.
         The fix of creating a new thread has found to solve this problem
         -A latch mechanism has been added to check that bearing should proceed only after Complete initialisation will happen.
         */

        final CountDownLatch latch = new CountDownLatch(1);

        Thread bearingDetectorThread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    logger.error(TAG, "Certificate load exception", e);
                }
                Util.setInternalStoragePath(appContext.getFilesDir().getPath() + File.separator + "DataStore" + File.separator);
                Util.setExternalStoragePath(File.separator + "sdcard" + File.separator + "BearingData" + File.separator + "DataStore" + File.separator);
                bearingHandler = BearingHandler.getInstance();
                bearingDetection = new Detection(bearingHandler);
                bearingRead = new Read(bearingHandler);

                if (!bearingHandler.isAlive()) {
                    bearingHandler.start();
                }
                if (!AlgorithmLifeCycleHandler.getInstance().isAlive()) {
                    AlgorithmLifeCycleHandler.getInstance().start();
                }

                latch.countDown();
            }
        });
        startBearingDetectorThread(bearingDetectorThread);
        try {
            latch.await();
        } catch (InterruptedException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Error: " + e);
            Thread.currentThread().interrupt();
        }

        validation = new Validation();
    }

    private void startBearingDetectorThread(Thread thread) {
        thread.start();
    }

    /**
     * Gets instance.
     *
     * @param appContext the app context
     * @return the instance
     */
    public static BearingDetector getInstance(Context appContext) {
        if (bearingDetector != null) {
            return bearingDetector;
        }
        bearingDetector = new BearingDetector(appContext);
        return bearingDetector;
    }


    /**
     * The invoke API is used to start and stop bearing Detection.
     * The invoke API accepts:
     *
     * @param isStart              true to start detection and false to stop detection
     * @param bearingConfiguration bearing parameters to work with detection. including the operationType, Approach and sensorList each approach will accept.
     * @param bearingData          all input data bearing will accept as input.
     * @param bearingCallBack      detection responses returned from bearing
     * @return the int
     */
    public int invoke(boolean isStart, @NonNull BearingConfiguration bearingConfiguration, BearingData bearingData, BearingCallBack bearingCallBack) {
        if (BearingConfiguration.OperationType.STOP_DETECTION == BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration)) {
            bearingDetection.shutdown();
            return Constants.RESPONSE_OK;
        }
        if (!validation.isValidConfigurationRequest(bearingConfiguration)) {
            return Constants.BAD_REQUEST;
        }
        if (isStart) {
            bearingDetection.invokeStartBearing(bearingConfiguration, bearingData, bearingCallBack);
        } else {
            bearingDetection.invokeStopBearing(bearingConfiguration);
        }
        return Constants.RESPONSE_OK;
    }

    /**
     * Read bearing output.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param bearingData          the bearing data
     * @param syncWithServer       the sync with server
     * @param bearingCallback      the bearing callback
     * @return the bearing output
     * @parms bearingData : data container for all read operation. BearingData contains specific information whether SiteList , or location :List needs to be downloaded.
     * @parms syncWithServer : this boolean is used to validate if the data on the local storage needs to be in with server . If miss match is found, the local is overridden.
     * @parms bearingCallBack : callback object -indicating synchronicity or asynchronicity. <p> The read api is designed to handle 4 scenarios: 1. Sync with server : <b>TRUE</b> and Callback :<b>NONNULL</b> 2. Sync with server :<b>FALSE</b> and Callback :<b>NONNULL</b> 3. Sync with server :<b>TRUE</b> and Callback :<b>NULL</b> 4. Sync with server :<b>FALSE</b> and Callback :<b>NULL</b> <p> <p> 1. & 2. are synchronous API calls as they don't have callback 3. & 4. are asynchronous API calls as they have callback
     */
    public BearingOutput read(BearingConfiguration bearingConfiguration, BearingData bearingData, @NonNull boolean syncWithServer, BearingCallBack bearingCallback) {

        if (bearingCallback == null) {
            return bearingRead.synchronousResponseReadOperation(bearingConfiguration, syncWithServer, bearingData);
        } else {
            bearingRead.asynchronousResponseReadOperation(syncWithServer, bearingConfiguration, bearingData, bearingCallback);
            return null;
        }
    }


    /**
     * Download boolean.
     *
     * @param bearingConfiguration the bearing configuration
     * @param bearingData          the bearing data
     * @param bearingCallBack      the bearing call back
     * @return the boolean
     */
    public boolean download(@NonNull BearingConfiguration bearingConfiguration, BearingData bearingData, BearingCallBack bearingCallBack) {
        if (BearingConfiguration.OperationType.READ_SOURCE_ID_MAP == bearingConfiguration.getOperationType()) {
            bearingRead.downloadSourceIdMap(bearingCallBack);
            return true;
        } else if (BearingConfiguration.OperationType.DOWNLOAD_SITE_THRESH_DATA == bearingConfiguration.getOperationType()) {
            bearingRead.downloadSiteThreshData(bearingData.getSiteMetaData().getSiteName(), bearingCallBack);
            return true;
        }
        return false;
    }
}
