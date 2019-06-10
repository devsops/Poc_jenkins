package com.bosch.pai.detection.authentication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.detection.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthenticationManager.class, CommsManager.class, Util.class, Log.class, Message.class, Handler.class})
public class AuthenticationManagerTest {

    @Mock
    private CommsManager commsManager;
    @Mock
    private AuthenticationCallback authenticationCallback;
    @Mock
    private InputStream inputStream;
    @Mock
    private Context context;
    @Mock
    private Handler handler;

    private AuthenticationManager authenticationManager;
    private UUID requestID;
    private Map<String, List<String>> headers = new HashMap<>();
    private final List<String> userRoles = new ArrayList<>();

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.mockStatic(Message.class);
        PowerMockito.when(Util.getCertificate(Mockito.any(Context.class))).thenReturn(inputStream);
        PowerMockito.when(Util.isHttpsURL(Mockito.anyString())).thenReturn(true);
        requestID = UUID.randomUUID();
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenReturn(requestID);
        String currentBaseUrl = "https://www.test.com/";
        String currentCompanyId = "COMPANY_ID";
        String currentCred = "cred";
        String currentUserId = "USER_ID";
        authenticationManager = new AuthenticationManager(currentCompanyId, currentUserId, currentCred, currentBaseUrl);
    }

    @Test
    public void testAuthenticateUser() {
        authenticationManager.authenticateUser(inputStream, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(InputStream.class), Mockito.any(CommsListener.class));
    }

    @Test
    public void testCheckUserRole() {
        userRoles.add(Util.UserType.ROLE_PREMIUM.toString());
        final Gson gson = new GsonBuilder().create();
        authenticationManager.checkUserRole(context, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRequestID(requestID);
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        authenticationManager.onFailure(401,"Failure");
        Mockito.verify(commsManager, Mockito.times(1)).registerUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(CommsListener.class), Mockito.any(InputStream.class));
    }

    @Test
    public void testCheckUserRoleFree() {
        userRoles.add(Util.UserType.ROLE_FREE.toString());
        final Gson gson = new GsonBuilder().create();
        authenticationManager.checkUserRole(context, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRequestID(requestID);
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        authenticationManager.onFailure(401,"Failure");
        Mockito.verify(commsManager, Mockito.times(1)).registerUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(CommsListener.class), Mockito.any(InputStream.class));
    }

    @Test
    public void testCheckUserRolePaid() {
        userRoles.add(Util.UserType.ROLE_PAID.toString());
        final Gson gson = new GsonBuilder().create();
        authenticationManager.checkUserRole(context, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRequestID(requestID);
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        authenticationManager.onFailure(401,"Failure");
        Mockito.verify(commsManager, Mockito.times(1)).registerUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(CommsListener.class), Mockito.any(InputStream.class));
    }

    @Test
    public void testCheckUserRoleFailure1() {
        userRoles.add(Util.UserType.ROLE_PREMIUM.toString());
        final Gson gson = new GsonBuilder().create();
        authenticationManager.checkUserRole(context, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRequestID(requestID);
        responseObject.setStatusCode(406);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void testCheckUserRoleFailure() {
        userRoles.add(Util.UserType.ROLE_PREMIUM.toString());
        final Gson gson = new GsonBuilder().create();
        authenticationManager.checkUserRole(context, authenticationCallback);
        Mockito.verify(commsManager, Mockito.times(1)).processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class));
        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRequestID(requestID);
        responseObject.setStatusCode(401);
        responseObject.setStatusMessage("Success");
        responseObject.setHeaders(headers);
        responseObject.setResponseBody(gson.toJson(userRoles));
        authenticationManager.onResponse(responseObject);
        Mockito.verify(commsManager, Mockito.times(1)).registerUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(CommsListener.class), Mockito.any(InputStream.class));
        authenticationManager.onFailure(405,"Failure");
    }
}
