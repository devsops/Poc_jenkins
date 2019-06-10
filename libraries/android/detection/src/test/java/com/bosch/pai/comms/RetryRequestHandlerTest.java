package com.bosch.pai.comms;

import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.comms.util.CommsUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RetryRequestHandler.class, CommsUtil.class, CommsManager.class})
public class RetryRequestHandlerTest {

    @Mock
    private Timer timer;
    @Mock
    private InputStream inputStream;
    @Mock
    private CommsManager commsManager;
    @Mock
    private CommsListener commsListener;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(CommsUtil.class);
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.whenNew(Timer.class).withArguments(Mockito.anyString()).thenReturn(timer);
        PowerMockito.when(CommsUtil.getStreamCrtString(Mockito.anyString())).thenReturn(inputStream);
        PowerMockito.when(CommsUtil.convertCrtStreamToString(Mockito.any(InputStream.class))).thenReturn("TEST");
    }

    @Test
    public void testConstructorAndRun() {
        final String BASE_URL = "http://www.test.com/";
        final String API_END_POINT = "dummyEndPoint/";
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, BASE_URL, API_END_POINT);
        requestObject.setCertFileStream(inputStream);
        requestObject.setRetry(true);
        requestObject.setRetryCount(1);
        final RetryRequestHandler retryRequestHandler = new RetryRequestHandler(requestObject);
        retryRequestHandler.setCommsListener(commsListener);

        final ResponseObject responseObject = new ResponseObject();
        retryRequestHandler.onResponse(responseObject);
        Mockito.verify(commsListener, Mockito.times(1))
                .onResponse(Mockito.any(ResponseObject.class));

        retryRequestHandler.onFailure(-1, "TEST");
        Mockito.verify(timer, Mockito.times(1))
                .schedule(Mockito.any(TimerTask.class), Mockito.anyLong());

    }
}
