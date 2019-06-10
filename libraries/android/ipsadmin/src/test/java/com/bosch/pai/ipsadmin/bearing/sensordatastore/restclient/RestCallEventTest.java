package com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RestCallEvent.class})
public class RestCallEventTest {

    private RestCallEvent restCallEvent;

    private String requestID;
    private EventType eventType;
    private Sender sender;
    private List<String> locations = new ArrayList<>();

    @Before
    public void init() throws Exception {
        restCallEvent = new RestCallEvent(requestID, eventType, sender);
        restCallEvent = new RestCallEvent(requestID, eventType, sender, "siteName");
        restCallEvent = new RestCallEvent(requestID, eventType, sender, locations, "siteName");
    }

    @Test
    public void TestToTest(){

    }
}
