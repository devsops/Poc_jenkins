package com.bosch.pai.comms;


import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.comms.operation.POSTRunnable;
import com.bosch.pai.comms.util.CommsUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * The type Retry connection handler.
 */
public class RetryConnectionHandler {
    private static final String TAG = RetryConnectionHandler.class.getName();
    private boolean connectionLost = false;
    private static final int PING_DELAY_INTERVAL = 4; // In seconds
    private String authenticateRequestBody;
    private String certificateString;
    private boolean isMasterURL;
    private final String baseURL;
    private ScheduledExecutorService scheduledExecutorService = null;

    /**
     * Instantiates a new Retry connection handler.
     *
     * @param baseURL the base url
     */
    RetryConnectionHandler(String baseURL) {
        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Created RetryConnectionHandler for baseURL :: " + baseURL);
        this.baseURL = baseURL;
    }

    /**
     * Sets authenticate request body.
     *
     * @param authenticateRequestBody the authenticate request body
     */
    protected void setAuthenticateRequestBody(String authenticateRequestBody) {
        this.authenticateRequestBody = authenticateRequestBody;
    }

    /**
     * Sets certificate string.
     *
     * @param certificateString the certificate string
     */
    protected void setCertificateString(String certificateString) {
        this.certificateString = certificateString;
    }

    /**
     * Sets is master url.
     *
     * @param isMasterURL the is master url
     */
    protected void setIsMasterURL(boolean isMasterURL) {
        this.isMasterURL = isMasterURL;
    }

    /**
     * Wait for connection restore.
     *
     * @param retryIntervalInMillis the retry interval in millis
     * @param maxRetryAttempt       the max retry attempt
     */
    public synchronized void waitForConnectionRestore(long retryIntervalInMillis, int maxRetryAttempt) {
        int maxRetryAttemptCount = maxRetryAttempt;
        if (retryIntervalInMillis < 0) {
            maxRetryAttemptCount = Integer.MAX_VALUE;
        }
        while (maxRetryAttemptCount > 0 && this.connectionLost) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "WaitForConnectionRestore");
            maxRetryAttemptCount--;
            if (retryIntervalInMillis < 0) {
                try {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Waiting...");
                    wait();
                } catch (InterruptedException e) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Thread interrupted" + e);
                    Thread.currentThread().interrupt();
                }
            } else {
                try {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Waiting...");
                    wait(retryIntervalInMillis);
                } catch (InterruptedException e) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Thread interrupted" + e);
                    Thread.currentThread().interrupt();
                }
            }
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Wait over! Resuming task.");
        }
    }

    /**
     * Handle connection lost.
     */
    public synchronized void handleConnectionLost() {
        if (!connectionLost) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Connection lost!");
            this.connectionLost = true;
            notifyAll();
            schedulePingRunnable();
        }
    }

    private synchronized void schedulePingRunnable() {
        if (this.scheduledExecutorService == null) {
            this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        this.scheduledExecutorService.schedule(createAuthenticationRunnable(), PING_DELAY_INTERVAL, TimeUnit.SECONDS);
    }

    private Runnable createAuthenticationRunnable() {
        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Create Authentication runnable!");
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, this.baseURL, CommsManager.AUTHENTICATE_USER);
        requestObject.setMessageBody(authenticateRequestBody);
        requestObject.setNonBezirkRequest(true);
        requestObject.setCertFileStream(CommsUtil.getStreamCrtString(certificateString));
        return new POSTRunnable(UUID.randomUUID(), requestObject, this, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                RetryConnectionHandler.this.notifyConnectionRestore();
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(responseObject.getResponseBody().toString());
                    final String contextId = String.valueOf(jsonObject.get(CommsManager.CONTEXT_ID_KEY));
                    final Long contextIdTime = Long.valueOf(jsonObject.get(CommsManager.TIME_EXPIRE_KEY).toString());
                    CommsManager.setContextIDExpiryTime(contextIdTime);
                    CommsManager.setContextID(isMasterURL, RetryConnectionHandler.this.baseURL, contextId);
                }
            }

            @Override
            public void onFailure(int statusCode, String errMessage) {
                schedulePingRunnable();
            }
        });
    }

    private synchronized void notifyConnectionRestore() {
        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Connection restored!");
        // Resetting connection lost
        RetryConnectionHandler.this.connectionLost = false;
        // Stopping ping task
        RetryConnectionHandler.this.scheduledExecutorService.shutdownNow();
        RetryConnectionHandler.this.scheduledExecutorService = null;
        // Notifying all threads in wait
        notifyAll();
    }
}