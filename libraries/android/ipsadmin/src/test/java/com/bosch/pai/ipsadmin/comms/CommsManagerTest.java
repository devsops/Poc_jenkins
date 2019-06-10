package com.bosch.pai.ipsadmin.comms;

import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommsManager.class, Executors.class})
public class CommsManagerTest {

    private final String BASE_TEST_URL = "http://www.test.com/";
    private final String USER_ID = "USER_ID";
    private final String COMPANY_ID = "COMPANY_ID";
    private final String TEST_CRED = "TEST_CRED";

    @Mock
    private CommsListener commsListener;
    @Mock
    private ExecutorService executors;

    private CommsManager commsManager;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Executors.class);
        PowerMockito.when(Executors.newFixedThreadPool(Mockito.anyInt())).thenReturn(executors);
        PowerMockito.doNothing().when(executors, "execute", Mockito.any(Runnable.class));
        this.commsManager = CommsManager.getInstance();
    }

    @Test
    public void testPublicAPIs() {
        this.commsManager.authenticate(BASE_TEST_URL, COMPANY_ID, USER_ID, TEST_CRED, null, commsListener);
        this.commsManager.registerUser(BASE_TEST_URL, COMPANY_ID, USER_ID, TEST_CRED, commsListener, null);
        final String END_POINT = "endPoint/";
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, BASE_TEST_URL, END_POINT);
        final Map<String, String> headers = new HashMap<>();
        headers.put("Key1", "Value1");
        requestObject.setHeaders(headers);
        this.commsManager.processRequest(requestObject, commsListener);
        Mockito.verify(executors, Mockito.times(3)).execute(Mockito.any(Runnable.class));
    }
}
