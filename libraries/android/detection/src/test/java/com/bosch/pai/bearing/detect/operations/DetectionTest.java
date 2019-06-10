package com.bosch.pai.bearing.detect.operations;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.core.BearingHandler;
import com.bosch.pai.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Detection.class,BearingHandler.class,BearingRequestParser.class,BearingMode.class})
public class DetectionTest {

    Detection detection;

    Set<BearingConfiguration.Approach> approaches;

    //  @Mock
    BearingConfiguration.Approach approach;

    @Mock
    BearingRequestParser bearingRequestParser;

    @Mock
    BearingHandler bearingHandler;

    // @Mock
    BearingMode bearingMode;

    // @Mock
    BearingConfiguration bearingConfiguration;

    //  @Mock
    BearingData bearingData;

    BearingConfiguration.OperationType operationType;

    @Mock
    BearingCallBack bearingCallBack;

    @Before
    public void testSetUpBefore() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(BearingRequestParser.class);
        PowerMockito.mockStatic(BearingMode.class);



        /*bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));
        bearingMode = BearingMode.LOCATION;
        approach = BearingConfiguration.Approach.DATA_CAPTURE;
        operationType = BearingConfiguration.OperationType.DETECT_SITE;
        approaches = new HashSet<>();
        approaches.add(approach);
        when(BearingRequestParser.class,"parseConfigurationForOperationType",bearingConfiguration).thenReturn(operationType);
        when(BearingRequestParser.class,"parseConfigurationForApproachList",bearingConfiguration).thenReturn(approaches);
        when(BearingRequestParser.class,"getBearingModeForDetection",bearingData).thenReturn(bearingMode);*/
    }

    @Test
    public void testInvokeStartBearing() throws Exception {
        detection = new Detection(bearingHandler);
        assertNotNull(detection);

        //Mockito.ver(BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration),Mockito.times(1));
        //Mockito.v
        ///
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));
        bearingMode = BearingMode.LOCATION;
        approach = BearingConfiguration.Approach.DATA_CAPTURE;
        operationType = BearingConfiguration.OperationType.DETECT_SITE;
        approaches = new HashSet<>();
        approaches.add(approach);
        when(BearingRequestParser.class,"parseConfigurationForOperationType",bearingConfiguration).thenReturn(operationType);
        when(BearingRequestParser.class,"parseConfigurationForApproachList",bearingConfiguration).thenReturn(approaches);
        when(BearingRequestParser.class,"getBearingModeForDetection",bearingData).thenReturn(bearingMode);
        ///
        detection.invokeStartBearing(bearingConfiguration,bearingData,bearingCallBack);
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);

    }

    @Test
    public void testInvokeDifferentSite() throws Exception {
        detection = new Detection(bearingHandler);
        assertNotNull(detection);
        //
        //  operationType = BearingConfiguration.OperationType.DETECT_LOC;
        //
        ///
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));
        bearingMode = BearingMode.LOCATION;
        approach = BearingConfiguration.Approach.DATA_CAPTURE;
        operationType = BearingConfiguration.OperationType.DETECT_LOC;
        approaches = new HashSet<>();
        approaches.add(approach);
        when(BearingRequestParser.class,"parseConfigurationForOperationType",bearingConfiguration).thenReturn(operationType);
        when(BearingRequestParser.class,"parseConfigurationForApproachList",bearingConfiguration).thenReturn(approaches);
        when(BearingRequestParser.class,"getBearingModeForDetection",bearingData).thenReturn(bearingMode);
        ///
        detection.invokeStartBearing(bearingConfiguration,bearingData,bearingCallBack);
        //Mockito.ver(BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration),Mockito.times(1));
        //Mockito.v
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);


    }

    ////

    @Test
    public void testInvokeStopBearing() throws Exception {
        detection = new Detection(bearingHandler);
        assertNotNull(detection);
        //
        //  operationType = BearingConfiguration.OperationType.DETECT_LOC;
        //
        ///
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));
        bearingMode = BearingMode.LOCATION;
        approach = BearingConfiguration.Approach.DATA_CAPTURE;
        operationType = BearingConfiguration.OperationType.DETECT_LOC;
        approaches = new HashSet<>();
        approaches.add(approach);
        when(BearingRequestParser.class,"parseConfigurationForOperationType",bearingConfiguration).thenReturn(operationType);
        when(BearingRequestParser.class,"parseConfigurationForApproachList",bearingConfiguration).thenReturn(approaches);
        // when(BearingRequestParser.class,"getBearingModeForDetection",bearingData).thenReturn(bearingMode);
        ///
        detection.invokeStopBearing(bearingConfiguration);
        //Mockito.ver(BearingRequestParser.parseConfigurationForOperationType(bearingConfiguration),Mockito.times(1));
        //Mockito.v
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);


    }

    @Test
    public void testInvokeStopDifferentSite() throws Exception {
        detection = new Detection(bearingHandler);
        assertNotNull(detection);
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));
        bearingMode = BearingMode.LOCATION;
        approach = BearingConfiguration.Approach.DATA_CAPTURE;
        operationType = BearingConfiguration.OperationType.DETECT_SITE;
        approaches = new HashSet<>();
        approaches.add(approach);
        when(BearingRequestParser.class,"parseConfigurationForOperationType",bearingConfiguration).thenReturn(operationType);
        when(BearingRequestParser.class,"parseConfigurationForApproachList",bearingConfiguration).thenReturn(approaches);
        // when(BearingRequestParser.class,"getBearingModeForDetection",bearingData).thenReturn(bearingMode);
        ///
        detection.invokeStopBearing(bearingConfiguration);
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BearingRequestParser.parseConfigurationSensorForApproach(bearingConfiguration, approach);

    }

    @Test
    public void testShutDown()
    {
        detection = new Detection(bearingHandler);
        assertNotNull(detection);
        detection.shutdown();
    }
}
