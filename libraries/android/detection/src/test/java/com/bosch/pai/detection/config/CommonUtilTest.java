package com.bosch.pai.detection.config;

import com.bosch.pai.comms.model.ResponseObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CommonUtilTest {

    private Map<String, List<String>> headers = new HashMap<>();
    private static final String BEARING_URL = "/gatewayService/ipsbearing/";

    @Before
    public void init() throws Exception {

    }

    @Test
    public void getBearingServerEndPointTest(){
        Assert.assertEquals(BEARING_URL,CommonUtil.getBearingServerEndPoint());
    }

    @Test
    public void isResponseValidTest(){
        final String responseBody = "RESPONSE_BODY";
        final int STATUS_CODE = 200;
        final String STATUS_MESSAGE = "STATUS_MESSAGE";
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseBody(responseBody);
        responseObject.setStatusCode(STATUS_CODE);
        responseObject.setHeaders(headers);
        responseObject.setStatusMessage(STATUS_MESSAGE);
        Assert.assertTrue(CommonUtil.isResponseValid(responseObject));
    }

    @Test
    public void isResponseValidFailureTest(){
        final String responseBody = "RESPONSE_BODY";
        final int STATUS_CODE = 406;
        final String STATUS_MESSAGE = "STATUS_MESSAGE";
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseBody(responseBody);
        responseObject.setStatusCode(STATUS_CODE);
        responseObject.setHeaders(headers);
        responseObject.setStatusMessage(STATUS_MESSAGE);
        Assert.assertFalse(CommonUtil.isResponseValid(responseObject));
    }

    @Test
    public void getErrorMessageFromResponse(){
        final String responseBody = "RESPONSE_BODY";
        final int STATUS_CODE = 201;
        final String STATUS_MESSAGE = "STATUS_MESSAGE";
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseBody(responseBody);
        responseObject.setStatusCode(STATUS_CODE);
        responseObject.setHeaders(headers);
        responseObject.setStatusMessage(STATUS_MESSAGE);
        Assert.assertNotEquals("Error from server. ", CommonUtil.getErrorMessageFromResponse(responseObject));
    }
}
