package com.bosch.pai.retail.common.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatusMessageTest {

    StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,"test");

    @Test
    public void testStatusMessageTest(){


        StatusMessage statusMessage = new StatusMessage();

       statusMessage.setStatus(StatusMessage.STATUS.SUCCESS);
        statusMessage.setStatusDescription("test");
     //  assertEquals(statusMessage.getStatus(),statusMessage.getStatus().getStatusMessage());
        assertEquals(statusMessage.getStatus(),statusMessage.getStatus());
        assertEquals("test",statusMessage.getStatusDescription());
        assertNotNull(statusMessage.toString());







    }












}
