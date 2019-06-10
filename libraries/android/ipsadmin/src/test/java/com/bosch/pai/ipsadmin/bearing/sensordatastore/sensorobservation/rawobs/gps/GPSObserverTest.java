
package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.enums.Property;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(PowerMockRunner.class)
@PrepareForTest({GPSObserver.class, Context.class, ResourceDataManager.class, LogAndToastUtil.class, Sensor.class, Property.class,
        ConfigurationSettings.class})
public class GPSObserverTest {

    private GPSObserver gpsObserver;

    @Mock
    LocationManager locationManager;
    @Mock
    Location location;
    @Mock
    private Context context;
    @Mock
    private ResourceDataManager resourceDataManager;
    private List<String> providers = new ArrayList<>();

    @Before
    public void init() throws Exception{
        providers.add("pro1");
        providers.add("pro2");
        providers.add("pro3");
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(ResourceDataManager.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(Sensor.class);
        PowerMockito.mockStatic(Property.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager);
        PowerMockito.when(locationManager.getProviders(true)).thenReturn(providers);
        gpsObserver = new GPSObserver(context, resourceDataManager);
    }

    @Test
    public void setUpGPSTest() {
        PowerMockito.when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
        Assert.assertFalse(gpsObserver.setUpGPS());
        PowerMockito.when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        Assert.assertTrue(gpsObserver.setUpGPS());
    }

    @Test
    public void startTest(){
        PowerMockito.when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        PowerMockito.when(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(location);
        Assert.assertFalse(gpsObserver.start());
        PowerMockito.when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
        PowerMockito.when(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(location);
        Assert.assertFalse(gpsObserver.start());
    }

}

