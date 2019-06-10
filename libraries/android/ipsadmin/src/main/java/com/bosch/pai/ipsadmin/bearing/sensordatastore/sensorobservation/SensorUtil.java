package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation;


import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;

import java.util.Objects;

public class SensorUtil {

    private SensorUtil() {
    }

    private static boolean scanForBLEMac = false;
    private static boolean shutdown = false;

    public static void setScanForBLEMac(boolean flag) {
        scanForBLEMac = flag;
    }

    public static boolean isScanForBLEMac() {
        return scanForBLEMac;
    }

    public static boolean isShutDown() {
        return shutdown;
    }

    public static void setShutdown(boolean shutdown) {
        SensorUtil.shutdown = shutdown;
    }
    public static boolean checkAreWIFISensorsEnabled(Context context) {
        try {
            final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final boolean isAlwaysEnabled = Objects.requireNonNull(wifiManager).isScanAlwaysAvailable();
            final boolean isWifiEnabled = wifiManager.isWifiEnabled();
            boolean isLocationEnabled;
            final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                isLocationEnabled = Objects.requireNonNull(lm).isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, "", "checkSensorStatus: ", ex);
                isLocationEnabled = false;
            }
            return validateSensorsWithRuleBook(isAlwaysEnabled, isWifiEnabled, isLocationEnabled);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static boolean validateSensorsWithRuleBook(boolean isAlwaysScanEnabled, boolean isWifiEnabled, boolean isLocationEnabled) {
        boolean sensorEnabled = false;
        for (ConfigurationSettings.SENSOR_RULEBOOK sensor_rulebook : ConfigurationSettings.SENSOR_RULEBOOK.values()) {
            sensorEnabled = sensorEnabled || validateSensorWithRule(sensor_rulebook, isAlwaysScanEnabled, isWifiEnabled, isLocationEnabled);
        }
        return sensorEnabled;
    }

    private static boolean validateSensorWithRule(ConfigurationSettings.SENSOR_RULEBOOK rulebook_rule, boolean isAlwaysEnabled, boolean isWifiEnabled, boolean isLocationEnabled) {
        return rulebook_rule.getAlwaysScanAvailable() == isAlwaysEnabled &&
                rulebook_rule.getIsWifiEnabled() == isWifiEnabled &&
                rulebook_rule.getLocationEnabled() == isLocationEnabled;
    }

    public static boolean checkAreBLESenorsEnabled(Context context) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        return bluetoothManager.getAdapter().isEnabled();
    }
}
