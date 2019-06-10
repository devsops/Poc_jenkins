package com.bosch.pai.ipsadmin.bearing.core.operation.processor;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.location.LocationTrainerUtil;
import com.bosch.pai.ipsadmin.bearing.core.operation.training.site.SiteTrainUtil;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.event.DataCaptureRequestEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataCaptureEventProcessor.class})
public class DataCaptureEventProcessorTest {

    @Mock
    private SiteTrainUtil siteTrainUtil;
    @Mock
    private LocationTrainerUtil locationTrainerUtil;
    @Mock
    private BearingCallBack bearingCallBack;

    private DataCaptureRequestEvent dataCaptureRequestEvent;

    @Before
    public void init() throws Exception {
        PowerMockito.whenNew(SiteTrainUtil.class).withNoArguments().thenReturn(siteTrainUtil);
        PowerMockito.whenNew(LocationTrainerUtil.class).withNoArguments().thenReturn(locationTrainerUtil);
        dataCaptureRequestEvent = new DataCaptureRequestEvent(UUID.randomUUID().toString(),
                EventType.CAPTURE_DATA_EVENT, bearingCallBack);
        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
        sensorTypes.add(BearingConfiguration.SensorType.ST_WIFI);
        dataCaptureRequestEvent.setSensors(sensorTypes);
        dataCaptureRequestEvent.setApproach(BearingConfiguration.Approach.FINGERPRINT);
    }

    @Test
    public void testCreateScenarios() {
        DataCaptureEventProcessor dataCaptureEventProcessor =
                new DataCaptureEventProcessor(dataCaptureRequestEvent.getRequestID(), dataCaptureRequestEvent, bearingCallBack);
        dataCaptureRequestEvent.setSite(true);
        dataCaptureEventProcessor.run();
        Mockito.verify(siteTrainUtil, Mockito.times(1))
                .createSite(Mockito.any(UUID.class), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyListOf(BearingConfiguration.SensorType.class));
        dataCaptureRequestEvent.setSiteMerge(true);
        dataCaptureEventProcessor.run();
        Mockito.verify(siteTrainUtil, Mockito.times(1))
                .scanSensorForSignalMerge(Mockito.any(UUID.class), Mockito.anyString(), Mockito.anyListOf(BearingConfiguration.SensorType.class));
        dataCaptureRequestEvent.setSite(false);
        final List<String> locs = new ArrayList<>();
        locs.add("LOCATION_NAME");
        dataCaptureRequestEvent.setLocations(locs);
        dataCaptureEventProcessor.run();
        Mockito.verify(locationTrainerUtil, Mockito.times(1)).
                addLocationToSite(Mockito.any(UUID.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyListOf(BearingConfiguration.SensorType.class));
        dataCaptureRequestEvent.setLocationRetrain(true);
        dataCaptureEventProcessor.run();
        Mockito.verify(locationTrainerUtil, Mockito.times(1)).
                retrainLocation(Mockito.any(UUID.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyListOf(BearingConfiguration.SensorType.class));
    }
}
