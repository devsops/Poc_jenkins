package com.bosch.pai.ipsadmin.comms.operation;


import android.util.Base64;
import android.util.Log;

import com.bosch.pai.ipsadmin.comms.CommsListener;
import com.bosch.pai.ipsadmin.comms.RetryConnectionHandler;
import com.bosch.pai.ipsadmin.comms.exception.CertificateLoadException;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.util.CommsUtil;
import com.bosch.pai.ipsadmin.comms.util.SSLConfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.validation.constraints.NotNull;

/**
 * The type Http runnable task.
 */
abstract class HTTPRunnableTask implements Runnable {

    private static final String TAG = HTTPRunnableTask.class.getName();
    private static final int TIME_OUT = 20000;
    private static final int READ_TIME_OUT = 10000;
    /**
     * The Request object.
     */
    protected final RequestObject requestObject;
    /**
     * The Comms listener.
     */
    protected final CommsListener commsListener;
    /**
     * The Retry connection handler.
     */
    protected final RetryConnectionHandler retryConnectionHandler;
    /**
     * The Http url connection.
     */
    protected HttpURLConnection httpURLConnection;

    /**
     * Instantiates a new Http runnable task.
     *
     * @param requestObject          the request object
     * @param retryConnectionHandler the retry connection handler
     * @param commsListener          the comms listener
     */
    HTTPRunnableTask(RequestObject requestObject, RetryConnectionHandler retryConnectionHandler, CommsListener commsListener) {
        this.requestObject = requestObject;
        this.retryConnectionHandler = retryConnectionHandler;
        this.commsListener = commsListener;
        initializeURLConnection();
    }

    private void initializeURLConnection() {
        try {
            final StringBuilder apiEndPointURL = new StringBuilder(requestObject.getBaseURL() + requestObject.getApiEndPoint());
            final String queryParams = getQueryParamURL(requestObject.isUrlParamEncoded(), requestObject.getQueryParams());
            apiEndPointURL.append(queryParams);
            final URL url = new URL(apiEndPointURL.toString());
            final URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof HttpsURLConnection) {
                httpURLConnection = (HttpsURLConnection) urlConnection;
                if(requestObject.isNonBezirkRequest()) {
                    SSLConfig.loadSSLContext(requestObject);
                }
                if(SSLConfig.getSSLSocketFactory() != null) {
                    ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(SSLConfig.getSSLSocketFactory());
                }
                final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(hostnameVerifier);
            } else {
                httpURLConnection = (HttpURLConnection) urlConnection;
            }
            httpURLConnection.setConnectTimeout(TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept-Charset", "application/json");
            httpURLConnection.setRequestProperty("platform", "android");
            if (requestObject.getHeaders() != null && !requestObject.getHeaders().isEmpty()) {
                for (Map.Entry<String, String> stringEntry : requestObject.getHeaders().entrySet()) {
                    httpURLConnection.setRequestProperty(stringEntry.getKey(), stringEntry.getValue());
                }
            }
        } catch (IOException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Error executing DELETE ", e);
        } catch (CertificateLoadException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Error loading SSL Certificate.", e);
        }
    }

    @NotNull
    private String getQueryParamURL(final boolean encodeURL, final Map<String, String> queryParams) throws UnsupportedEncodingException {
        final StringBuilder stringBuilder = new StringBuilder("");
        if (queryParams != null && !queryParams.isEmpty()) {
            stringBuilder.append("?");
            for (Map.Entry<String, String> stringEntry : requestObject.getQueryParams().entrySet()) {
                if (encodeURL) {
                    stringBuilder.append(stringEntry.getKey()).append("=").append(Base64.encodeToString(stringEntry.getValue().getBytes(), Base64.DEFAULT).trim()).append("&");
                } else {
                    stringBuilder.append(stringEntry.getKey()).append("=").append(URLEncoder.encode(stringEntry.getValue(), "UTF-8")).append("&");
                }
            }
            return stringBuilder.toString().substring(0, stringBuilder.toString().lastIndexOf('&'));
        }
        return stringBuilder.toString();
    }
}