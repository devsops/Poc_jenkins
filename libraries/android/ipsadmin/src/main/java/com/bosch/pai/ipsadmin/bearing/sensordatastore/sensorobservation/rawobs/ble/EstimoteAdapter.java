package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.estimote.coresdk.observation.region.Region;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.recognition.packets.Nearable;
import com.estimote.coresdk.service.BeaconManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * The type Estimote adapter.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

public class EstimoteAdapter {
    private static final String TAG = EstimoteAdapter.class.getName();

    private BluetoothAdapter bluetoothAdapter;
    private boolean isScanning = false;
    private boolean isAdapterEnabled = false;
    private final ResourceDataManager resourceDataManager;
    private final Context context;
    private final List<String> deviceAddressList;
    private final List<Integer> deviceRssiList;
    private BeaconManager beaconManager;
    private static final BeaconRegion ALL_BEACONS = new BeaconRegion("rid", null, null, null);
    private String scanId;
    private SensorObservationHandler sensorObservationHandler;

    /**
     * Instantiates a new Estimote adapter.
     *
     * @param applicationContext  the application context
     * @param resourceDataManager the resource data manager
     */
    public EstimoteAdapter(Context applicationContext, ResourceDataManager resourceDataManager) {
        this.resourceDataManager = resourceDataManager;
        this.context = applicationContext;
        this.deviceAddressList = new LinkedList<>();
        this.deviceRssiList = new LinkedList<>();

        if (beaconManager == null) {
            final Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    beaconManager = new BeaconManager(context);
                    setUpBeaconListener();
                }
            });
        }
    }

    private void setUpBeaconListener() {
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {


                for (Beacon beacon : list) {
                    UUID beaconUuid = beacon.getProximityUUID();
                    int rssi = beacon.getRssi();
                    String sourceId;
                    if(SensorUtil.isScanForBLEMac()) {
                        sourceId = beacon.getMacAddress().toStandardString();
                    } else {
                        sourceId = beaconUuid + "_" + beacon.getMajor() + "_" + beacon.getMinor();
                    }
                    boolean isAddrPresent = false;

                    for (String devAddr : deviceAddressList) {
                        if (devAddr.trim().equals(sourceId)) {
                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Device already present");
                            isAddrPresent = true;
                            break;
                        }
                    }

                    if (!isAddrPresent) {
                        deviceAddressList.add(sourceId);
                        deviceRssiList.add(rssi);
                    }

                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "MAC:" + beacon.getMacAddress() + "UUID: " + beaconUuid + " Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor());
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Device Count " + deviceAddressList.size());
                    Calendar cal = Calendar.getInstance();
                    TimeZone tz = cal.getTimeZone();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                    sdf.setTimeZone(tz);
                    final String localTime = sdf.format(new Date());
                    //LogData logData = new LogData(localTime, sourceId, String.valueOf(beacon.getRssi()), beacon.toString());
                    //logData.setSensorLogType(LogData.Type.BLE);
                    //FileLogger.INSTANCE.writeLogSnapShot(logData);


                }

                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Sent beacons detected event");


            }
        });
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.INFO, TAG, list.toString());
            }
        });
    }

    /**
     * Sets estimote.
     *
     * @param sensorObservationHandler the sensor observation handler
     * @return the estimote
     */
    public boolean setupEstimote(SensorObservationHandler sensorObservationHandler) {
        this.sensorObservationHandler = sensorObservationHandler;
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setupUpEstimote");

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null) {
            isAdapterEnabled = bluetoothAdapter.isEnabled();
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter not Null, isEnabled for estimote: " + isAdapterEnabled);
            return isAdapterEnabled;
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter NULL for estimote!");
            return false;
        }
    }


    /**
     * Start.
     */
    public void start() {

        deviceAddressList.clear();
        deviceRssiList.clear();

        final int bleScanTimeOut = Double.valueOf(ConfigurationSettings.getConfiguration().getSensorPreferences().getProperty(Property.Sensor.BLE_SCAN_TIMEOUT).toString()).intValue();
        if (sensorObservationHandler != null) {
            sensorObservationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            }, bleScanTimeOut);
        }

        if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled())) {
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.startNearableDiscovery();
                    beaconManager.startRanging(ALL_BEACONS);
                }
            });
            isScanning = true;
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Estimote Scan started");
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter NULL or Not Enabled for estimote scan");
        }
    }


    /**
     * Stop.
     */
    public void stop() {

        if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled())) {

            try {
                beaconManager.stopNearableDiscovery();
                beaconManager.stopRanging(ALL_BEACONS);
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Reporting results to callback");
                if (resourceDataManager != null) {
                    final List<SnapshotObservation> snapshotObservationEstimote = RawSnapshotConvertor.createSnapshotObservationforBLE(deviceAddressList, deviceRssiList);
                    resourceDataManager.onResponseReceived(snapshotObservationEstimote);
                }
            } catch (AbstractMethodError e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "AbstractMethodError: " + e.toString());
            } catch (Exception e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Callback Error: " + e.toString());
            }

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "BLE Scan stopped");
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Adapter NULL or Not Enabled");
        }

        isScanning = false;


    }


    /**
     * Is scanning boolean.
     *
     * @return the boolean
     */
    public boolean isScanning() {
        return isScanning;
    }


}