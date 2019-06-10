package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.impl;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.Header;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.ipsadmin.bearing.detect.BearingDetector;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionFromServer;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionMode;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback.IBearingDetectionCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.config.DetectionConfig;

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingDetectionFromServer.class, DetectionMode.class, DetectionFromServer.class, BearingDetector.class, DetectionConfig.class,
        ConfigurationSettings.class, Handler.class, Util.class, Log.class, URLUtil.class})
public class BearingDetectionFromServerTest {

    private DetectionMode detectionMode;
    private BearingOutput bearingOutput;

    @Mock
    private DetectionFromServer detectionFromServer;
    @Mock
    private Context context;
    @Mock
    private Sensor sensor;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private InputStream inputStream;
    @Mock
    private Handler handler;
    @Mock
    private Resources resources;
    @Mock
    private BearingDetector bearingDetector;
    @Mock
    private IBearingDetectionCallback.ISetBearingServerEndpointForDetection iSetBearingServerEndpointForDetection;
    @Mock
    private IBearingDetectionCallback.IBearingStartSiteDetectionListener iBearingStartSiteDetectionListener;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(BearingDetector.class);
        PowerMockito.mockStatic(DetectionConfig.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mock(Resources.class);
        PowerMockito.mockStatic(URLUtil.class);
        PowerMockito.when(URLUtil.isHttpsUrl(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        final InputStream certStream = new ByteArrayInputStream("Certificate".getBytes());
        PowerMockito.when(context.getResources().openRawResource(Mockito.anyInt())).thenReturn(certStream);
        PowerMockito.when(Util.getCertificate(Mockito.any(Context.class))).thenReturn(inputStream);
        PowerMockito.when(ConfigurationSettings.saveConfigObject(Mockito.any(ConfigurationSettings.class))).thenReturn(true);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class),Mockito.anyObject())).thenReturn(sensor);
        PowerMockito.when(configurationSettings.withSensorPreferences(Mockito.any(Sensor.class))).thenReturn(configurationSettings);
        PowerMockito.when(BearingDetector.getInstance(Mockito.any(Context.class))).thenReturn(bearingDetector);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);

        detectionFromServer = BearingDetectionFromServer.getInstance(context, detectionMode);
    }

    @Test
    public void setServerEndPointTest() {
        detectionFromServer.setServerEndPoint("serverEndPoint", inputStream, iSetBearingServerEndpointForDetection);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void storeBearingDataExternalTest() {
        detectionFromServer.storeBearingData(true, context);
        Mockito.verify(configurationSettings, Mockito.times(1))
                .withDataStoragePathPreference(Mockito.any(ConfigurationSettings.DataStorePathPreference.EXTERNAL.getDeclaringClass()));
    }

    @Test
    public void storeBearingDataInternalTest() {
        detectionFromServer.storeBearingData(false, context);
        Mockito.verify(configurationSettings, Mockito.times(1))
                .withDataStoragePathPreference(Mockito.any(ConfigurationSettings.DataStorePathPreference.INTERNAL.getDeclaringClass()));
    }

    /*@Test
    public void startSiteDetectionTest(){
        BearingOutput bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.OK);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setErrorMessage("Error");
        body.setOutput("Output");
        body.setTimestamp("102145");
        body.setSnapshotObservations(new ArrayList<>());
        bearingOutput.setBody(body);

        PowerMockito.when(bearingDetector.invoke(Mockito.anyBoolean(), Mockito.any(BearingConfiguration.class), Mockito.any(BearingData.class), Mockito.any(BearingCallBack.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        ((BearingCallBack)invocation.getArguments()[3]).onLocationResponse(bearingOutput);
                        return null;
                    }
                });

        detectionFromServer.startSiteDetection(iBearingStartSiteDetectionListener);
        Mockito.verify(handler, Mockito.atLeastOnce())
                .sendMessage(Mockito.any(Message.class));
    }*/
}
