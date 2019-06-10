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
import com.bosch.pai.detection.authentication.AuthenticationCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;


public final class AuthenticationManager implements CommsListener {

    private static final String LOG_TAG = AuthenticationManager.class.getSimpleName();
    private String currentBaseUrl;
    private String currentCompanyId;
    private String currentPassword;
    private String currentUserId;
    private CommsManager commsManager;
    private AuthenticationCallback authenticationCallback;
    private InputStream cert;
    private UUID requestId;

    private static final int SUCCESS = 555;
    private static final int FAILURE = 888;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case SUCCESS:
                    if (authenticationCallback != null) {
                        authenticationCallback.onAuthenticationSuccess();
                    }
                    break;
                case FAILURE:
                    if (authenticationCallback != null) {
                        final String errorMessage = (String) message.obj;
                        authenticationCallback.onAuthenticationFail(errorMessage);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public AuthenticationManager(String companyId, String userId, String password, String baseUrl) {
        this.currentCompanyId = companyId;
        this.currentUserId = userId;
        this.currentPassword = password;
        this.currentBaseUrl = baseUrl;
    }


    private void initComms() {
        commsManager = CommsManager.getInstance();
    }

    public void authenticateUser(InputStream cert, AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
        initComms();
        this.cert = cert;
        commsManager.authenticate(currentBaseUrl, currentCompanyId, currentUserId, currentPassword, cert, this);
    }

    public void checkUserRole(final Context context, final AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
        initComms();
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, this.currentBaseUrl,
                "/registration/companies/" + this.currentCompanyId + "/users/" + this.currentUserId + "/roles/");
        if (Util.isHttpsURL(currentBaseUrl)) {
            cert = Util.getCertificate(context);
            requestObject.setCertFileStream(cert);
        }
        this.requestId = commsManager.processRequest(requestObject, this);
    }

    public void clearUserSession() {
        initComms();
        this.commsManager.clearSession();
    }

    @Override
    public void onResponse(ResponseObject responseObject) {
        if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK
                || responseObject.getStatusCode() == HttpURLConnection.HTTP_CREATED) {
            Log.d(LOG_TAG, "Authentication Success " + this.currentUserId);
            if (this.requestId == responseObject.getRequestID()) {
                final Gson gson = new GsonBuilder().create();
                try {
                    final Type type = new TypeToken<List<String>>() {
                    }.getType();
                    final List<String> userRoles =
                            gson.fromJson(responseObject.getResponseBody().toString(), type);
                    if (userRoles.contains(Util.UserType.ROLE_PREMIUM.toString())) {
                        Util.setUserType(Util.UserType.ROLE_PREMIUM);
                    } else if (userRoles.contains(Util.UserType.ROLE_PAID.toString())) {
                        Util.setUserType(Util.UserType.ROLE_PAID);
                    } else {
                        Util.setUserType(Util.UserType.ROLE_FREE);
                    }
                } catch (JsonSyntaxException e) {
                    Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Error parsing json! falling back to default user role : ROLE_FREE", null);
                    handler.sendMessage(handler.obtainMessage(FAILURE, "Error parsing JSON to check role!"));
                    return;
                }
            }
            handler.sendMessage(handler.obtainMessage(SUCCESS));

        } else if (responseObject.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            commsManager.registerUser(currentBaseUrl, currentCompanyId, currentUserId, currentPassword, this, this.cert);
        } else {
            Log.d(LOG_TAG, "Authentication Failed " + this.currentUserId);
            handler.sendMessage(handler.obtainMessage(FAILURE, "Failure: " + responseObject.getStatusMessage()));
        }
    }

    @Override
    public void onFailure(int i, String s) {
        if (i == HttpURLConnection.HTTP_UNAUTHORIZED) {
            commsManager.registerUser(currentBaseUrl, currentCompanyId, currentUserId, currentPassword, this, Util.getCertificate());
        } else {
            Log.d(LOG_TAG, "Authentication Failed " + this.currentUserId);
            handler.sendMessage(handler.obtainMessage(FAILURE, s));
        }
    }

}
