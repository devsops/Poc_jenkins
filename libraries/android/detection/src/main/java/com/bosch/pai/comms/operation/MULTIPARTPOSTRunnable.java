package com.bosch.pai.comms.operation;


import android.util.Log;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.RetryConnectionHandler;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.comms.util.CommsUtil;
import com.bosch.pai.comms.util.SSLContextHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * The type Multi-part post runnable.
 */
public final class MULTIPARTPOSTRunnable extends HTTPRunnableTask {

    private static final String TAG = MULTIPARTPOSTRunnable.class.getName();
    private static final String ERROR = "Error closing reader";
    private static final String LINE_FEED = "\r\n";
    private final UUID requestID;
    private String boundary;

    /**
     * Instantiates a new Multipartpost runnable.
     *
     * @param requestID              the request id
     * @param requestObject          the request object
     * @param retryConnectionHandler the retry connection handler
     * @param commsListener          the comms listener
     */
    public MULTIPARTPOSTRunnable(UUID requestID, RequestObject requestObject, RetryConnectionHandler retryConnectionHandler, CommsListener commsListener) {
        super(requestObject, retryConnectionHandler, commsListener);
        this.requestID = requestID;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStream outputStream = null;
        PrintWriter writer = null;
        int responseStatus = 0;
        String responseMessage = "";
        StringBuilder responseBody = new StringBuilder("");
        Map<String, List<String>> headerValues;
        try {
            this.retryConnectionHandler.waitForConnectionRestore(this.requestObject.getRetryAfterMillis(), this.requestObject.getRetryCount());
            this.httpURLConnection.setDoOutput(true);
            this.httpURLConnection.setDoInput(true);
            this.httpURLConnection.setRequestMethod("POST");
            this.boundary = "===" + System.currentTimeMillis() + "===";
            this.httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            if (this.requestObject.getMultiPartFile() != null) {
                outputStream = this.httpURLConnection.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
                addFilePart(writer, outputStream, "file", this.requestObject.getMultiPartFile());
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();
            }
            responseStatus = this.httpURLConnection.getResponseCode();
            headerValues = this.httpURLConnection.getHeaderFields();
            responseMessage = this.httpURLConnection.getResponseMessage();
            try {
                inputStreamReader = new InputStreamReader(this.httpURLConnection.getInputStream());
            } catch (IOException e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Not a success status message", e);
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
            responseObject.setStatusMessage(responseMessage);
            responseObject.setResponseBody(responseBody.toString());
            responseObject.setHeaders(headerValues);
            this.commsListener.onResponse(responseObject);
        } catch (SocketTimeoutException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Failed to connect: " + this.requestObject.getApiEndPoint());
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Socket timeout exception", e);
            this.retryConnectionHandler.handleConnectionLost();
            this.commsListener.onFailure(HttpURLConnection.HTTP_CLIENT_TIMEOUT, "Failed to connect" + requestObject.getApiEndPoint());
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
                    Log.e(TAG, ERROR, e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, ERROR, e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, ERROR, e);
                }
            }

        }

    }

    /**
     * Code was referred from the link : http://www.codejava.net/
     */
    private void addFilePart(PrintWriter writer, OutputStream outputStream, String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(this.boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"")
                .append(fieldName).append("\"; filename=\"")
                .append(fileName).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ")
                .append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        try (FileInputStream inputStream = new FileInputStream(uploadFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }

        writer.append(LINE_FEED);
        writer.flush();
    }
}