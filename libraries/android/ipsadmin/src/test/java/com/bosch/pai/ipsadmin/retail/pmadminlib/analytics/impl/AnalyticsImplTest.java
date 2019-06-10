package com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.impl;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.config.AnalyticsConfig;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationManager;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.math3.analysis.function.Power;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AnalyticsImpl.class, Analytics.class, InputStream.class, Handler.class, Util.class, URLUtil.class,
        CommonUtil.class, AuthenticationManager.class, Context.class, AnalyticsConfig.class, CommsManager.class, Log.class})
public class AnalyticsImplTest {
    private static final String COMPANIES = "TESTCOM/";
    private static final String STORES = "/TESTSTR/";
    private static final String SITES = "/TESTSITE/";
    private static final String LOCATIONS = "/TESTLOC/";
    private static final String STARTTIME = "/TESTLOC/";
    private static final String ENDTIME = "/TESTLOC/";
    private AnalyticsImpl analyticsimpl;
    private String proximityUrl;

    private Map<String, List<String>> filterOptionsMap = new HashMap<>();
    private long startTime;
    private long endTime;
    private List<LocationDwellTime> locationDwellTimeList = new ArrayList<>();
    private List<HeatMapDetail> heatMapDetails = new ArrayList<>();
    private List<OfferAnalyticsResponse> offerAnalyticsResponseList = new ArrayList<>();
    private EntryExitResponse entryExitResponse = new EntryExitResponse();

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Analytics analytics;
    @Mock
    private Context context;
    @Mock
    private IAnalyticsCallbacks.IAuthenticationListener iAuthenticationListener;
    @Mock
    private IAnalyticsCallbacks.IDwelltimeListener iDwelltimeListener;
    @Mock
    private IAnalyticsCallbacks.IHeatmapListener iHeatmapListener;
    @Mock
    private IAnalyticsCallbacks.IOfferAnalyticstListener iOfferAnalyticstListener;
    @Mock
    private IAnalyticsCallbacks.IEntryExitListener iEntryExitListener;
    @Mock
    private InputStream inputStream;
    @Mock
    private Handler handler;
    @Mock
    private AuthenticationCallback authenticationCallback;
    @Mock
    private AnalyticsConfig analyticsConfig;
    @Mock
    private CommsManager commsManager;

    String currentBaseUrl = "https://www.test.com/";
    String companyName = "COMPANY_ID";
    String currentCred = "cred";
    String userName = "USER_ID";


    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Analytics.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(URLUtil.class);
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(AnalyticsConfig.class);
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Util.getCertificate(Mockito.any(Context.class))).thenReturn(inputStream);
        PowerMockito.when(URLUtil.isHttpsUrl(Mockito.anyString())).thenReturn(true);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.when(CommonUtil.isResponseValid(Mockito.anyObject())).thenReturn(true);
        PowerMockito.whenNew(AuthenticationManager.class).withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
                .thenReturn(authenticationManager);
        analytics = AnalyticsImpl.getInstance(proximityUrl);
    }

    @Test
    public void testAuthenticateUserTest() {
        analytics.onAuthentication(context,companyName,userName,currentCred,currentBaseUrl,iAuthenticationListener);
        Mockito.verify(authenticationManager,Mockito.times(1)).authenticateUser(Mockito.any(InputStream.class),Mockito.any(AuthenticationCallback.class));
    }

    @Test
    public void getDwellTimeAnalyticsTest() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setRequestID(UUID.randomUUID());
        responseObject.setResponseBody(locationDwellTimeList);

        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        analytics.getDwellTimeAnalytics("company", "store", "siteName", "locationName",
                startTime, endTime, iDwelltimeListener);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void getHeatMapDetailsTest(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setRequestID(UUID.randomUUID());
        responseObject.setResponseBody(heatMapDetails);

        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        analytics.getHeatMapDetails("company", "store", "siteName", "locationName",
                startTime, endTime, iHeatmapListener);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void getOfferAnalyticsTest(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setRequestID(UUID.randomUUID());
        responseObject.setResponseBody(offerAnalyticsResponseList);

        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        analytics.getOfferAnalytics("company", "store", "siteName", "locationName",
                startTime, endTime, iOfferAnalyticstListener);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void getEntryExitDetailsTest(){
        final Gson gson = new GsonBuilder().create();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatusCode(200);
        responseObject.setStatusMessage("Success");
        responseObject.setRequestID(UUID.randomUUID());
        responseObject.setResponseBody(entryExitResponse);


        PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CommsListener)invocation.getArguments()[1]).onResponse(responseObject);
                return null;
            }
        });
        analytics.getEntryExitDetails("company", "store", startTime, endTime, IntervalDetails.YEARLY ,iEntryExitListener);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

}