package com.bosch.pai.ipsadmin.retail.pmadminlib.common;


import com.bosch.pai.bearing.entity.Location;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResponseObject.class})
public class CommonUtilTest {

    @Test
    public void isResponseValidTest(){
        final List<Location> locations = new ArrayList<>();
        ResponseObject responseObject=new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setResponseBody(new GsonBuilder().create().toJson(locations));
       boolean truetest= CommonUtil.isResponseValid(responseObject);
        Assert.assertTrue(truetest);
    }

    @Test
    public void isResponseValidFalseTest(){
        final List<Location> locations = new ArrayList<>();
        ResponseObject responseObject=new ResponseObject();
        responseObject.setStatusCode(406);
        responseObject.setResponseBody(new GsonBuilder().create().toJson(locations));
        boolean truetest= CommonUtil.isResponseValid(responseObject);
        Assert.assertFalse(truetest);
    }

    @Test
    public void getErrorMessageFromResponseTest(){
        final List<Location> locations = new ArrayList<>();
        ResponseObject responseObject=new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setResponseBody(new GsonBuilder().create().toJson(locations));
        String returnTeststring=CommonUtil.getErrorMessageFromResponse(responseObject);
        Assert.assertNotEquals(Mockito.anyString(),returnTeststring);

    }

    @Test
    public void isCompanySparTest(){

        String getAnalyticsend=CommonUtil.getAnalyticsEndPoint();
        String proximatidemoconfig=CommonUtil.getProximityConfigurationEndPoint();
        String proximaticonfig= CommonUtil.getProximityConfigurationEndPoint();
        String bearingserverurl=CommonUtil.getBearingServerEndPoint();

        Assert.assertEquals("/gatewayService/ipsanalytics/",getAnalyticsend);
        Assert.assertEquals( "/gatewayService/ipsconfiguration/",proximatidemoconfig);
        Assert.assertEquals("/gatewayService/ipsconfiguration/",proximaticonfig);
        Assert.assertEquals("/gatewayService/ipsbearing/",bearingserverurl);
    }

    @Test
    public void getPhilipseEndPointTest(){
        Assert.assertEquals("/gatewayService/retailproximityphilipslifi/",CommonUtil.getPhilipseEndPoint());
    }

    @Test
    public void getServerURLTest(){
        Assert.assertEquals("prod1.bosch-iero.com",CommonUtil.getServerURL());
    }
}
