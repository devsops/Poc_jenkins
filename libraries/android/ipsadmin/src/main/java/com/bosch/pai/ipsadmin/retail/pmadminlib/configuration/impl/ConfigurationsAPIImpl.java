package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.impl;

import android.os.Handler;
import android.util.Log;

import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.ConfigurationAPI;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.callback.IConfigurationCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.util.ConfigurationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

public final class ConfigurationsAPIImpl implements ConfigurationAPI {

    private static final String LOG_TAG = ConfigurationsAPIImpl.class.getSimpleName();

    private static final int FAILURE = 404;
    private static final int SUCCESS = 200;
    private static final int ALREADY_EXIST = 555;

    private ConfigurationsAPIImpl() {
        //default
    }

    public static ConfigurationAPI getInstance() {
        return new ConfigurationsAPIImpl();
    }

    @Override
    public void saveStoreLocations(String company, String store, String siteName, Set<String> locationNames, String baseUrl, IConfigurationCallback.ISaveStoreLocationsCallback listener) {
        final Handler handler = new Handler(message -> {
            if (message.what == FAILURE) {
                final String errorMessage = (String) message.obj;
                listener.onFailure(errorMessage);
            } else if (message.what == SUCCESS) {
                listener.onSuccess();
            }
            return false;
        });

        final String configurationEndpoint =
                ConfigurationUtil.getSaveStoreLocationsEndpoint(company, store, siteName);

        final String proximityUrl = baseUrl + CommonUtil.getProximityConfigurationEndPoint();

        final RequestObject requestObject =
                new RequestObject(RequestObject.RequestType.POST, proximityUrl, configurationEndpoint);

        final Gson gson = new GsonBuilder().create();

        requestObject.setMessageBody(gson.toJson(locationNames));

        CommsManager.getInstance().processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(final ResponseObject responseObject) {
                handleStatusMessageResponse(responseObject, gson, handler);
            }

            @Override
            public void onFailure(int i, final String s) {
                handler.sendMessage(handler.obtainMessage(FAILURE, s));
            }
        });
    }

    private void handleStatusMessageResponse(final ResponseObject responseObject, final Gson gson, final Handler handler) {
        try {
            if (CommonUtil.isResponseValid(responseObject)) {
                final Type type = new TypeToken<StatusMessage>() {
                }.getType();
                final StatusMessage statusMessage = gson.fromJson(responseObject.getResponseBody().toString(), type);

                if (statusMessage != null && statusMessage.getStatus().equals(StatusMessage.STATUS.SUCCESS)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    handler.sendMessage(handler.obtainMessage(FAILURE, responseObject.getStatusMessage()));
                }
            } else {
                handler.sendMessage(handler.obtainMessage(FAILURE, responseObject.getStatusMessage()));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Comms fail: ", e);
            handler.sendMessage(handler.obtainMessage(FAILURE, " " + e));
        }
    }

}
