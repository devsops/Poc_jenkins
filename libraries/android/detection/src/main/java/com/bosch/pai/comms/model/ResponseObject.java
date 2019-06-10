package com.bosch.pai.comms.model;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response model object
 */
public class ResponseObject {

    private UUID requestID;
    private int statusCode;
    private String statusMessage;
    private Map<String, List<String>> headers;
    private Object responseBody;

    /**
     * Sets request id.
     *
     * @param requestID the request id
     */
    public void setRequestID(UUID requestID) {
        this.requestID = requestID;
    }

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public UUID getRequestID() {
        return requestID;
    }

    /**
     * Sets status code.
     *
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Sets status message.
     *
     * @param statusMessage the status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Sets headers.
     *
     * @param headers the headers
     */
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    /**
     * Sets response body.
     *
     * @param responseBody the response body
     */
    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Gets status message.
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     */
    public Map<String, List<String>> getHeaders() {
        if (headers == null)
            return Collections.emptyMap();
        return headers;
    }

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets response body.
     *
     * @return the response body
     */
    public Object getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return "[ResponseObject :: requestID: " + requestID +
                " statusCode: " + statusCode +
                " statusMessage: " + statusMessage +
                " headers: " + headers +
                " responseBody: " + responseBody + "]";
    }
}