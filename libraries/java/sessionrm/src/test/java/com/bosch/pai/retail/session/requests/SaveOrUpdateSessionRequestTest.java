package com.bosch.pai.retail.session.requests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SaveOrUpdateSessionRequestTest {


@Test

    public  void testSaveOrUpdateSessionRequestTest(){

    SaveOrUpdateSessionRequest saveOrUpdateSessionRequest = new SaveOrUpdateSessionRequest( "test","new","kor","ban","gmt");


    assertEquals("test",saveOrUpdateSessionRequest.getUserId());
    assertEquals("new",saveOrUpdateSessionRequest.getSessionId());
    assertEquals("kor",saveOrUpdateSessionRequest.getSiteName());
assertEquals("ban",saveOrUpdateSessionRequest.getLocationName());
assertEquals("gmt",saveOrUpdateSessionRequest.getTimeZoneID());
    assertNotNull(saveOrUpdateSessionRequest.toString());


}









}
