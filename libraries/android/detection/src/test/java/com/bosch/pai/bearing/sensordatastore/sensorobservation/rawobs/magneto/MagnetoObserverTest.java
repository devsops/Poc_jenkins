package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.magneto;

import android.content.Context;
import android.hardware.SensorManager;

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
@PrepareForTest({MagnetoObserver.class, Context.class, ResourceDataManager.class, LogAndToastUtil.class, Sensor.class, Property.class,
        ConfigurationSettings.class})
public class MagnetoObserverTest {

    private MagnetoObserver magnetoObserver;
    private SensorObservationHandler sensorObservationHandler;

    @Mock
    private Context context;
    @Mock
    private ResourceDataManager resourceDataManager;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private Sensor sensor;
    @Mock
    private SensorManager sensorManager;

    @Before
    public void init() throws Exception{
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(ResourceDataManager.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(Sensor.class);
        PowerMockito.mockStatic(Property.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.when(ConfigurationSettings.saveConfigObject(Mockito.any(ConfigurationSettings.class))).thenReturn(true);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class), Mockito.any()))
                .thenReturn(sensor);
        PowerMockito.when(sensor.getProperty(Property.Sensor.IMU_SCAN_TIMEOUT)).thenReturn(Integer.valueOf("4000"));
        magnetoObserver = new MagnetoObserver(context,resourceDataManager);
    }

    @Test
    public void setUpMagnetoTest(){
        Assert.assertFalse(magnetoObserver.setUpMagneto(sensorObservationHandler));
        PowerMockito.when(context.getSystemService(Context.SENSOR_SERVICE)).thenReturn(sensorManager);
        Assert.assertFalse(magnetoObserver.setUpMagneto(sensorObservationHandler));
    }
}
