package com.bosch.pai.comms;

import com.bosch.pai.comms.util.CommsUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RetryConnectionHandler.class, CommsUtil.class})
public class RetryConnectionHandlerTest {

    @Mock
    private ScheduledExecutorService scheduledExecutorService;

    private final String BASE_URL = "http://www.test.com/";

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Executors.class);
        PowerMockito.when(Executors.newSingleThreadScheduledExecutor()).thenReturn(scheduledExecutorService);
        PowerMockito.mockStatic(CommsUtil.class);
        PowerMockito.doNothing().when(CommsUtil.class,
                "addLogs",
                Mockito.any(CommsUtil.LOG_STATUS.class), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testHandleConnectionLost() {
        final RetryConnectionHandler retryConnectionHandler = new RetryConnectionHandler(BASE_URL);
        retryConnectionHandler.handleConnectionLost();
        Mockito.verify(scheduledExecutorService, Mockito.times(1))
                .schedule(Mockito.any(Runnable.class), Mockito.anyLong(), Mockito.any(TimeUnit.class));
        final long start = Instant.now().getEpochSecond();
        retryConnectionHandler.waitForConnectionRestore(2000L, 1);
        final long end = Instant.now().getEpochSecond();
        Assert.assertEquals(2L, end - start);
    }
}
