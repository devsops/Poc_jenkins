package com.bosch.pai.session;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.config.Config;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.detection.Util;
import com.bosch.pai.detection.models.StatusMessage;
import com.google.gson.Gson;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SessionHandler.class, Util.class, File.class, FileReader.class, FileWriter.class,
        Log.class, CommsManager.class, Context.class, SessionInfo.class, SubSessionInfo.class, Config.class, URLUtil.class})
public class SessionHandlerTest {

    @Mock
    private CommsManager commsManager;
    @Mock
    private Context context;
    @Mock
    private File file;
    @Mock
    private FileReader fileReader;
    @Mock
    private BufferedReader bufferedReader;
    @Mock
    private FileWriter fileWriter;
    @Mock
    private BufferedWriter bufferedWriter;
    @Mock
    private SubSessionInfo subSessionInfo;
    @Mock
    private InputStream inputStream;
    @Mock
    private Util util;

    private SessionHandler sessionHandler;
    private final List<SubSessionInfo> subSessionInfos = new ArrayList<>();

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(SubSessionInfo.class);
        PowerMockito.mockStatic(SessionInfo.class);
        PowerMockito.mockStatic(Config.class);
        PowerMockito.mockStatic(URLUtil.class);
        final Gson gson = new GsonBuilder().create();

        final SubSessionInfo subSessionInfo = new SubSessionInfo("userId", "siteName", "locationName");
        subSessionInfo.setStartTime(1021);
        subSessionInfo.setValid(true);
        subSessionInfo.setStoreId("storeId");
        subSessionInfo.setEndTime(1021);
        subSessionInfos.add(subSessionInfo);

        PowerMockito.when(context.getFilesDir()).thenReturn(new File("testDir" + File.separator));
        sessionHandler = SessionHandler.getInstance(context);
        PowerMockito.when(Util.getCertificate(Mockito.any(Context.class))).thenReturn(inputStream);
        PowerMockito.when(Util.isHttpsURL(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(URLUtil.isHttpsUrl(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class)))
                .thenReturn(null);
        PowerMockito.when(context.getFilesDir()).thenReturn(file);
        PowerMockito.when(context.getFilesDir().getPath()).thenReturn(File.separator);

        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(file);
        PowerMockito.whenNew(FileReader.class).withAnyArguments().thenReturn(fileReader);

        PowerMockito.whenNew(BufferedReader.class).withArguments(Mockito.any(FileReader.class)).thenReturn(bufferedReader);
        PowerMockito.when(bufferedReader.readLine()).thenReturn(gson.toJson(subSessionInfos)).thenReturn(null);
        PowerMockito.whenNew(FileWriter.class).withAnyArguments().thenReturn(fileWriter);
        PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(bufferedWriter);
        PowerMockito.doNothing().when(bufferedWriter, "write", Mockito.anyString());
    }

    @Test
    public void endSessionTest() {
        sessionHandler.endSession("sessionId");
    }

    @Test
    public void startSubSessionTest() {
        sessionHandler.startSubSession("sessionId", subSessionInfo);
    }

    @Test
    public void startSessionTest() {
        sessionHandler.startSession("userId", "storeId");
    }

    @Test
    public void uploadPreviousSessionDataIfAvailableTest() {
        final StatusMessage statusMessage = new StatusMessage();
        statusMessage.setStatus(StatusMessage.STATUS.SUCCESS);
        statusMessage.setMessage("SUCCESS");
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ResponseObject responseObject = new ResponseObject();
                responseObject.setStatusCode(200);
                responseObject.setResponseBody(new GsonBuilder().create().toJson(statusMessage));
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });

        sessionHandler.uploadPreviousSessionDataIfAvailable(context, "companyId");
        Mockito.verify(util,Mockito.atLeastOnce()).addLogs(Mockito.any(Util.LOG_STATUS.class), Mockito.anyString(),Mockito.anyString(), Mockito.any(Exception.class));
        //Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
    }
}
