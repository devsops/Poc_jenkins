package com.bosch.pai.comms.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RequestObjectTest {

    @Mock
    private InputStream inputStream;
    @Mock
    private File file;

    private final RequestObject.RequestType REQUEST_TYPE = RequestObject.RequestType.POST;
    private final Map<String, String> HEADERS = new HashMap<>();
    private final Map<String, String> QUERY_PARAMS = new HashMap<>();

    @Before
    public void init() {
        HEADERS.put("KEY1", "VALUE1");
        HEADERS.put("KEY2", "VALUE2");
        HEADERS.put("KEY3", "VALUE3");

        QUERY_PARAMS.put("KEY1", "VALUE1");
        QUERY_PARAMS.put("KEY2", "VALUE2");
        QUERY_PARAMS.put("KEY3", "VALUE3");
    }

    @Test
    public void testConstructorAndGetterSetter() {
        final String BASE_URL = "http://www.testUrl.com/";
        final String API_END_POINT = "endPoint/";
        final RequestObject requestObject = new RequestObject(REQUEST_TYPE, BASE_URL, API_END_POINT);
        requestObject.setMultipartFile(file);
        requestObject.setNonBezirkRequest(true);
        requestObject.setRetry(true);
        final long RETRY_MILLIS = 2000L;
        requestObject.setRetryAfterMillis(RETRY_MILLIS);
        final int RETRY_COUNT = 2;
        requestObject.setRetryCount(RETRY_COUNT);
        requestObject.setCertFileStream(inputStream);
        final String MESSAGE_BODY = "MESSAGE-BODY";
        requestObject.setMessageBody(MESSAGE_BODY);
        requestObject.setHeaders(HEADERS);
        requestObject.setQueryParams(QUERY_PARAMS);

        Assert.assertEquals(REQUEST_TYPE, requestObject.getRequestType());
        Assert.assertEquals(BASE_URL, requestObject.getBaseURL());
        Assert.assertEquals(API_END_POINT, requestObject.getApiEndPoint());
        Assert.assertEquals(file, requestObject.getMultiPartFile());
        Assert.assertTrue(requestObject.isNonBezirkRequest());
        Assert.assertEquals(API_END_POINT, requestObject.getApiEndPoint());
        Assert.assertTrue(requestObject.isRetry());
        Assert.assertEquals(RETRY_MILLIS, requestObject.getRetryAfterMillis());
        Assert.assertEquals(RETRY_COUNT, requestObject.getRetryCount());
        Assert.assertEquals(inputStream, requestObject.getCertFileStream());
        Assert.assertEquals(MESSAGE_BODY, requestObject.getMessageBody());
        Assert.assertEquals(HEADERS, requestObject.getHeaders());
        Assert.assertEquals(QUERY_PARAMS, requestObject.getQueryParams());
    }
}
