package com.bosch.pai.ipsadmin.retail.pmadminlib;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.bosch.pai.ipsadmin.comms.model.RequestObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class Utiltest {
    private ByteArrayInputStream byteArrayInputStream;

    @Mock
    Context context;
    @Mock
    private Resources resources;

    @Before
    public void initialize() {
        PowerMockito.mock(Context.class);
        PowerMockito.mock(Resources.class);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        final InputStream certStream = new ByteArrayInputStream("Certificate".getBytes());
        PowerMockito.when(context.getResources().openRawResource(Mockito.anyInt())).thenReturn(certStream);
    }

    @Test
    public void getSHA256ConversionTest() {
        String currentUserId = "USER_ID";
        String result = "E5BB97D1792FF76E360CD8E928B6B9B53BDA3E4FE88B026E961C2FACF963A361";
        Assert.assertEquals(result, Util.getSHA256Conversion(currentUserId));
    }

    @Test
    public void getAndSetCertificateTest() {
        final InputStream certStream = new ByteArrayInputStream("Certificate".getBytes());
        Util.setCertificate(certStream);

        try {
            final InputStream test1Stream = new ByteArrayInputStream("Certificate".getBytes());
            Assert.assertEquals(test1Stream.available(), Objects.requireNonNull(Util.getCertificate()).available());
        } catch (IOException e) {
            Log.e("", "testSetAndGetCertificate: ", e);
        }
    }

    @Test
    public void getCertificateTest() {
        byteArrayInputStream=new ByteArrayInputStream("Test".getBytes());
        Util.setCertificate(byteArrayInputStream);
        Assert.assertNotEquals(byteArrayInputStream, Util.getCertificate(context));
    }

    @Test
    public void httptest() {
        String currentBaseUrl = "https://www.test.com/";
        Assert.assertTrue(currentBaseUrl,Util.isHttpsURL(currentBaseUrl));
    }

    @Test
    public void userTypeTest(){
        Util.setUserType(Util.UserType.ROLE_FREE);
        Assert.assertEquals(Util.UserType.ROLE_FREE,Util.getUserType());
    }

    @Test
    public void addLogsTest(){
        Exception exception = null;
        Util.addLogs(Util.LOG_STATUS.DEBUG,"TAG","message", exception);
    }

    @Test
    public void addCertificationTest(){
        byteArrayInputStream=new ByteArrayInputStream("Test".getBytes());
        RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, "","");
        Util.addCertification(requestObject, byteArrayInputStream, "https://www.test.com/");
    }
}
