package com.bosch.pai.ipsadmin.retail.pmadminlib.authentication;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtilTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.tools.ant.taskdefs.Length;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommsManager.class, AuthenticationManager.class, Util.class, CommonUtil.class, Log.class, Handler.class, Message.class, CommsListener.class, ResponseObject.class})
public class AuthennticationManagerTest {

    AuthenticationManager authenticationManager;

    @Mock
    private Context context;

    @Mock
    AuthenticationCallback authenticationCallback;

    @Mock
    CommsManager commsManager;

    @Mock
    InputStream inputStream;
    @Mock
    Handler handler;
    private Map<String, List<String>> headers = new HashMap<>();
    private final List<String> userRoles = new ArrayList<>();


    private String testBaseUrl;
    private String testCompanyId;
    private String testCred;
    private String testUserId;

    @Before
    public void init() throws Exception {
        headers.put(Util.UserType.ROLE_PAID.toString(), new ArrayList<>());
        headers.put(Util.UserType.ROLE_FREE.toString(), new ArrayList<>());
        PowerMockito.mockStatic(CommsManager.class);

        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        PowerMockito.when(Util.isHttpsURL(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(Util.getCertificate(context)).thenReturn(inputStream);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.when(Util.getCertificate(any(Context.class))).thenReturn(inputStream);
        authenticationManager = new AuthenticationManager("testCompID", "testUserID", "testCred", "testBaseurl");
    }

    @Test
    public void testCponstructor() {
        authenticationManager = new AuthenticationManager("testCompID", "testUserID", "testCred", "testBaseurl");
        this.testBaseUrl = "testBaseurl";
        this.testCompanyId = "testCompID";
        this.testCred = "testCred";
        this.testUserId = "testUserID";
    }

    @Test
    public void testChangecred() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, "Test1", "Test2");
        requestObject.setCertFileStream(inputStream);
        requestObject.setMessageBody("DemoMessage");

        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });

        authenticationManager.changePassword(context, "testOldcred", "testNewcred", authenticationCallback);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
        /*Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(), any(CommsListener.class));*/
        /*Assert.assertEquals(inputStream, Util.getCertificate(context));*/
    }

    @Test
    public void testAutenticateUser() {
        authenticationManager.authenticateUser(inputStream, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), any(InputStream.class), any(CommsListener.class));
    }

    @Test
    public void testgetCredValidityDays() {
        final Gson gson = new GsonBuilder().create();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, "Test1", "Test2");
        requestObject.setCertFileStream(inputStream);
        requestObject.setMessageBody("DemoMessage");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });

        authenticationManager.checkUserRole(context, authenticationCallback);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
        //Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(), any(CommsListener.class));
    }

    @Test
    public void testResponse() {
        userRoles.add(Util.UserType.ROLE_PREMIUM.toString());
        final Gson gson = new GsonBuilder().create();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
        authenticationManager.onFailure(4, "Test2");
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void testResponsePaid() {
        userRoles.add(Util.UserType.ROLE_PAID.toString());
        final Gson gson = new GsonBuilder().create();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
        authenticationManager.onFailure(4, "Test2");
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void testResponsePaidFree() {
        userRoles.add(Util.UserType.ROLE_FREE.toString());
        final Gson gson = new GsonBuilder().create();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
        authenticationManager.onFailure(4, "Test2");
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void testResponseFailure() {
        userRoles.add(Util.UserType.ROLE_FREE.toString());
        final Gson gson = new GsonBuilder().create();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(406);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
        authenticationManager.onFailure(4, "Test2");
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
    }
}
