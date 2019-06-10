package com.bosch.pai.retail.db.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubSessionDetailIosTest {


@Test

    public void testSubSessionDetailIosTest(){


    SubSessionDetailIos subSessionDetailIos = new SubSessionDetailIos();

    subSessionDetailIos.setEndTime(12l);
    subSessionDetailIos.setIsValid(true);
    subSessionDetailIos.setLocationName("kor");
    subSessionDetailIos.setSessionId("test1");
    subSessionDetailIos.setSiteName("kor");
    subSessionDetailIos.setStoreId("ban");
    subSessionDetailIos.setSubSessionId("test1a");
    subSessionDetailIos.setUserId("test");
    subSessionDetailIos.setStartTime(20l);


    assertEquals(true,subSessionDetailIos.getIsValid());
    assertEquals("kor",subSessionDetailIos.getLocationName());
    assertEquals("test1",subSessionDetailIos.getSessionId());
    assertEquals("kor",subSessionDetailIos.getSiteName());
    assertEquals("ban",subSessionDetailIos.getStoreId());
    assertEquals("test1a",subSessionDetailIos.getSubSessionId());
    assertEquals("test",subSessionDetailIos.getUserId());

    assertNotNull(subSessionDetailIos.getEndTime());
    assertNotNull(subSessionDetailIos.getStartTime());
    assertNotNull(subSessionDetailIos.toString());













}



















}
