package com.bosch.pai.ipsadmin.bearing.core.operation.processor;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestThreshDetectStartEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.BearingResponseAggregator;
import com.bosch.pai.ipsadmin.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;

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
@PrepareForTest({ThreshDetectionStartProcessor.class, LogAndToastUtil.class, BearingHandler.class,
        BearingResponseAggregator.class})
public class ThreshDetectionStartProcessorTest {

    @Mock
    private ObservationHandlerAndListener observationHandlerAndListener;
    @Mock
    private BearingResponseAggregator bearingResponseAggregator;
    @Mock
    private BearingCallBack bearingCallBack;


    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.mockStatic(BearingResponseAggregator.class);
        PowerMockito.mockStatic(BearingHandler.class);
        PowerMockito.whenNew(ObservationHandlerAndListener.class).withNoArguments().thenReturn(observationHandlerAndListener);
        PowerMockito.when(BearingResponseAggregator.getInstance()).thenReturn(bearingResponseAggregator);
    }

    @Test
    public void testConstructorAndRunInvoke() {
        final RequestThreshDetectStartEvent event = new RequestThreshDetectStartEvent(UUID.randomUUID().toString(), EventType.THRESH_DETECTION, bearingCallBack);
        event.setApproach(BearingConfiguration.Approach.THRESHOLDING);
        event.setSiteName("SITE_NAME");
        final List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
        sensorTypeList.add(BearingConfiguration.SensorType.ST_BLE);
        event.setSensors(sensorTypeList);
        final ThreshDetectionStartProcessor processor = new ThreshDetectionStartProcessor(event.getRequestID(), event, bearingCallBack);
        processor.run();
        Mockito.verify(observationHandlerAndListener, Mockito.times(1))
                .setApproach(Mockito.any(BearingConfiguration.Approach.class));
        Mockito.verify(bearingResponseAggregator, Mockito.times(1))
                .updateDetectionResponseAggregatorMap(Mockito.any(UUID.class), Mockito.any(BearingConfiguration.OperationType.class),
                        Mockito.any(BearingConfiguration.Approach.class), Mockito.any(BearingCallBack.class));
        Mockito.verify(observationHandlerAndListener, Mockito.times(1))
                .registerSensorObservationListener();
        Mockito.verify(observationHandlerAndListener, Mockito.times(1))
                .setObservationDataType(Mockito.any(RequestDataHolder.ObservationDataType.class));
        Mockito.verify(observationHandlerAndListener, Mockito.times(1))
                .addObservationSource(Mockito.any(UUID.class), Mockito.anyListOf(BearingConfiguration.SensorType.class), Mockito.anyBoolean());
        PowerMockito.verifyStatic(Mockito.times(1));
        BearingHandler.addRequestToRequestDataHolderMap(Mockito.any(RequestDataHolder.class));
    }

}
