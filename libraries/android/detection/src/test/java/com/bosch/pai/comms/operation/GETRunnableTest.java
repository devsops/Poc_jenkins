package com.bosch.pai.comms.operation;

import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PrepareForTest({HTTPRunnableTask.class, GETRunnable.class})
public class GETRunnableTest extends HTTPRunnableTaskTest {
    @Before
    public void init() throws Exception {
        super.init();
    }

    @Test
    public void testConstructorAndRun() {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, BASE_TEST_URL, END_POINT);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("KEY1", "VALUE1");
        queryParams.put("KEY2", "VALUE2");
        queryParams.put("KEY3", "VALUE3");
        requestObject.setQueryParams(queryParams);
        final GETRunnable getRunnable = new GETRunnable(UUID.randomUUID(), requestObject, retryConnectionHandler, commsListener);
        getRunnable.run();
        Mockito.verify(commsListener, Mockito.times(1)).onResponse(Mockito.any(ResponseObject.class));
    }
}
