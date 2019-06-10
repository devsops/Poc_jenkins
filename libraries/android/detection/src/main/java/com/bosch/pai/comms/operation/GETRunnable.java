package com.bosch.pai.comms.operation;


import android.util.Log;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.RetryConnectionHandler;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.comms.util.CommsUtil;
import com.bosch.pai.comms.util.SSLContextHolder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * The type Get runnable.
 */
public final class GETRunnable extends HTTPRunnableTask {

    private static final String TAG = GETRunnable.class.getName();
    private final UUID requestID;
    private final ResponseObject responseObject = new ResponseObject();
    private BufferedReader bufferedReader = null;
    private InputStreamReader inputStreamReader = null;
    private StringBuilder responseBody = new StringBuilder();

    /**
     * Instantiates a new Get runnable.
     *
     * @param requestID              the request id
     * @param requestObject          the request object
     * @param retryConnectionHandler the retry connection handler
     * @param commsListener          the comms listener
     */
    public GETRunnable(UUID requestID, RequestObject requestObject, RetryConnectionHandler retryConnectionHandler, CommsListener commsListener) {
        super(requestObject, retryConnectionHandler, commsListener);
        this.requestID = requestID;
    }

    @Override
    public void run() {

        int responseStatus = 0;
        String responseMsg = "";
        Map<String, List<String>> headerValues;

        try {
            this.retryConnectionHandler.waitForConnectionRestore(this.requestObject.getRetryAfterMillis(), this.requestObject.getRetryCount());
            this.httpURLConnection.setDoInput(true);
            this.httpURLConnection.setRequestMethod("GET");
            this.httpURLConnection.connect();
            responseStatus = this.httpURLConnection.getResponseCode();
            responseMsg = this.httpURLConnection.getResponseMessage();
            headerValues = this.httpURLConnection.getHeaderFields();
            responseObject.setRequestID(requestID);
            responseObject.setStatusCode(responseStatus);
            responseObject.setStatusMessage(responseMsg);
            responseObject.setHeaders(headerValues);
            getResponse();
            this.commsListener.onResponse(responseObject);
        } catch (SocketTimeoutException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Failed to connect: " + this.requestObject.getApiEndPoint());
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Socket timeout exception", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(HttpURLConnection.HTTP_CLIENT_TIMEOUT, "Failed to connect" + this.requestObject.getApiEndPoint());
        } catch (IOException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "IO exception", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(responseStatus, responseBody + e.getMessage());
        } finally {
            if (this.httpURLConnection != null)
                this.httpURLConnection.disconnect();
            try {
                SSLContextHolder.clear();
                if (bufferedReader != null)
                    bufferedReader.close();
                if (inputStreamReader != null)
                    inputStreamReader.close();
            } catch (IOException e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Error closing reader", e);
            }
        }
    }

    private void getResponse() {
        if (requestObject != null && !requestObject.getHeaders().isEmpty() &&
                requestObject.getHeaders().get("accept") != null && requestObject.getHeaders().get("accept").contains("zip")) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, "Inside Comms", "Accept Contains ZIP");
            try (InputStream in = this.httpURLConnection.getInputStream()) {
                byte[] buffer = new byte[0xFFFF];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                    baos.write(buffer, 0, read);
                }
                responseObject.setResponseBody(baos.toByteArray());
            } catch (Exception e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Not a success status message", e);
            }
        } else {
            try {
                inputStreamReader = new InputStreamReader(this.httpURLConnection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBody.append(line);
                    responseBody.append("\n");
                }
                if (responseBody.length() != 0 || !responseBody.toString().isEmpty()) {
                    responseObject.setResponseBody(responseBody.toString().substring(0, responseBody.length() - 1));
                }
            } catch (IOException e) {
                try {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Not a success status message", e);
                    inputStreamReader = new InputStreamReader(this.httpURLConnection.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBody.append(line);
                        responseBody.append("\n");
                    }
                    if (responseBody.length() != 0 || !responseBody.toString().isEmpty()) {
                        responseObject.setStatusMessage(responseBody.toString().substring(0, responseBody.length() - 1));
                    }
                } catch (IOException e1) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Error reading stream", e1);
                }
            }
        }
    }
}