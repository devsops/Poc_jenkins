package com.bosch.pai.bearing.core.event;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class GenerateClassifierEventTest {

    private final EventType EVENT_TYPE = EventType.TRIGGER_ALGO;
    private final BearingConfiguration.Approach APPROACH = BearingConfiguration.Approach.FINGERPRINT;
    @Mock
    private Sender sender;
    @Mock
    private EventSender eventSender;

    private List<String> locationName = new ArrayList<>();

    @Test
    public void testConstructor() {
        String REQUEST_ID = "REQUEST_ID";
        final GenerateClassifierEvent generateClassifierEvent1 = new GenerateClassifierEvent(REQUEST_ID, EVENT_TYPE, APPROACH, sender);
        Assert.assertNotNull(generateClassifierEvent1);
        String SITE_NAME = "SITE_NAME";
        final GenerateClassifierEvent generateClassifierEvent2 = new GenerateClassifierEvent(REQUEST_ID, SITE_NAME, EVENT_TYPE, eventSender);
        Assert.assertNotNull(generateClassifierEvent2);
        final GenerateClassifierEvent generateClassifierEvent3 = new GenerateClassifierEvent(REQUEST_ID, SITE_NAME, locationName, EVENT_TYPE, eventSender);
        Assert.assertNotNull(generateClassifierEvent3);

        generateClassifierEvent3.setGenerateOnServer(true);
        Assert.assertTrue(generateClassifierEvent3.isGenerateOnServer());

        Object o = null;
        Assert.assertNotEquals(generateClassifierEvent1, o);

        Assert.assertNotEquals(1351611956,generateClassifierEvent1.hashCode());
    }
}
