package com.bosch.pai.comms.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ResponseObjectTest {

    private Map<String, List<String>> headers = new HashMap<>();
    private List<String> strings = new ArrayList<>();

    @Before
    public void init() {
        headers.put("KEY1", strings);
        headers.put("KEY2", strings);
        headers.put("KEY3", strings);
    }

    @Test
    public void testConstructorAndGetterSetter() {
        final String responseBody = "RESPONSE_BODY";
        final int STATUS_CODE = 201;
        final String STATUS_MESSAGE = "STATUS_MESSAGE";
        final UUID requestID = UUID.randomUUID();

        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRequestID(requestID);
        responseObject.setResponseBody(responseBody);
        responseObject.setStatusCode(STATUS_CODE);
        responseObject.setHeaders(headers);
        responseObject.setStatusMessage(STATUS_MESSAGE);

        Assert.assertEquals(STATUS_CODE, responseObject.getStatusCode());
        Assert.assertEquals(STATUS_MESSAGE, responseObject.getStatusMessage());
        Assert.assertEquals(responseBody, (String) responseObject.getResponseBody());
        Assert.assertEquals(requestID, responseObject.getRequestID());
        Assert.assertEquals(headers, responseObject.getHeaders());
        Assert.assertNotNull(responseObject.toString());
    }
}
