
package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.wifi;


import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.logger.Logger;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.RequestResponseHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceStateManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@Config(sdk = Build.VERSION_CODES.KITKAT)
@PrepareForTest({WifiObserver.class,Context.class, WifiReceiver.class, LogAndToastUtil.class})
public class WifiObserverTest {

    @Mock
    private Context context;
    @Mock
    private ResourceDataManager resourceDataManager;
    @Mock
    private WifiManager wifiManager;

    private WifiObserver wifiObserver;
    private SensorObservationHandler sensorObservationHandler;

    @Before
    public void init() throws Exception{
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(WifiReceiver.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getSystemService(Context.WIFI_SERVICE)).thenReturn(wifiManager);
        wifiObserver = new WifiObserver(context, resourceDataManager);
    }

    @Test
    public void startScanningTest(){
        Assert.assertFalse(wifiObserver.startScanning());
    }

    @Test
    public void setupFilterWifiTrueTest(){
        PowerMockito.when(wifiManager.isWifiEnabled()).thenReturn(true);
        PowerMockito.when(wifiManager.isScanAlwaysAvailable()).thenReturn(true);
        Assert.assertTrue(wifiObserver.setupFilterWifi(sensorObservationHandler));
    }

    @Test
    public void setupFilterWifiFalseTest(){
        PowerMockito.when(wifiManager.isWifiEnabled()).thenReturn(false);
        PowerMockito.when(wifiManager.isScanAlwaysAvailable()).thenReturn(false);
        Assert.assertFalse(wifiObserver.setupFilterWifi(sensorObservationHandler));
    }

    @Test
    public void teardownFilterWifiTest(){
        wifiObserver.teardownFilterWifi();
        Mockito.verify(context, Mockito.atLeastOnce()).unregisterReceiver(Mockito.any(WifiReceiver.class));
    }


}

