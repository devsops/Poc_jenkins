package com.bosch.pai.ipswrapper;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.webkit.URLUtil;

import com.bosch.pai.IeroIPSPlatformListener;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.detect.BearingDetector;
import com.bosch.pai.detection.Util;
import com.bosch.pai.detection.authentication.AuthenticationCallback;
import com.bosch.pai.detection.authentication.AuthenticationManager;
import com.bosch.pai.session.SessionHandler;

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
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IeroIPSPlatformImpl.class, BearingDetector.class, Util.class, SessionHandler.class,
        AuthenticationManager.class, ConfigurationSettings.class, ContextCompat.class, Handler.class, URLUtil.class})
public class IeroIPSPlatformImplTest {

    private IeroIPSPlatformImpl ieroIPSPlatform;
    private Map<Config.Key, Object> keyObjectMap = new HashMap<>();

    @Mock
    private SessionHandler sessionHandler;
    @Mock
    private Handler handler;
    @Mock
    private Context context;
    @Mock
    private IeroIPSPlatformListener platformListener;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BearingDetector bearingDetector;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private InputStream inputStream;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(BearingDetector.class);
        PowerMockito.mockStatic(SessionHandler.class);
        PowerMockito.mockStatic(URLUtil.class);
        PowerMockito.when(SessionHandler.getInstance(Mockito.any(Context.class))).thenReturn(sessionHandler);
        PowerMockito.when(Util.getCertificate()).thenReturn(inputStream);
        PowerMockito.when(URLUtil.isHttpsUrl(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(BearingDetector.getInstance(Mockito.any(Context.class))).thenReturn(bearingDetector);
        PowerMockito.whenNew(AuthenticationManager.class).withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
                .thenReturn(authenticationManager);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.
                when(ContextCompat.checkSelfPermission(Mockito.any(Context.class), Mockito.anyString())).thenReturn(0);
        PowerMockito.whenNew(Handler.class).withArguments(Handler.Callback.class).thenReturn(handler);
        final String COMPANY_ID = "COMPANY_ID";
        final String USER_ID = "USER_ID";
        final Config.SensorType SENSOR_TYPE = Config.SensorType.WIFI;
        keyObjectMap.put(Config.Key.COMPANY_ID, COMPANY_ID);
        keyObjectMap.put(Config.Key.UNIQUE_CLIENT_ID, USER_ID);
        keyObjectMap.put(Config.Key.SENSOR_TYPE, SENSOR_TYPE);
        ieroIPSPlatform = new IeroIPSPlatformImpl(context, platformListener);
    }

    @Test
    public void testRegister() throws Exception {
        PowerMockito.when(authenticationManager, "authenticateUser", Mockito.any(InputStream.class), Mockito.any(AuthenticationCallback.class))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        ((AuthenticationCallback) invocation.getArguments()[1]).onAuthenticationSuccess();
                        return null;
                    }
                });
        ieroIPSPlatform.register(keyObjectMap);
        Mockito.verify(authenticationManager, Mockito.times(1))
                .authenticateUser(Mockito.any(InputStream.class), Mockito.any(AuthenticationCallback.class));
        Mockito.verify(bearingDetector, Mockito.atLeastOnce())
                .download(Mockito.any(BearingConfiguration.class), Mockito.isNull(BearingData.class), Mockito.any(BearingCallBack.class));
    }
}
