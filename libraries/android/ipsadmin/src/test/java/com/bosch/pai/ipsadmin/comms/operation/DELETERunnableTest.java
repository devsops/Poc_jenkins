package com.bosch.pai.ipsadmin.comms.operation;

import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.UUID;

@PrepareForTest({HTTPRunnableTask.class, DELETERunnable.class})
public class DELETERunnableTest extends HTTPRunnableTaskTest {

    @Before
    public void init() throws Exception {
        super.init();
    }

    @Test
    public void testConstructorAndRun() {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.DELETE, BASE_TEST_URL, END_POINT);
        requestObject.setMessageBody("DUMMY_DATA");
        final DELETERunnable deleteRunnable = new DELETERunnable(UUID.randomUUID(), requestObject, retryConnectionHandler, commsListener);
        deleteRunnable.run();
        Mockito.verify(commsListener, Mockito.times(1)).onResponse(Mockito.any(ResponseObject.class));
    }
}
