package com.bosch.pai.bearing.core.operation.processor;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.event.RequestDetectionStartEvent;
import com.bosch.pai.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;

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
@PrepareForTest({DetectionStartRequestProcessor.class})
public class DetectionStartRequestProcessorTest {
    @Mock
    private SiteDetectorUtil siteDetectorUtil;
    @Mock
    private LocationDetectorUtil locationDetectorUtil;
    @Mock
    private BearingCallBack sender;
    private RequestDetectionStartEvent event;

    @Before
    public void init() throws Exception {
        PowerMockito.whenNew(SiteDetectorUtil.class).withNoArguments().thenReturn(siteDetectorUtil);
        PowerMockito.whenNew(LocationDetectorUtil.class).withNoArguments().thenReturn(locationDetectorUtil);
        event = new RequestDetectionStartEvent(UUID.randomUUID().toString(), EventType.TRIGGER_DETECTION, sender);
    }

    @Test
    public void testConstructorAndRunInvoke() {
        final List<BearingConfiguration.SensorType> list = new ArrayList<>();
        list.add(BearingConfiguration.SensorType.ST_WIFI);
        final DetectionStartRequestProcessor processor = new DetectionStartRequestProcessor(event.getRequestID(), event, sender);
        event.setSensors(list);
        event.setApproach(BearingConfiguration.Approach.FINGERPRINT);
        event.setSite(true);
        processor.run();
        Mockito.verify(siteDetectorUtil, Mockito.times(1))
                .startSiteDetection(Mockito.any(BearingConfiguration.Approach.class), Mockito.any(UUID.class), Mockito.anyListOf(BearingConfiguration.SensorType.class));
        event.setSite(false);
        processor.run();
        Mockito.verify(locationDetectorUtil, Mockito.times(1))
                .startLocationDetection(Mockito.any(BearingConfiguration.Approach.class), Mockito.any(UUID.class), Mockito.anyListOf(BearingConfiguration.SensorType.class));

    }
}
