package com.bosch.pai.comms.operation;


import android.util.Log;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.RetryConnectionHandler;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.comms.util.CommsUtil;
import com.bosch.pai.comms.util.SSLContextHolder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * The type Post runnable.
 */
public final class POSTRunnable extends HTTPRunnableTask {

    private static final String TAG = POSTRunnable.class.getName();
    private static final String ERROR = "Error closing reader";
    private final UUID requestID;

    /**
     * Instantiates a new Post runnable.
     *
     * @param requestID              the request id
     * @param requestObject          the request object
     * @param retryConnectionHandler the retry connection handler
     * @param commsListener          the comms listener
     */
    public POSTRunnable(UUID requestID, RequestObject requestObject, RetryConnectionHandler retryConnectionHandler, CommsListener commsListener) {
        super(requestObject, retryConnectionHandler, commsListener);
        this.requestID = requestID;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStream outputStream = null;
        int responseStatus = 0;
        String responseMsg = "";
        StringBuilder responseBody = new StringBuilder("");
        Map<String, List<String>> headerValues;
        try {
            this.retryConnectionHandler.waitForConnectionRestore(this.requestObject.getRetryAfterMillis(), this.requestObject.getRetryCount());
            this.httpURLConnection.setDoOutput(true);
            this.httpURLConnection.setRequestMethod("POST");
            if (this.requestObject.getMessageBody() != null) {
                final String message = this.requestObject.getMessageBody();
                this.httpURLConnection.setRequestProperty("Content-Length", String.valueOf(message.getBytes().length));
                this.httpURLConnection.setFixedLengthStreamingMode(message.getBytes().length);
                this.httpURLConnection.connect();
                outputStream = new BufferedOutputStream(this.httpURLConnection.getOutputStream());
                outputStream.write(message.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            responseStatus = this.httpURLConnection.getResponseCode();
            headerValues = this.httpURLConnection.getHeaderFields();
            responseMsg = this.httpURLConnection.getResponseMessage();

            try {
                inputStreamReader = new InputStreamReader(this.httpURLConnection.getInputStream());
            } catch (IOException e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Not a success status message", e);
                inputStreamReader = new InputStreamReader(this.httpURLConnection.getErrorStream());
            }
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseBody.append(line);
            }
            final ResponseObject responseObject = new ResponseObject();
            responseObject.setRequestID(requestID);
            responseObject.setStatusCode(responseStatus);
            responseObject.setStatusMessage(responseMsg);
            responseObject.setResponseBody(responseBody.toString());
            responseObject.setHeaders(headerValues);
            this.commsListener.onResponse(responseObject);
        } catch (SocketTimeoutException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Failed to connect: " + this.requestObject.getApiEndPoint());
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Socket timeout exception", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(HttpURLConnection.HTTP_CLIENT_TIMEOUT, "Failed to connect" + this.requestObject.getApiEndPoint());
        } catch (IOException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Socket timeout exception", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(responseStatus, responseBody + e.getMessage());
        } finally {
            if (this.httpURLConnection != null)
                this.httpURLConnection.disconnect();
            SSLContextHolder.clear();
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, ERROR, e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, ERROR, e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, ERROR, e);
                }
            }
        }
    }
}