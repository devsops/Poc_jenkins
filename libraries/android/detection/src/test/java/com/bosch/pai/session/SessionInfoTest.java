package com.bosch.pai.session;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SessionInfoTest {
    private SessionInfo sessionInfo = new SessionInfo("userId");

    @Test
    public void tSessionInfoTest(){
        sessionInfo.setValid(false);
        sessionInfo.setEndTime(10212);
        sessionInfo.setSessionId("sessionId");
        sessionInfo.setStartTime(10212);
        sessionInfo.setStoreId("storeId");
        assertEquals(sessionInfo.getSessionId(),sessionInfo.getSessionId());
        assertFalse(sessionInfo.isValid());
    }
}
