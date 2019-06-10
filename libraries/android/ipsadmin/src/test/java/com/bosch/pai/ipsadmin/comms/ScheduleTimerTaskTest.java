package com.bosch.pai.ipsadmin.comms;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ScheduleTimerTask.class, CommsManager.class})
public class ScheduleTimerTaskTest {

    @Mock
    private CommsManager commsManager;

    @Before
    public void init() {
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        final Map<String, String> map = new HashMap<>();
        map.put("URL1", "CONTEXT_ID1");
        map.put("URL2", "CONTEXT_ID2");
        PowerMockito.when(commsManager.getUrlContextIdMAP()).thenReturn(map);
    }

    @Test
    public void testConstructorAndRun() {
        final ScheduleTimerTask scheduleTimerTask = new ScheduleTimerTask();
        scheduleTimerTask.run();
        Mockito.verify(commsManager, Mockito.times(2))
                .refreshToken(Mockito.anyString(), Mockito.any(CommsListener.class));
    }
}
