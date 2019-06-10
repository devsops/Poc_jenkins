package com.bosch.pai.retail.db.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SessionDetailIosTest {

    @Test

    public void testSessionDetailIosTest(){

        SessionDetailIos sessionDetailIos = new SessionDetailIos();

        sessionDetailIos.setStoreId("test");
        sessionDetailIos.setUserId("abc");
        sessionDetailIos.setEndTime(12L);
        sessionDetailIos.setIsValid(true);
        sessionDetailIos.setSessionId("newtest");
        sessionDetailIos.setStartTime(10L);

        assertEquals("test",sessionDetailIos.getStoreId());
        assertEquals("abc",sessionDetailIos.getUserId());

        assertEquals(true,sessionDetailIos.getIsValid());
        assertEquals("newtest",sessionDetailIos.getSessionId());

                assertNotNull(sessionDetailIos.getEndTime());
                assertNotNull(sessionDetailIos.getStartTime());
                assertNotNull(sessionDetailIos.toString());















    }


























}

