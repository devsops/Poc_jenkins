package com.bosch.pai.retail.configuration.interceptor;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertTrue;

public class RequestInterceptorTest {

    private RequestInterceptor requestInterceptor = new RequestInterceptor();

    private MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    private MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

    @Test
    public void testRequestInterceptor() throws Exception{
        boolean status1 = requestInterceptor.preHandle(mockHttpServletRequest,mockHttpServletResponse,new Object());
        mockHttpServletRequest.addHeader("userid","test");
        boolean status2 = requestInterceptor.preHandle(mockHttpServletRequest,mockHttpServletResponse,new Object());
        assertTrue(status2);
        assertTrue(status1);
        requestInterceptor.postHandle(mockHttpServletRequest,mockHttpServletResponse,new Object(),null);
        requestInterceptor.postHandle(mockHttpServletRequest,mockHttpServletResponse,new Object(),null);
    }
}
