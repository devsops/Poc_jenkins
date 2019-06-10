package com.bosch.pai.retail.db.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuickAssistRequestDetailTest {



    @Test

    public void testQuickAssistRequestDetailTest(){


        QuickAssistRequestDetail quickAssistRequestDetail = new QuickAssistRequestDetail();

        quickAssistRequestDetail.setLocation("kor");
        quickAssistRequestDetail.setServed(true);
        quickAssistRequestDetail.setUserId("test");
        quickAssistRequestDetail.setRequestId("yes");
        quickAssistRequestDetail.setRequestedAt( new Timestamp(5));
        quickAssistRequestDetail.setRespondedAt(new Timestamp(10));



        assertEquals("kor",quickAssistRequestDetail.getLocation());
        assertEquals(true,quickAssistRequestDetail.getServed());
        assertEquals("test",quickAssistRequestDetail.getUserId());
        assertEquals("yes",quickAssistRequestDetail.getRequestId());

        assertNotNull(quickAssistRequestDetail.getRequestedAt());
        assertNotNull(quickAssistRequestDetail.getRespondedAt());
        assertNotNull(quickAssistRequestDetail.toString());














    }













}
