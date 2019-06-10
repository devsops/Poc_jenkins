package com.bosch.pai.retail.common.serviceutil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestContextTest {


    @Test

    public void testRequestContextTest (){

        RequestContext requestContext = new RequestContext();

        requestContext.setUserId("test");
        assertEquals("test",requestContext.getUserId());












    }



}
