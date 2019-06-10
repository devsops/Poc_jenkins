package com.bosch.pai.detection;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.bosch.pai.comms.model.RequestObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {

    private String currentBaseUrl = "https://www.test.com/";

    @Mock
    private Context context;
    @Mock
    private Resources resources;
    @Mock
    private InputStream inputStream;

    @Before
    public void init() throws Exception {

    }

    @Before
    public void initialize() {
        PowerMockito.mock(Context.class);
        PowerMockito.mock(Resources.class);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(context.getResources().openRawResource(Mockito.anyInt())).thenReturn(inputStream);
    }

    @Test
    public void isHttpsURLTest() {
        Assert.assertTrue(currentBaseUrl,Util.isHttpsURL(currentBaseUrl));
        Exception ex = null;
        Util.addLogs(Util.LOG_STATUS.DEBUG, "tag", "message", ex);
    }

    @Test
    public void getSHA256ConversionTest() {
        String currentUserId = "USER_ID";
        String result = "E5BB97D1792FF76E360CD8E928B6B9B53BDA3E4FE88B026E961C2FACF963A361";
        Assert.assertEquals(result, Util.getSHA256Conversion(currentUserId));
        Assert.assertEquals("",Util.getSHA256Conversion(""));
    }

    @Test
    public void addCertificationTest(){
        RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET,currentBaseUrl,"");
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        Util.addCertification(requestObject, inputStream, currentBaseUrl);
    }

    @Test
    public void testSetAndGetCertificate() {
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
    public void getCertificateTest(){
        final ByteArrayInputStream inputStream = new ByteArrayInputStream("testCertificate".getBytes());
        Util.setCertificate(inputStream);

        Assert.assertNotEquals(inputStream, Util.getCertificate(context));


    }

    @Test
    public void getAndSetUserTypeTest(){
        Util.setUserType(Util.UserType.ROLE_PAID);
        Util.getUserType();
    }
}
