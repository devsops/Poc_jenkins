package com.bosch.pai.comms.operation;

import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.UUID;

@PrepareForTest({PUTRunnable.class, HTTPRunnableTask.class})
public class PUTRunnableTest extends HTTPRunnableTaskTest {

    @Before
    public void init() throws Exception {
        super.init();
    }

    @Test
    public void testConstuctorAndRun() {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.PUT, BASE_TEST_URL, END_POINT);
        requestObject.setMessageBody("DUMMY_DATA");
        final PUTRunnable putRunnable = new PUTRunnable(UUID.randomUUID(), requestObject, retryConnectionHandler, commsListener);
        putRunnable.run();
        Mockito.verify(commsListener, Mockito.times(1)).onResponse(Mockito.any(ResponseObject.class));
    }
}
