package com.bosch.pai.ipsadmin.bearing.train;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

import com.bosch.pai.bearing.algorithm.AlgorithmLifeCycleHandler;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.persistence.util.Util;
import com.bosch.pai.ipsadmin.bearing.train.operations.Create;
import com.bosch.pai.ipsadmin.bearing.train.operations.Retrieve;
import com.bosch.pai.ipsadmin.bearing.train.operations.Update;
import com.bosch.pai.ipsadmin.bearing.train.operations.Upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingTrainer.class, Util.class, ConfigurationSettings.class, BearingHandler.class, AlgorithmLifeCycleHandler.class})
public class BearingTrainerTest {

    @Mock
    private Context context;
    @Mock
    private File dir;
    @Mock
    private BearingHandler bearingHandler;
    @Mock
    private AlgorithmLifeCycleHandler algoHandler;
    @Mock
    private BearingCallBack callback;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private Sensor sensor;
    @Mock
    private Create create;
    @Mock
    private Retrieve retrieve;
    @Mock
    private Update update;
    @Mock
    private Upload upload;

    private BearingTrainer bearingTrainer;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mockStatic(BearingHandler.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(AlgorithmLifeCycleHandler.class);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class), Mockito.any()))
                .thenReturn(sensor);
        PowerMockito.when(AlgorithmLifeCycleHandler.getInstance()).thenReturn(algoHandler);
        PowerMockito.when(algoHandler.isAlive()).thenReturn(false);
        PowerMockito.when(BearingHandler.getInstance()).thenReturn(bearingHandler);
        PowerMockito.when(bearingHandler.isAlive()).thenReturn(false);
        PowerMockito.when(context.getFilesDir()).thenReturn(dir);
        PowerMockito.when(dir.getPath()).thenReturn(File.separator);
        PowerMockito.whenNew(Create.class).withAnyArguments().thenReturn(create);
        PowerMockito.whenNew(Retrieve.class).withAnyArguments().thenReturn(retrieve);
        PowerMockito.whenNew(Update.class).withAnyArguments().thenReturn(update);
        PowerMockito.whenNew(Upload.class).withAnyArguments().thenReturn(upload);

        bearingTrainer = BearingTrainer.getInstance(context);
    }

    @Test
    public void testCreate() {
        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
        sensorTypes.add(BearingConfiguration.SensorType.ST_WIFI);
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListHashMap = new HashMap<>();
        approachListHashMap.put(BearingConfiguration.Approach.DATA_CAPTURE, sensorTypes);

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.TRAIN_SITE, approachListHashMap);
        SiteMetaData siteMetaData = new SiteMetaData("SITE_NAME");
        siteMetaData.setNumberOfFloors(1);
        final BearingData bearingData = new BearingData(siteMetaData);
        bearingTrainer.create(configuration, bearingData, false, callback);
        Mockito.verify(create).triggerSiteCreation(Mockito.anyString(), Mockito.any(BearingConfiguration.class),
                Mockito.any(BearingData.class), Mockito.any(BearingConfiguration.Approach.class), Mockito.anyBoolean(), Mockito.any(BearingCallBack.class));

    }
}