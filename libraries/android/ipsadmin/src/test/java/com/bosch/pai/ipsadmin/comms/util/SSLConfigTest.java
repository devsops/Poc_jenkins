package com.bosch.pai.ipsadmin.comms.util;

import com.bosch.pai.ipsadmin.comms.model.RequestObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SSLConfig.class, CertificateFactory.class, TrustManager.class, X509TrustManager.class,
        SSLContext.class, SSLContextHolder.class, KeyStore.class, TrustManagerFactory.class})
public class SSLConfigTest {

    @Mock
    private CertificateFactory certificateFactory;

    @Mock
    private InputStream inputStream;

    @Mock
    private Certificate certificate;

    @Mock
    private KeyStore keyStore;

    @Mock
    private TrustManagerFactory trustManagerFactory;

    @Mock
    private X509TrustManager trustManager;

    @Mock
    private X509TrustManager x509TrustManager;

    @Mock
    private SSLContext sslContext;


    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(CertificateFactory.class);
        PowerMockito.mockStatic(TrustManager.class);
        PowerMockito.when(CertificateFactory.getInstance(Mockito.anyString()))
                .thenReturn(certificateFactory);
        PowerMockito.when(certificateFactory.generateCertificate(Mockito.any(InputStream.class)))
                .thenReturn(certificate);
        PowerMockito.mockStatic(KeyStore.class);
        PowerMockito.when(KeyStore.getDefaultType()).thenReturn("DEFAULT-TYPE");
        PowerMockito.when(KeyStore.getInstance(Mockito.anyString())).thenReturn(keyStore);
        PowerMockito.mockStatic(TrustManagerFactory.class);
        PowerMockito.when(TrustManagerFactory.getDefaultAlgorithm()).thenReturn("DEFAULT_ALGO");
        PowerMockito.when(TrustManagerFactory.getInstance(Mockito.anyString()))
                .thenReturn(trustManagerFactory);
        PowerMockito.doNothing().
                when(trustManagerFactory, "init", Mockito.any(KeyStore.class));
        final TrustManager[] trustManagers = new X509TrustManager[]{x509TrustManager};
        PowerMockito.
                when(trustManagerFactory.getTrustManagers()).thenReturn(trustManagers);
        PowerMockito.mockStatic(SSLContext.class);
        PowerMockito.when(SSLContext.getInstance(Mockito.anyString())).thenReturn(sslContext);
        PowerMockito.doNothing().when(sslContext, "init", Mockito.isNull(), Mockito.any(TrustManager[].class), Mockito.isNull());

    }

    @Test
    public void test1() throws Exception {
        final String BASE_URL = "http://www.test.com";
        final String API_END_POINT = "/dummyEndPoint";
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.GET, BASE_URL, API_END_POINT);
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(inputStream);
        SSLConfig.loadSSLContext(requestObject);
        PowerMockito.verifyPrivate(SSLContextHolder.class, Mockito.times(1))
                .invoke("set", Mockito.any(SSLContext.class));
        Assert.assertNull(SSLConfig.getSSLSocketFactory());
        Assert.assertNotNull(SSLConfig.getX509TrustManager());
    }

}
