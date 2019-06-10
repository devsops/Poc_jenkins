package com.bosch.pai.retail.session.requests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EndSessionRequestTest {

@Test
    public  void TestEndSessionRequestTest(){


    EndSessionRequest endSessionRequest = new EndSessionRequest( "test","new");

    assertEquals("test",endSessionRequest.getUserId());
    assertEquals("new",endSessionRequest.getSessionId());

    assertNotNull(endSessionRequest.toString());
    assertNotNull(endSessionRequest.getSessionId());
    assertNotNull(endSessionRequest.getSessionId());












}



}


