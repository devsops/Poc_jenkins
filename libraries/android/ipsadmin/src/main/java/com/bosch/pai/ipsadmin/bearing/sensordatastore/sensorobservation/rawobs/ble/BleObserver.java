package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * The type Ble observer.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BleObserver {
    private final String TAG = "[" + getClass().getSimpleName() + "]";

    private static BleObserver bleObserver;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner leScanner;
    private Context context;
    private List<String> deviceAddressList;
    private List<Integer> deviceRssiList;
    private SensorObservationHandler sensorObservationHandler;
    private boolean isScanning = false;
    private boolean isAdapterEnabled = false;
    private ResourceDataManager resourceDataManager = null;

 /*   public static BleObserver getInstance(Context context) {
        if (bleObserver == null)
            bleObserver = new BleObserver(context);
        return bleObserver;
    }*/

    /**
     * Instantiates a new Ble observer.
     *
     * @param context             the context
     * @param resourceDataManager the resource data manager
     */
    public BleObserver(Context context, ResourceDataManager resourceDataManager) {
        this.context = context;

        deviceAddressList = new LinkedList<>();
        deviceRssiList = new LinkedList<>();
        this.resourceDataManager = resourceDataManager;

        // handler = new Handler();
    }


    /**
     * Sets ble.
     *
     * @param sensorObservationHandler the sensor observation handler
     * @return the ble
     */
    public boolean setupBle(SensorObservationHandler sensorObservationHandler) {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "setupBle");
        this.sensorObservationHandler = sensorObservationHandler;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null) {
            isAdapterEnabled = bluetoothAdapter.isEnabled();
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter not Null, isEnabled: " + isAdapterEnabled);
            return isAdapterEnabled;
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter NULL!");
            return false;
        }
    }


    /**
     * Start scanning.
     */
    public void startScanning() {
        deviceAddressList.clear();
        deviceRssiList.clear();
        final int bleScanTimeOut = Double.valueOf(ConfigurationSettings.getConfiguration().getSensorPreferences().getProperty(Property.Sensor.BLE_SCAN_TIMEOUT).toString()).intValue();
        if (sensorObservationHandler != null) {
            sensorObservationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                }
            }, bleScanTimeOut);
        }

        if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled())) {
            leScanner = bluetoothAdapter.getBluetoothLeScanner();
            leScanner.startScan(mLeScanCallback);

            isScanning = true;

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "BLE Scan started");
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter NULL or Not Enabled");
        }
    }

    /**
     * Stop scanning.
     */
    public void stopScanning() {
        if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled())) {
            leScanner = bluetoothAdapter.getBluetoothLeScanner();
            leScanner.stopScan(mLeScanCallback);

            List<SnapshotObservation> snapshotObservationBLE = RawSnapshotConvertor.createSnapshotObservationforBLE(deviceAddressList, deviceRssiList);
            resourceDataManager.onResponseReceived(snapshotObservationBLE);

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "BLE Scan stopped");
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Adapter NULL or Not Enabled");
        }

        isScanning = false;
    }


    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            boolean isAddrPresent = false;

            for (String devAddr : deviceAddressList) {
                if (devAddr.trim().equals(result.getDevice().getAddress().trim())) {
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Device already present");
                    isAddrPresent = true;
                    break;
                }
            }

            if (!isAddrPresent) {
                deviceAddressList.add(result.getDevice().getAddress());
                deviceRssiList.add(result.getRssi());
            }

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Scan result received - Name: " + result.getDevice().getName() + ",Address: " + result.getDevice().getAddress());

            //Log.d(TAG, "Scan result received - Name: " + result.getDevice().getName() + ",Address: " + result.getDevice().getAddress() + ",RSSI: " + result.getRssi() + "ScanRecord: " + result.getScanRecord());



  /*          List<ParcelUuid> listOfUUIDS = result.getScanRecord().getServiceUuids();
            byte[] mScanRecord = result.getScanRecord().getBytes();

            Log.d(TAG, "onScanValues1: "+ mScanRecord[25]);
            Log.d(TAG, "onScanValues2: "+ mScanRecord[26]);
            Log.d(TAG, "onScanValues3: "+ mScanRecord[27]);
            Log.d(TAG, "onScanValues4: "+ mScanRecord[28]);



            String major = String.valueOf((mScanRecord[25] & 0xff) * 0x100 + (mScanRecord[26] & 0xff));
            String minor = String.valueOf((mScanRecord[27] & 0xff) * 0x100 + (mScanRecord[28] & 0xff));

            Log.d(TAG, "Scan result received - Major:" + major + ",Minor" + minor);


            Log.d(TAG, "Scan object result" + result.getDevice().getName() + "UUIDS:" + result.getScanRecord().getServiceUuids());

            Map<ParcelUuid, byte[]> serviceData = result.getScanRecord().getServiceData();

            for (int i = 0; i < listOfUUIDS.size(); i++) {
                ParcelUuid parcelUuid = listOfUUIDS.get(i);
                byte[] uuidSvcData = serviceData.get(parcelUuid);
                Log.d(TAG, "parcelUUIDs: " + parcelUuid.getUuid() + "serviceData: " +  bytesToHex(uuidSvcData));

                String majorServc = String.valueOf((uuidSvcData[0] & 0xff) * 0x100 + (uuidSvcData[1] & 0xff));
                String minorServc = String.valueOf((uuidSvcData[2] & 0xff) * 0x100 + (uuidSvcData[3] & 0xff));

                Log.d(TAG, "ServiceMajor: " + majorServc + "Minor" + minorServc);


            }*/


            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Device Count " + deviceAddressList.size());
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
            sdf.setTimeZone(tz);
            String localTime = sdf.format(new Date());
            //LogData logData = new LogData(localTime, result.getDevice().getAddress(), String.valueOf(result.getRssi()), result.getScanRecord());
            //FileLogger.INSTANCE.writeLogSnapShot(logData);
        }
    };

/*
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/


    /**
     * Is scanning boolean.
     *
     * @return the boolean
     */
    public boolean isScanning() {
        return isScanning;
    }
}
