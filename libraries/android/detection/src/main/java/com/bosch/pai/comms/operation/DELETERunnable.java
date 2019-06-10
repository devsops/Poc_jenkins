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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * The type Delete runnable.
 */
public final class DELETERunnable extends HTTPRunnableTask {

    private static final String TAG = DELETERunnable.class.getName();
    private final UUID requestID;

    /**
     * Instantiates a new Delete runnable.
     *
     * @param requestID              the request id
     * @param requestObject          the request object
     * @param retryConnectionHandler the retry connection handler
     * @param commsListener          the comms listener
     */
    public DELETERunnable(UUID requestID, RequestObject requestObject, RetryConnectionHandler retryConnectionHandler, CommsListener commsListener) {
        super(requestObject, retryConnectionHandler, commsListener);
        this.requestID = requestID;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStream outputStream = null;
        int responseStatus = 0;
        String responseMessage = "";
        StringBuilder responseBody = new StringBuilder("");
        Map<String, List<String>> headerValues;
        InputStream stream = null;
        try {
            this.retryConnectionHandler.waitForConnectionRestore(this.requestObject.getRetryAfterMillis(), this.requestObject.getRetryCount());
            this.httpURLConnection.setRequestMethod("DELETE");
            this.httpURLConnection.setDoOutput(true);
            if (this.requestObject.getMessageBody() != null) {
                final String message = requestObject.getMessageBody();
                this.httpURLConnection.setFixedLengthStreamingMode(message.getBytes().length);
                this.httpURLConnection.connect();
                outputStream = new BufferedOutputStream(this.httpURLConnection.getOutputStream());
                outputStream.write(message.getBytes());
                outputStream.flush();
            }
            responseStatus = this.httpURLConnection.getResponseCode();
            headerValues = this.httpURLConnection.getHeaderFields();
            responseMessage = this.httpURLConnection.getResponseMessage();

            try {
                stream = this.httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(stream);
            } catch (IOException e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Not a success status message", e);
                stream = this.httpURLConnection.getErrorStream();
                inputStreamReader = new InputStreamReader(stream);
            }
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseBody.append(line);
            }
            final ResponseObject responseObject = new ResponseObject();
            responseObject.setRequestID(requestID);
            responseObject.setStatusCode(responseStatus);
            responseObject.setStatusMessage(responseMessage);
            responseObject.setResponseBody(responseBody.toString());
            responseObject.setHeaders(headerValues);
            this.commsListener.onResponse(responseObject);
        } catch (SocketTimeoutException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Failed to connect" + requestObject.getApiEndPoint());
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Socket timeout exception", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(HttpURLConnection.HTTP_CLIENT_TIMEOUT, "Failed to connect" + requestObject.getApiEndPoint());
        } catch (IOException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Error executing DELETE ", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(responseStatus, responseBody + e.getMessage());
        } finally {
            if (this.httpURLConnection != null)
                this.httpURLConnection.disconnect();
            try {
                SSLContextHolder.clear();
                if (outputStream != null)
                    outputStream.close();
                if (bufferedReader != null)
                    bufferedReader.close();
                if (inputStreamReader != null)
                    inputStreamReader.close();
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Error closing the stream", e);
            }
        }
    }
}
