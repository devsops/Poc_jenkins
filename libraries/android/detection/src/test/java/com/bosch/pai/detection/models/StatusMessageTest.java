package com.bosch.pai.detection.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatusMessageTest {

    StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,"test");

    @Test
    public void tStatusMessageTest(){
        StatusMessage statusMessage = new StatusMessage();
        statusMessage.setStatus(StatusMessage.STATUS.SUCCESS);
        statusMessage.setMessage("test");
        assertEquals(statusMessage.getStatus(),statusMessage.getStatus());
        assertEquals("test",statusMessage.getMessage());
        assertNotNull(statusMessage.toString());
    }
}
