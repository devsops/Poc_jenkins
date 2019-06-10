package com.bosch.pai.retail.db.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SessionDetailTest {

    @Test


    public  void testSessionDetailTest(){

        SessionDetail sessionDetail = new SessionDetail();

        sessionDetail.setStartTime(12l);
        sessionDetail.setEndTime(20l);
        sessionDetail.setSessionId("xyz");
        sessionDetail.setIsValid(false);
        sessionDetail.setStoreId("ban");
        sessionDetail.setUserId("test");
        assertEquals("xyz",sessionDetail.getSessionId());
        assertEquals(false,sessionDetail.getIsValid());
        assertEquals("ban",sessionDetail.getStoreId());
        assertEquals("test",sessionDetail.getUserId());

        assertNotNull(sessionDetail.toString());
        assertNotNull(sessionDetail.getStartTime());
        assertNotNull(sessionDetail.getEndTime());






    }



}
