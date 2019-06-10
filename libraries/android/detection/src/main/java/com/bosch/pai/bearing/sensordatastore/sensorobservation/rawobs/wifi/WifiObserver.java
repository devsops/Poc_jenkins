package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.benchmark.bearinglogger.profiling.ResourceProfiler;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.logger.Logger;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import java.util.List;

/**
 * The type Wifi observer.
 */
public class WifiObserver {
    /**
     * The Filter wifi.
     */
    private IntentFilter filterWifi;
    private WifiManager wifiManager;
    private List<ScanResult> scanResult;
    private final Context context;

    private static final String TAG = WifiObserver.class.getName();
    private WifiReceiver wifiReceiver;

    private final ResourceDataManager resourceDataManager;
    private Logger logger;

    /**
     * Instantiates a new Wifi observer.
     *
     * @param context             the context
     * @param resourceDataManager the resource data manager
     */
    public WifiObserver(Context context, ResourceDataManager resourceDataManager) {
        this.context = context;
        this.resourceDataManager = resourceDataManager;
        logger = new Logger("WifiLogs");
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

        wifiReceiver = new WifiReceiver(resourceDataManager, wifiManager, logger);
    }

    /*Wifi sensors follow 3 use case :
    * 1. WIFI , LOCATION AND ALWAYS ON ENABLED
    * 2. WIFI , LOCATION  BOTH DISABLED, ALWAYS SCAN ENABLED,
    * 3. ONLY WIFI DISABLED
    * ........................................................
    *
    * Android above API - 18 has introduced isScanAlwaysAvailable . This will enable a device to get scan results even when Wifi and location are disabled.
    * NOTE: As bearing library is designed to work with API- 19 above, always scan is checked.
    *
    *
    *
    *
    * */


    /**
     * Start scanning boolean.
     *
     * @return the boolean
     */
    public boolean startScanning() {
        new ResourceProfiler().writeDeviceInfo(context);
        boolean scanStarted = wifiManager.startScan();
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "startScanning: ");
        return scanStarted;
    }

    /**
     * Sets filter wifi.
     *
     * @param sensorObservationHandler the sensor observation handler
     * @return the filter wifi
     */
    public boolean setupFilterWifi(SensorObservationHandler sensorObservationHandler) {
        filterWifi = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        if (wifiManager.isWifiEnabled() || wifiManager.isScanAlwaysAvailable()) {
            context.registerReceiver(wifiReceiver, filterWifi, "try", sensorObservationHandler);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Teardown filter wifi.
     */
    public void teardownFilterWifi() {
        try {
            context.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, " Wifi Receiver already unregistered!!!");
        }
    }

}
