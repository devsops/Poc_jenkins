package com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.config.AnalyticsConfig;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationManager;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.GsonTimestampAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AnalyticsImpl implements Analytics {


    private static final int FAILURE = 404;
    private static final int SUCCESS = 200;

    private static final String LOG_TAG = AnalyticsImpl.class.getSimpleName();

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String REQUEST_INTERVAL = "requestInterval";
    private Gson gson;
    private String proximityUrl;

    private AnalyticsImpl(String proximityUrl) {
        this.proximityUrl = proximityUrl;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new GsonTimestampAdapter())
                .create();
    }

    public static Analytics getInstance(String proximityUrl) {
        return new AnalyticsImpl(proximityUrl);
    }


    @Override
    public void onAuthentication(Context context,
                                 String companyName,
                                 String userName, String password,
                                 String authenticationUrl,
                                 final IAnalyticsCallbacks.IAuthenticationListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.failure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        InputStream cert = null;
        if (Util.isHttpsURL(authenticationUrl)) {
            cert = Util.getCertificate(context);
        }

        final AuthenticationManager authenticationManagerBearing = new AuthenticationManager(
                companyName, userName,
                password, authenticationUrl);

        authenticationManagerBearing.authenticateUser(cert, new AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                handler.sendMessage(handler.obtainMessage(SUCCESS));
            }

            @Override
            public void onAuthenticationFail(final String message) {
                handler.sendMessage(handler.obtainMessage(FAILURE, message));
            }
        });
    }

    @Override
    public void getDwellTimeAnalytics(String company,
                                      String store,
                                      String siteName,
                                      String locationName,
                                      Long startTime,
                                      Long endTime, final IAnalyticsCallbacks.IDwelltimeListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    List<LocationDwellTime> locationDwellTimeList = (List<LocationDwellTime>) message.obj;
                    listener.onSuccess(Collections.unmodifiableList(locationDwellTimeList));
                }
                return false;
            }
        });

        final String analyticsUrl = proximityUrl + CommonUtil.getAnalyticsEndPoint();
        final String dwellTimeEndpoint = AnalyticsConfig.getDwellTimeUrl(company, store, siteName, locationName);

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, analyticsUrl, dwellTimeEndpoint);
        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put(START_TIME, gson.toJson(startTime));
        queryParam.put(END_TIME, gson.toJson(endTime));
        requestObject.setQueryParams(queryParam);

        CommsManager.getInstance().processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (CommonUtil.isResponseValid(responseObject)) {
                    final Type type = new TypeToken<List<LocationDwellTime>>() {
                    }.getType();
                    final List<LocationDwellTime> locationDwellTimeList = gson.fromJson(responseObject.getResponseBody().toString(), type);
                    handler.sendMessage(handler.obtainMessage(SUCCESS, locationDwellTimeList));
                } else {
                    handler.sendMessage(handler.obtainMessage(FAILURE, CommonUtil.getErrorMessageFromResponse(responseObject)));
                }
            }

            @Override
            public void onFailure(final int errorCode, final String errorMessage) {
                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));

            }
        });

    }

    @Override
    public void getHeatMapDetails(String company, String store, String siteName, String locationName, Long startTime, Long endTime, final IAnalyticsCallbacks.IHeatmapListener listener) {


        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    List<HeatMapDetail> heatMapDetails = (List<HeatMapDetail>) message.obj;
                    listener.onSuccess(Collections.unmodifiableList(heatMapDetails));
                }
                return false;
            }
        });

        final String analyticsUrl = proximityUrl + CommonUtil.getAnalyticsEndPoint();
        final String heatmapEndpoint = AnalyticsConfig.getHeatmapEndpoint(company, store, siteName, locationName);

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, analyticsUrl, heatmapEndpoint);
        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put(START_TIME, gson.toJson(startTime));
        queryParam.put(END_TIME, gson.toJson(endTime));
        requestObject.setQueryParams(queryParam);

        CommsManager.getInstance().processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (CommonUtil.isResponseValid(responseObject)) {
                    final Type type = new TypeToken<List<HeatMapDetail>>() {
                    }.getType();
                    final List<HeatMapDetail> heatMapDetails = gson.fromJson(responseObject.getResponseBody().toString(), type);
                    handler.sendMessage(handler.obtainMessage(SUCCESS, heatMapDetails));
                } else {
                    handler.sendMessage(handler.obtainMessage(FAILURE, CommonUtil.getErrorMessageFromResponse(responseObject)));
                }
            }

            @Override
            public void onFailure(final int errorCode, final String errorMessage) {
                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
            }
        });

    }

    @Override
    public void getOfferAnalytics(
            String company,
            String store,
            String siteName,
            String locationName,
            Long startTime,
            Long endTime,
            final IAnalyticsCallbacks.IOfferAnalyticstListener listener) {


        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    List<OfferAnalyticsResponse> offerAnalyticsResponses = (List<OfferAnalyticsResponse>) message.obj;
                    listener.onSuccess(Collections.unmodifiableList(offerAnalyticsResponses));
                }
                return false;
            }
        });

        final String analyticsUrl = proximityUrl + CommonUtil.getAnalyticsEndPoint();
        final String offerAnalyticsEndpoint = AnalyticsConfig.getOfferAnalyticsEndpoint(company, store, siteName, locationName);

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, analyticsUrl, offerAnalyticsEndpoint);
        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put(START_TIME, gson.toJson(startTime));
        queryParam.put(END_TIME, gson.toJson(endTime));
        requestObject.setQueryParams(queryParam);

        CommsManager.getInstance().processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (CommonUtil.isResponseValid(responseObject)) {
                    final Type type = new TypeToken<List<OfferAnalyticsResponse>>() {
                    }.getType();
                    final List<OfferAnalyticsResponse> offerAnalyticsResponseList = gson.fromJson(responseObject.getResponseBody().toString(), type);
                    handler.sendMessage(handler.obtainMessage(SUCCESS, offerAnalyticsResponseList));

                } else {
                    handler.sendMessage(handler.obtainMessage(FAILURE, CommonUtil.getErrorMessageFromResponse(responseObject)));
                }
            }

            @Override
            public void onFailure(final int errorCode, final String errorMessage) {
                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
            }
        });

    }

    @Override
    public void getEntryExitDetails(
            final String company,
            final String store,
            final Long startTime,
            final Long endTime,
            final IntervalDetails requestinterval,
            final IAnalyticsCallbacks.IEntryExitListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final EntryExitResponse entryExitResponses = (EntryExitResponse) message.obj;
                    final EntryExitResponse newEntryExitResponses = new EntryExitResponse();
                    newEntryExitResponses.setEntryExitDetails(entryExitResponses.getEntryExitDetails());
                    newEntryExitResponses.setIntervalDetails(entryExitResponses.getIntervalDetails());
                    newEntryExitResponses.setStatusMessage(entryExitResponses.getStatusMessage());
                    listener.onSuccess(newEntryExitResponses);
                }
                return false;
            }
        });

        final String analyticsUrl = proximityUrl + CommonUtil.getAnalyticsEndPoint();
        final String entryexitEndpoint = AnalyticsConfig.getEntryExitEndpoint(company, store);

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, analyticsUrl, entryexitEndpoint);
        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put(START_TIME, gson.toJson(startTime));
        queryParam.put(END_TIME, gson.toJson(endTime));
        if (requestinterval != null) {
            queryParam.put(REQUEST_INTERVAL, gson.toJson(requestinterval));
        }
        requestObject.setQueryParams(queryParam);

        CommsManager.getInstance().processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                handleEntryExitResponse(responseObject, handler);
            }

            @Override
            public void onFailure(final int errorCode, final String errorMessage) {
                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
            }
        });

    }

    private void handleEntryExitResponse(ResponseObject responseObject, Handler handler) {
        try {
            if (CommonUtil.isResponseValid(responseObject)) {
                final Type type = new TypeToken<EntryExitResponse>() {
                }.getType();
                final EntryExitResponse entryExitResponse =
                        gson.fromJson(responseObject.getResponseBody().toString(), type);
                if (entryExitResponse != null &&
                        entryExitResponse.getStatusMessage() != null &&
                        entryExitResponse.getStatusMessage().getStatus().equals(StatusMessage.STATUS.SUCCESS)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS, entryExitResponse));
                } else {
                    handler.sendMessage(handler.obtainMessage(FAILURE, CommonUtil.getErrorMessageFromResponse(responseObject)));
                }
            } else {
                handler.sendMessage(handler.obtainMessage(FAILURE, CommonUtil.getErrorMessageFromResponse(responseObject)));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e, e);
            handler.sendMessage(handler.obtainMessage(FAILURE, " " + e));
        }
    }
}