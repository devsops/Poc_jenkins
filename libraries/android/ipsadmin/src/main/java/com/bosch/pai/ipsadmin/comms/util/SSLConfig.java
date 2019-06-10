package com.bosch.pai.ipsadmin.comms.util;


import com.bosch.pai.ipsadmin.comms.exception.CertificateLoadException;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * The type Ssl config.
 */
public final class SSLConfig {

    private static X509TrustManager x509TrustManager;

    private SSLConfig() {
    }

    /**
     * Load ssl context.
     *
     * @throws CertificateLoadException the certificate load exception
     */
    public static void loadSSLContext(RequestObject reqObj) throws CertificateLoadException {
        if (reqObj.isNonBezirkRequest()) {
            setSSLContext(reqObj.getCertFileStream());
        }
    }

    public static void loadSSLContext(InputStream inputStream) throws CertificateLoadException {
        setSSLContext(inputStream);
    }

    private static void setSSLContext(InputStream certStream) throws CertificateLoadException {
        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate;
            if (certStream == null) {
                SSLContextHolder.set(null);
                return;
            }
            certificate = certificateFactory.generateCertificate(certStream);

            final String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("Certificate", certificate);

            final String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keyStore);

            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            x509TrustManager = (X509TrustManager) trustManagers[0];

            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
            SSLContextHolder.set(sslContext);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, "SSLConfig", "Error loading self-signed certificate", e);
            throw new CertificateLoadException(e.getMessage());
        }
    }

    /**
     * Gets ssl socket factory.
     *
     * @return the ssl socket factory
     */
    public static SSLSocketFactory getSSLSocketFactory() {
        if (SSLContextHolder.get() != null) {
            return SSLContextHolder.get().getSocketFactory();
        }
        return null;
    }

    /**
     * Gets x 509 trust manager.
     *
     * @return the x 509 trust manager
     */
    public static X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }
}