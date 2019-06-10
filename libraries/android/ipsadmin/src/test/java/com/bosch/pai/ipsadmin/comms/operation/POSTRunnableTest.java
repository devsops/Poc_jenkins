package com.bosch.pai.ipsadmin.comms.operation;

import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PrepareForTest({POSTRunnable.class, HTTPRunnableTask.class})
public class POSTRunnableTest extends HTTPRunnableTaskTest {

    @Before
    public void init() throws Exception {
        super.init();
    }

    @Test
    public void testConstructorAndRun() {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, BASE_TEST_URL, END_POINT);
        requestObject.setMessageBody("DUMMY_DATA");
        final Map<String, String> headers = new HashMap<>();
        headers.put("Key1", "Value1");
        requestObject.setHeaders(headers);
        final POSTRunnable postRunnable = new POSTRunnable(UUID.randomUUID(), requestObject, retryConnectionHandler, commsListener);
        postRunnable.run();
        Mockito.verify(commsListener, Mockito.times(1)).onResponse(Mockito.any(ResponseObject.class));
    }
}