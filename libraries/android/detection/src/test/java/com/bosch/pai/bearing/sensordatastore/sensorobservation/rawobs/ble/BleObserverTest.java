package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BleObserver.class, Context.class, ResourceDataManager.class, LogAndToastUtil.class, Sensor.class, Property.class,
        ConfigurationSettings.class})
public class BleObserverTest {

    @Mock
    private Context context;
    @Mock
    private ResourceDataManager resourceDataManager;
    @Mock
    private BluetoothManager bluetoothManager;
    @Mock
    private BluetoothAdapter bluetoothAdapter;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private Sensor sensor;

    private BleObserver bleObserver;
    private SensorObservationHandler sensorObservationHandler;

    @Before
    public void init() throws Exception{
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(ResourceDataManager.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(Sensor.class);
        PowerMockito.mockStatic(Property.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getSystemService(Context.BLUETOOTH_SERVICE)).thenReturn(bluetoothManager);
        PowerMockito.when(bluetoothManager.getAdapter()).thenReturn(bluetoothAdapter);
        PowerMockito.when(ConfigurationSettings.saveConfigObject(Mockito.any(ConfigurationSettings.class))).thenReturn(true);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class), Mockito.any()))
                .thenReturn(sensor);
        PowerMockito.when(sensor.getProperty(Property.Sensor.BLE_SCAN_TIMEOUT)).thenReturn(Integer.valueOf("4000"));
        bleObserver = new BleObserver(context, resourceDataManager);
    }

    @Test
    public void setupBleTest(){
        Assert.assertFalse(bleObserver.setupBle(sensorObservationHandler));
    }

    @Test
    public void startAndStopScanningTest(){
        bleObserver.startScanning();
        Assert.assertFalse(bleObserver.isScanning());
        bleObserver.stopScanning();
        Assert.assertFalse(bleObserver.isScanning());
    }
}
