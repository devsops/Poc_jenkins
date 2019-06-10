package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs;

import android.content.Context;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.RequestResponseHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorInfo;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ble.BleObserver;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ble.EstimoteAdapter;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.gps.GPSObserver;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.imu.IMUObserver;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.magneto.MagnetoObserver;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.wifi.WifiObserver;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.imu.IMUObserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration.SensorType.ST_WIFI;


/**
 * The type Resource data manager.
 */
public class ResourceDataManager {
    private static final String TAG = ResourceDataManager.class.getSimpleName();
    private final Map<BearingConfiguration.SensorType, List<SnapshotObservation>> sensortoLatestestDataMap = new HashMap<>();
    private final RequestResponseHandler responseHandler;
    private WifiObserver wifiObserver;
    private EstimoteAdapter estimoteAdapter;
    private GPSObserver gpsObserver;
    private MagnetoObserver magnetoObserver;
    //private BleObserver bleObserver;
    private IMUObserver imuObserver;
    private final ResourceStateManager resourceStateManager;
    private List<SensorInfo> sensorList;
    private SensorObservationHandler sensorObservationHandler;

    /**
     * Instantiates a new Resource data manager.
     *
     * @param responseHandler          the response handler
     * @param resourceStateManager     the resource state manager
     * @param sensorObservationHandler the sensor observation handler
     */
    public ResourceDataManager(RequestResponseHandler responseHandler, ResourceStateManager resourceStateManager, SensorObservationHandler sensorObservationHandler) {

        /*Create a list of sensors ,This list is used to pass additional info for each sensor*/
        sensorList = new ArrayList<>();
        final ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration();
        final boolean wifiActiveMode = configurationSettings.getSensorPreferences().getProperty(Property.Sensor.WIFI_ACTIVE_MODE).toString().equals(ConfigurationSettings.ActiveMode.RESPONSIVE.toString());
        final boolean bleActiveMode = configurationSettings.getSensorPreferences().getProperty(Property.Sensor.BLE_ACTIVE_MODE).toString().equals(ConfigurationSettings.ActiveMode.RESPONSIVE.toString());
        final long wifiScanInterval = Double.valueOf(configurationSettings.getSensorPreferences().getProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL).toString()).longValue();
        final long bleScanInterval = Double.valueOf(configurationSettings.getSensorPreferences().getProperty(Property.Sensor.BLE_ACTIVE_MODE_INTERVAL).toString()).longValue();
        final SensorInfo sensorInfoWifi = new SensorInfo(ST_WIFI, wifiActiveMode, wifiScanInterval);
        final SensorInfo sensorInfoBle = new SensorInfo(BearingConfiguration.SensorType.ST_BLE, bleActiveMode, bleScanInterval);
        final SensorInfo sensorInfoGps = new SensorInfo(BearingConfiguration.SensorType.ST_GPS, false, 0);
        final SensorInfo sensorInfoMagneto = new SensorInfo(BearingConfiguration.SensorType.ST_MAGNETO, false, wifiScanInterval);
        final SensorInfo sensorInfoIMU = new SensorInfo(BearingConfiguration.SensorType.ST_IMU, false, wifiScanInterval);

        sensorList.add(sensorInfoWifi);
        sensorList.add(sensorInfoBle);
        sensorList.add(sensorInfoGps);
        sensorList.add(sensorInfoMagneto);
        sensorList.add(sensorInfoIMU);

        this.sensorObservationHandler = sensorObservationHandler;
        this.responseHandler = responseHandler;
        this.resourceStateManager = resourceStateManager;
        responseHandler.registerListener(this);
        resourceStateManager.registerListener(this, sensorList);

    }


    /**
     * Modify sensor configuration.
     *
     * @param sensorType      the sensor type
     * @param isResponseBased the is response based
     * @param scanInterval    the scan interval
     */
/*Method will allow the user to change the sensor settings as per user preference*/
    public void modifySensorConfiguration(BearingConfiguration.SensorType sensorType, boolean isResponseBased, long scanInterval) {
        for (SensorInfo sensor : sensorList) {
            if (sensorType == sensor.getSensorType()) {
                sensor.setScanInterval(scanInterval);
                sensor.setResponseBased(isResponseBased);
            }
        }

    }

    /**
     * On response received.
     *
     * @param snapshotObservations the snapshot observations
     */
/*On response the state for the sensor is updated and the latest copy is held by the data manager*/
    public void onResponseReceived(List<SnapshotObservation> snapshotObservations) {

        //  List<SnapshotObservation> filteredObservations = filerForTesting(snapshotObservations);

        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            switch (snapshotObservation.getSensorType()) {
                case ST_WIFI:
                    sensortoLatestestDataMap.put(ST_WIFI, snapshotObservations);
                    resourceStateManager.notifySensorStateOnStopScan(ST_WIFI);
                    responseHandler.notifyObservationAndUpdate(ST_WIFI);
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onResponseReceived: WIFI RECEIVED;");
                    break;
                case ST_BLE:
                    sensortoLatestestDataMap.put(BearingConfiguration.SensorType.ST_BLE, snapshotObservations);
                    resourceStateManager.notifySensorStateOnStopScan(BearingConfiguration.SensorType.ST_BLE);
                    responseHandler.notifyObservationAndUpdate(BearingConfiguration.SensorType.ST_BLE);
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onResponseReceived: BLE RECEIVED;");
                    break;
                case ST_GPS:
                    sensortoLatestestDataMap.put(BearingConfiguration.SensorType.ST_GPS, snapshotObservations);
                    resourceStateManager.notifySensorStateOnStopScan(BearingConfiguration.SensorType.ST_GPS);
                    responseHandler.notifyObservationAndUpdate(BearingConfiguration.SensorType.ST_GPS);
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onResponseReceived: GPS RECEIVED;");
                    break;
                case ST_MAGNETO:
                    sensortoLatestestDataMap.put(BearingConfiguration.SensorType.ST_MAGNETO, snapshotObservations);
                    resourceStateManager.notifySensorStateOnStopScan(BearingConfiguration.SensorType.ST_MAGNETO);
                    responseHandler.notifyObservationAndUpdate(BearingConfiguration.SensorType.ST_MAGNETO);
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onResponseReceived: MAGNETO RECEIVED");
                    break;
                case ST_IMU:
                    sensortoLatestestDataMap.put(BearingConfiguration.SensorType.ST_IMU, snapshotObservations);
                    resourceStateManager.notifySensorStateOnStopScan(BearingConfiguration.SensorType.ST_IMU);
                    responseHandler.notifyObservationAndUpdate(BearingConfiguration.SensorType.ST_IMU);
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onResponseReceived: IMU RECEIVED");
                    break;
                default:
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onResponseReceived: Default data for wifi ,ble,gps or all");
                    break;
            }
        }

    }

    /**
     * Scan sensor.
     *
     * @param sensorType the sensor type
     */
/*Start scan for corresponding and updates the state for each */
    public void scanSensor(BearingConfiguration.SensorType sensorType) {
        if(SensorUtil.isShutDown())
            return;
        switch (sensorType) {
            case ST_WIFI:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "initiate WiFi scan");
                if (wifiObserver != null) {

                    wifiObserver.startScanning();
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "scanSensor: scan starts" + new Date().getTime());
                } else
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "WifiObserver NULL, Could not initiate WiFi scan");
                break;
            case ST_BLE:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "initiate BLE scan");
                /*if (bleObserver != null) {
                    bleObserver.startScanning();
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "scanSensor: scan starts" + new Date().getTime());
                } else {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "bleObserver NULL: Could not initiate BLE scan ");
                }
                break;*/
                if (estimoteAdapter != null) {
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            estimoteAdapter.start();
                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "scanSensor: scan starts" + new Date().getTime());
                            timer.cancel();
                            timer.purge();
                        }
                    }, 1000);
                } else
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "estimote NULL: Could not initiate BLE scan ");
                break;
            case ST_GPS:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Initiating GPS scan");
                if (gpsObserver != null) {
                    gpsObserver.start();
                } else
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "GPS NULL, Could not initiate scan");

                break;
            case ST_MAGNETO:
                if (magnetoObserver != null) {
                    magnetoObserver.startScan();
                } else {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Magnetometer NULL, Could not initiate scan: ");
                }
                break;
            case ST_IMU:
                if (imuObserver != null) {
                    imuObserver.startScan();
                } else {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "IMU NULL, Could not initiate scan: ");
                }
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "restartScanForSensor: select sensor for rescan");
                break;
        }
    }


    /**
     * Terminate sensor.
     *
     * @param sensorType the sensor type
     */
    public void terminateSensor(BearingConfiguration.SensorType sensorType) {
        switch (sensorType) {
            case ST_WIFI:
                wifiObserver.teardownFilterWifi();
                break;
            case ST_BLE:
                /*if (bleObserver.isScanning()) {
                    bleObserver.stopScanning();
                }*/
                if (estimoteAdapter.isScanning()) {
                    estimoteAdapter.stop();
                }
                break;
            case ST_GPS:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "terminateSensor: No stop method for sensor+GPS");
                break;
            case ST_MAGNETO:
                magnetoObserver.stopScan();
                break;
            case ST_IMU:
                imuObserver.stopScan();
                break;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "terminate: select sensor for termination");
                break;
        }
    }


    /**
     * Sets up sensor.
     *
     * @param context    the context
     * @param sensorType the sensor type
     * @return the up sensor
     */
    public boolean setUpSensor(Context context, BearingConfiguration.SensorType sensorType) {

        switch (sensorType) {
            case ST_WIFI:
                if (wifiObserver == null)
                    wifiObserver = new WifiObserver(context, this);
                if (!wifiObserver.setupFilterWifi(sensorObservationHandler)) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "WiFi Utils NULL!");
                    wifiObserver = null;
                    return false;
                }
                resourceStateManager.createSensorState(BearingConfiguration.SensorType.ST_WIFI);  /*Sensor state created on adding sensor*/
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setUpSensor: WIFI Sensor setUp + stated added. Continue with scan");
                return true;
            case ST_BLE:

                /*if (bleObserver == null)
                    bleObserver = new BleObserver(context, this);
                if (!bleObserver.setupBle(sensorObservationHandler)) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "BLE Observer NULL");
                    bleObserver = null;
                    return false;
                }*/
                if (estimoteAdapter == null)
                    estimoteAdapter = new EstimoteAdapter(context, this);
                if (!estimoteAdapter.setupEstimote(sensorObservationHandler)) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Ble Utils NULL!");
                    estimoteAdapter = null;
                    return false;
                }
                resourceStateManager.createSensorState(BearingConfiguration.SensorType.ST_BLE);  /*Sensor state created on adding sensor*/

                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setUpSensor: BLE Sensor setUp + stated added. Continue with scan");
                return true;

            case ST_GPS:
                if (gpsObserver == null)
                    gpsObserver = new GPSObserver(context, this);
                if (!gpsObserver.setUpGPS()) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "GPS Observer NULL!");
                    gpsObserver = null;
                    return false;
                }
                resourceStateManager.createSensorState(BearingConfiguration.SensorType.ST_GPS);  /*Sensor state created on adding sensor*/

                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setUpSensor: GPS Sensor setUp + state added. Continue with scan");
                return true;

            case ST_MAGNETO:
                if (magnetoObserver == null)
                    magnetoObserver = new MagnetoObserver(context, this);
                if (!magnetoObserver.setUpMagneto(sensorObservationHandler)) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Magneto Observer NULL!");
                    magnetoObserver = null;
                    return false;
                }
                resourceStateManager.createSensorState(BearingConfiguration.SensorType.ST_MAGNETO);  /*Sensor state created on adding sensor*/
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setUpSensor: Magneto Sensor setUp + state added. Continue with scan");
                return true;
            case ST_IMU:
                if (imuObserver == null)
                    imuObserver = new IMUObserver(context, this);
                if (!imuObserver.setUpIMU(sensorObservationHandler)) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "IMU Observer NULL!");
                    imuObserver = null;
                    return false;
                }
                resourceStateManager.createSensorState(BearingConfiguration.SensorType.ST_IMU);                                  /*Sensor data capture happens on setUp*/
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setUpSensor: IMU Sensor setUp + state added. Continue with scan");
                return true;
            default:
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setUpSensor: select sensor for setup and add state");
        }
        return false;
    }

    /**
     * Gets updated data values.
     *
     * @param sensorType the sensor type
     * @return the updated data values
     */
/*Returns the updated value for each sensor type*/
    public List<SnapshotObservation> getUpdatedDataValues(BearingConfiguration.SensorType sensorType) {
        return sensortoLatestestDataMap.get(sensorType);
    }

    /*Method added to handle filtering*/
    private List<SnapshotObservation> filerForTesting(List<SnapshotObservation> snapshotObservations) {

        final List<SnapshotObservation> observations = new ArrayList<>();
        final List<SnapshotItem> tempList = new ArrayList<>();
        final SnapshotObservation filteredsnapshotObservation = new SnapshotObservation();
        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            BearingConfiguration.SensorType sensorType = snapshotObservation.getSensorType();
            List<SnapshotItem> snapshotTofilter = snapshotObservation.getSnapShotItemList();
            for (int i = 0; i < snapshotTofilter.size(); i++) {
                double[] measuredValues = snapshotTofilter.get(i).getMeasuredValues();
                double measuredRSSIvalue = measuredValues[0];
                if (Math.abs(measuredRSSIvalue) > 90) {

                } else {
                    tempList.add(snapshotTofilter.get(i));
                }
            }

            filteredsnapshotObservation.setSensorType(sensorType);
            filteredsnapshotObservation.setSnapShotItemList(tempList);
            observations.add(filteredsnapshotObservation);

        }
        return observations;

    }

}



