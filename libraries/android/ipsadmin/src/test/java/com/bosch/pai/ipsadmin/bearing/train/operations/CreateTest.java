package com.bosch.pai.ipsadmin.bearing.train.operations;

import android.content.Context;

import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.LocationMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.ipsadmin.bearing.train.errorcode.Codes;
import com.bosch.pai.ipsadmin.comms.exception.CertificateLoadException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class,BearingHandler.class,BearingConfiguration.Approach.class,BearingRequestParser.class})
public class CreateTest {

    @Mock
    BearingHandler bearingHandler;

    @Mock
    BearingConfiguration bearingConfiguration;

  //  @Mock
    BearingData bearingData;

  //  @Mock
    BearingConfiguration.Approach approach;

    boolean syncServer;

    String requestId;

    @Mock
    BearingCallBack bearingCallBack;

    List<LocationMetaData> locationMetaDataList;
    LocationMetaData locationMetaData;

    @Mock
    Context context;


    Create create;
  //  BearingHandler bearingHandler;
  String filePath;

    List<BearingConfiguration.SensorType> bearingSensors;
    BearingConfiguration.SensorType sensorType;
    Map<BearingConfiguration.Approach,List<BearingConfiguration.SensorType>> map;

    @Before
    public void testTriggerLocationCreation() throws CertificateLoadException {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(BearingRequestParser.class);
        sensorType = BearingConfiguration.SensorType.ST_WIFI;
        approach = BearingConfiguration.Approach.DATA_CAPTURE;
        bearingSensors = new ArrayList<>();
        bearingSensors.add(sensorType);
        map = new HashMap<BearingConfiguration.Approach,List<BearingConfiguration.SensorType>>();
        map.put(approach,bearingSensors);
        bearingData = new BearingData(new SiteMetaData("DemoSite"));


        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,map);
        requestId = "11122";
        create = new Create(bearingHandler);
        syncServer = true;

        locationMetaDataList = new ArrayList<LocationMetaData>();
        locationMetaData = new LocationMetaData("DemoSite");
        locationMetaDataList.add(locationMetaData);
//        locationMetaDataList.add

     //   when(BearingRequestParser.getLocationMetaDataList(any(BearingData.class))).thenReturn(locationMetaDataList);
    }

    @Test
    public void triggerLocationCreationTest()
    {
       int returnValue = create.triggerLocationCreation(requestId,bearingConfiguration,bearingData,approach,syncServer,bearingCallBack);
       assertNotNull(returnValue);
       assertEquals(Codes.RESPONSE_OK,returnValue);
       //  bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,map);
    }

    @Test
    public void triggerLocationInThreshholdTest()
    {
        sensorType = BearingConfiguration.SensorType.ST_WIFI;
        approach = BearingConfiguration.Approach.THRESHOLDING;
        bearingSensors = new ArrayList<>();
        bearingSensors.add(sensorType);
        map = new HashMap<BearingConfiguration.Approach,List<BearingConfiguration.SensorType>>();
        map.put(approach,bearingSensors);
      //  bearingData = new BearingData(new SiteMetaData("DemoSite"));
        bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,map);
        requestId = "11122";
        create = new Create(bearingHandler);
        syncServer = true;

        locationMetaDataList = new ArrayList<LocationMetaData>();
        locationMetaData = new LocationMetaData("DemoSite");
        locationMetaDataList.add(locationMetaData);
//        locationMetaDataList.add

        when(BearingRequestParser.getLocationMetaDataList(bearingData)).thenReturn(locationMetaDataList);

        int returnValue = create.triggerLocationCreation(requestId,bearingConfiguration,bearingData,approach,syncServer,bearingCallBack);
        assertNotNull(returnValue);
        assertEquals(Codes.RESPONSE_OK,returnValue);
    }


}
