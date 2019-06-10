package com.bosch.pai.bearing.sensordatastore.sensorobservation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.net.wifi.WifiManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SensorUtil.class, Context.class})
public class SensorUtilTest {

    @Mock
    Context context;
    @Mock
    private WifiManager wifiManager;
    @Mock
    private BluetoothManager bluetoothManager;
    @Mock
    private BluetoothAdapter bluetoothAdapter;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Context.class);
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getSystemService(Context.WIFI_SERVICE)).thenReturn(wifiManager);
        when(context.getSystemService(Context.BLUETOOTH_SERVICE)).thenReturn(bluetoothManager);
        when(bluetoothManager.getAdapter()).thenReturn(bluetoothAdapter);
        when(wifiManager.isWifiEnabled()).thenReturn(true);
        when(bluetoothAdapter.isEnabled()).thenReturn(true);
    }

    @Test
    public void TestTheFunc(){
        Assert.assertFalse(SensorUtil.isScanForBLEMac());
        Assert.assertFalse(SensorUtil.isShutDown());
        Assert.assertFalse(SensorUtil.checkAreWIFISensorsEnabled(context));
        Assert.assertTrue(SensorUtil.checkAreBLESenorsEnabled(context));
    }
}
