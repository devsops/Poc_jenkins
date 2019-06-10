package com.bosch.pai.comms.operation;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.RetryConnectionHandler;
import com.bosch.pai.comms.model.ResponseObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class HTTPRunnableTaskTest {

    @Mock
    protected CommsListener commsListener;
    @Mock
    protected RetryConnectionHandler retryConnectionHandler;
    @Mock
    private HttpURLConnection httpURLConnection;

    @Mock
    private OutputStream outputStream;
    @Mock
    private InputStream inputStream;
    @Mock
    private InputStreamReader inputStreamReader;
    @Mock
    private BufferedReader bufferedReader;
    @Mock
    private URL url;

    protected Map<String, List<String>> HEADERS = new HashMap<>();
    protected final String RESPONSE_MSG = "RESPONSE_MSG";
    protected final String BASE_TEST_URL = "http://www.test.com/";
    protected final String END_POINT = "testEndPoint/";
    protected List<String> strings = new ArrayList<>();

    public void init() throws Exception {
        HEADERS.put("KEY1", strings);
        PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(url);
        PowerMockito.when(url.openConnection()).thenReturn(httpURLConnection);
        PowerMockito.doNothing().when(httpURLConnection, "setConnectTimeout", Mockito.anyInt());
        PowerMockito.doNothing().when(httpURLConnection, "setReadTimeout", Mockito.anyInt());
        PowerMockito.doNothing().when(httpURLConnection, "setRequestProperty", Mockito.anyString(), Mockito.anyString());
        PowerMockito.doNothing().when(httpURLConnection, "setRequestMethod", Mockito.anyString());
        PowerMockito.doNothing().when(httpURLConnection, "setDoOutput", Mockito.anyBoolean());
        PowerMockito.doNothing().when(httpURLConnection, "setFixedLengthStreamingMode", Mockito.anyInt());
        PowerMockito.doNothing().when(httpURLConnection, "connect");
        PowerMockito.when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        PowerMockito.when(httpURLConnection.getHeaderFields()).thenReturn(HEADERS);
        PowerMockito.when(httpURLConnection.getResponseMessage()).thenReturn(RESPONSE_MSG);
        PowerMockito.when(httpURLConnection.getInputStream()).thenReturn(inputStream);
        PowerMockito.when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        PowerMockito.whenNew(InputStreamReader.class).withArguments(Mockito.any(InputStream.class)).thenReturn(inputStreamReader);
        PowerMockito.whenNew(BufferedReader.class).withArguments(Mockito.any(InputStreamReader.class)).thenReturn(bufferedReader);
        PowerMockito.when(bufferedReader.readLine()).thenReturn(RESPONSE_MSG).thenReturn(null);
        PowerMockito.doNothing().when(outputStream, "write", (Mockito.any()));
        PowerMockito.doNothing().when(outputStream, "flush");
        PowerMockito.doNothing().when(commsListener, "onResponse", Mockito.any(ResponseObject.class));
        PowerMockito.doNothing().when(retryConnectionHandler,
                "waitForConnectionRestore", Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    public void helpTest(){

    }
}
