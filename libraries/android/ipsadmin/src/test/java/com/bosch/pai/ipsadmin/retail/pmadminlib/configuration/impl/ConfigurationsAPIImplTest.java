package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.CommsManager;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.ConfigurationAPI;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.callback.IConfigurationCallback;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommsManager.class, URLUtil.class, CommonUtil.class, Log.class,
        Util.class, Handler.class, ConfigurationAPI.class, ConfigurationsAPIImpl.class})
public class ConfigurationsAPIImplTest {

    @Mock
    private IConfigurationCallback.ISaveStoreLocationsCallback iSaveStoreLocationsCallback;
    @Mock
    private CommsManager commsManager;
    @Mock
    private InputStream inputStream;
    @Mock
    private ConfigurationAPI configurationAPI;
    @Mock
    private Handler handler;

    private ConfigurationsAPIImpl configurationsAPI;
    private StatusMessage statusMessage = new StatusMessage();
    private List<String> list = new ArrayList<>();

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(URLUtil.class);
        PowerMockito.mockStatic(CommsManager.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.when(Util.getCertificate(Mockito.any(Context.class))).thenReturn(inputStream);
        PowerMockito.when(URLUtil.isHttpsUrl(Mockito.anyString())).thenReturn(true);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        PowerMockito.when(CommsManager.getInstance()).thenReturn(commsManager);
        PowerMockito.when(CommonUtil.isResponseValid(Mockito.anyObject())).thenReturn(true);
        configurationAPI = ConfigurationsAPIImpl.getInstance();
    }


   @Test
    public void saveStoreLocationsTest(){
       ResponseObject responseObject = new ResponseObject();
       responseObject.setStatusCode(200);
       responseObject.setStatusMessage("Success");
       responseObject.setRequestID(UUID.randomUUID());
       responseObject.setResponseBody(new GsonBuilder().create().toJson(statusMessage));

       PowerMockito.when(commsManager.processRequest(Mockito.any(RequestObject.class), Mockito.any(CommsListener.class))).thenAnswer(new Answer<Object>() {
           @Override
           public Object answer(InvocationOnMock invocation) throws Throwable {
               ((CommsListener) invocation.getArguments()[1]).onResponse(responseObject);
               return null;
           }
       });

        configurationAPI.saveStoreLocations("company", "store", "siteName",Mockito.anySet(),"https://www.test.com/",iSaveStoreLocationsCallback);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }
}
