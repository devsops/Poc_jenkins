package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs;

import android.net.wifi.ScanResult;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.filter.KalmanFilter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The type Raw snapshot convertor.
 */
public class RawSnapshotConvertor {
    private static final String RAW_TAG = RawSnapshotConvertor.class.getName();
    private static Map<String, DescriptiveStatistics> statisticsMap = new HashMap<>();
    private static Map<String, KalmanFilter> kalmanFilterMap = new HashMap<>();

    /**
     * Create snapshot observationfor wifi list.
     *
     * @param wifiScanResult the wifi scan result
     * @return the list
     */
/*SnapshotObservation List Creation for wifi from List<scanResult> */
    public static List<SnapshotObservation> createSnapshotObservationforWIFI(List<ScanResult> wifiScanResult) {

        //LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, RAW_TAG, wifiScanResult.toString());
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, RAW_TAG, "");
        //List used to maintain the uniqueness in the obtained mac address's
        final List<String> tempMacAddressList = new ArrayList<>();
        final List<SnapshotObservation> snapshotObjectList = new ArrayList<>();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotItem> snapshotItemList = new ArrayList<>();
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.INTERMEDIATE);
        for (ScanResult wifiObject : wifiScanResult) {
            final String tempMacAddress = wifiObject.BSSID.trim();
            if (!tempMacAddressList.contains(tempMacAddress)) {
                tempMacAddressList.add(tempMacAddress);
                SnapshotItem snapshotItem = createSnapItemObjectforWIFI(wifiObject);
                snapshotItemList.add(snapshotItem);
            }
        }
        snapshotObservation.setSnapShotItemList(snapshotItemList);
        snapshotObjectList.add(snapshotObservation);

        return snapshotObjectList;
    }

    private static SnapshotItem createSnapItemObjectforWIFI(ScanResult scanResult) {
        final SnapshotItem snapshotItem = new SnapshotItem();
        final double[] measuredValue = new double[1];
        final String[] customField = new String[1];
        String apIdentifier = scanResult.BSSID + "-" + scanResult.SSID;
        measuredValue[0] = scanResult.level;
        ConfigurationSettings settings = ConfigurationSettings.getConfiguration();
        if (settings.isRollingAverageForWifi()) {
            if (!statisticsMap.containsKey(apIdentifier)) {
                DescriptiveStatistics ds = new DescriptiveStatistics(settings.getRollingAverageWindowSize());
                ds.addValue(measuredValue[0]);
                measuredValue[0] = ds.getMean();
                statisticsMap.put(apIdentifier, ds);
            } else {
                DescriptiveStatistics ds = statisticsMap.get(apIdentifier);
                ds.addValue(measuredValue[0]);
                measuredValue[0] = ds.getMean();
            }
        }
        if (settings.isKalmanSmoother()) {
            if (!kalmanFilterMap.containsKey(apIdentifier)) {
                KalmanFilter kf = new KalmanFilter(0.008, 3.0, 1.0, 0.0, 1.0);
                measuredValue[0] = kf.filter(measuredValue[0], 0.0);
                kalmanFilterMap.put(apIdentifier, kf);
            } else {
                KalmanFilter kf = kalmanFilterMap.get(apIdentifier);
                measuredValue[0] = kf.filter(measuredValue[0], 0.0);
            }
        }
        snapshotItem.setSourceId(scanResult.BSSID);
        customField[0] = scanResult.SSID;
        snapshotItem.setCustomField(customField);
        snapshotItem.setMeasuredValues(measuredValue);

        return snapshotItem;
    }

    /**
     * Create snapshot observationfor ble list.
     *
     * @param addressList the address list
     * @param rssiList    the rssi list
     * @return the list
     */
/*SnapshotObservation List Creation for BLE from List<addressList> and List<rssiList> */
    public static List<SnapshotObservation> createSnapshotObservationforBLE(List<String> addressList, List<Integer> rssiList) {
        final List<SnapshotObservation> snapshotObjectList = new ArrayList<>();
        final SnapshotObservation writerObj = new SnapshotObservation();
        final List<SnapshotItem> snapShotItemArray = new ArrayList<>();
        writerObj.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        writerObj.setDetectionLevel(BearingConfiguration.DetectionLevel.MICRO);

        Iterator<String> iterator1 = addressList.iterator();
        Iterator<Integer> iterator2 = rssiList.iterator();

        while (iterator1.hasNext() && iterator2.hasNext()) {
            String bleAddress = iterator1.next();
            Integer bleRssi = iterator2.next();

            SnapshotItem snapshotItem = createSnapItemObjectforBLE(bleAddress, bleRssi);
            snapShotItemArray.add(snapshotItem);
        }
        writerObj.setSnapShotItemList(snapShotItemArray);
        snapshotObjectList.add(writerObj);

        return snapshotObjectList;
    }

    private static SnapshotItem createSnapItemObjectforBLE(String bleAddress, Integer bleRssi) {
        final SnapshotItem snapshotItem = new SnapshotItem();
        final double[] measuredValue = new double[1];
        final String[] customField = new String[1];
        snapshotItem.setSourceId(bleAddress);
        measuredValue[0] = bleRssi;
        snapshotItem.setMeasuredValues(measuredValue);
        snapshotItem.setCustomField(customField);

        return snapshotItem;
    }

    /**
     * Create snapshot observationfor gps list.
     *
     * @param latitude   the latitude
     * @param longitude  the longitude
     * @param deviceName the device name
     * @return the list
     */
/* SnapshotObservation List Creation for GPS from Latitude and Longitude strings and radius set to 1.5KM */
    public static List<SnapshotObservation> createSnapshotObservationforGPS(double latitude, double longitude, String deviceName) {
        final List<SnapshotObservation> snapshotObservationList = new ArrayList<>();
        final SnapshotObservation writeObject = new SnapshotObservation();
        final List<SnapshotItem> snapshotItemList = new ArrayList<>();
        final SnapshotItem snapshotItem = new SnapshotItem();
        final String[] customField = new String[1];
        writeObject.setSensorType(BearingConfiguration.SensorType.ST_GPS);
        writeObject.setDetectionLevel(BearingConfiguration.DetectionLevel.MACRO);
        snapshotItem.setSourceId(deviceName);
        snapshotItem.setMeasuredValues(new double[]{latitude, longitude});
        snapshotItem.setCustomField(customField);
        snapshotItemList.add(snapshotItem);
        writeObject.setSnapShotItemList(snapshotItemList);
        snapshotObservationList.add(writeObject);

        return snapshotObservationList;
    }

    /**
     * Create snapshot observation magneto list.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the list
     */
    public static List<SnapshotObservation> createSnapshotObservationMagneto(double x, double y, double z) {

        final List<SnapshotObservation> snapshotObservationList = new ArrayList<>();
        final SnapshotObservation writeObject = new SnapshotObservation();
        final List<SnapshotItem> snapshotItemList = new ArrayList<>();

        writeObject.setSensorType(BearingConfiguration.SensorType.ST_MAGNETO);
        writeObject.setDetectionLevel(BearingConfiguration.DetectionLevel.MICRO);
        writeObject.setSensorType(BearingConfiguration.SensorType.ST_MAGNETO);
        writeObject.setDetectionLevel(BearingConfiguration.DetectionLevel.MICRO);
        final SnapshotItem xVal = createSnapshotItemsForSensor("X", x);
        snapshotItemList.add(xVal);
        final SnapshotItem yVal = createSnapshotItemsForSensor("Y", y);
        snapshotItemList.add(yVal);
        final SnapshotItem zVal = createSnapshotItemsForSensor("Z", z);
        snapshotItemList.add(zVal);
        writeObject.setSnapShotItemList(snapshotItemList);
        snapshotObservationList.add(writeObject);

        return snapshotObservationList;
    }


    private static SnapshotItem createSnapshotItemsForSensor(String label, double val) {

        SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setSourceId(label);
        snapshotItem.setMeasuredValues(new double[]{val});
        snapshotItem.setCustomField(null);
        return snapshotItem;
    }

    /**
     * Create snapshot observation for geofence list.
     *
     * @param message the message
     * @return the list
     */
    public static List<SnapshotObservation> createSnapshotObservationForGeofence(String message) {

        final List<SnapshotObservation> snapshotObservationList = new ArrayList<>();
        final SnapshotObservation geofenceObject = new SnapshotObservation();
        final List<SnapshotItem> snapshotItemList = new ArrayList<>();
        final SnapshotItem snapshotItem = new SnapshotItem();
        final String[] customField = new String[1];
        customField[0] = message;
        geofenceObject.setSensorType(BearingConfiguration.SensorType.ST_GEOFENCE);
        geofenceObject.setDetectionLevel(BearingConfiguration.DetectionLevel.MACRO);
        snapshotItem.setCustomField(customField);
        snapshotItemList.add(snapshotItem);
        geofenceObject.setSnapShotItemList(snapshotItemList);
        snapshotObservationList.add(geofenceObject);
        return snapshotObservationList;
    }

    /**
     * Create snapshot observation imu list.
     *
     * @param value the value
     * @return the list
     */
    public static List<SnapshotObservation> createSnapshotObservationIMU(int value) {

        final List<SnapshotObservation> snapshotObservationList = new ArrayList<>();
        final SnapshotObservation writeObject = new SnapshotObservation();
        final List<SnapshotItem> snapshotItemList = new ArrayList<>();

        writeObject.setSensorType(BearingConfiguration.SensorType.ST_IMU);
        writeObject.setDetectionLevel(BearingConfiguration.DetectionLevel.MICRO);
        final SnapshotItem step = createSnapshotItemsForSensor("STEP", value);
        snapshotItemList.add(step);
        writeObject.setSnapShotItemList(snapshotItemList);
        snapshotObservationList.add(writeObject);

        return snapshotObservationList;
    }


}
