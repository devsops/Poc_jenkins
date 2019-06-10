package com.bosch.pai.ipsadmin.comms.model;


import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Request model object
 */
public class RequestObject {
    private final RequestType requestType;
    private final String baseURL;
    private final String apiEndPoint;
    private String messageBody;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private File multiPartFile;
    private InputStream certFileStream;
    private boolean nonBezirkRequest = false;
    private RequestRetry requestRetry;
    private boolean urlParamEncoded = false;

    private class RequestRetry {
        private boolean isRetry = false;
        private int retryCount = 0;
        private long retryAfterMillis = 0L;

        /**
         * Is retry boolean.
         *
         * @return the boolean
         */
        boolean isRetry() {
            return isRetry;
        }

        /**
         * Sets retry.
         *
         * @param retry the retry
         */
        void setRetry(boolean retry) {
            isRetry = retry;
        }

        /**
         * Gets retry count.
         *
         * @return the retry count
         */
        int getRetryCount() {
            return retryCount;
        }

        /**
         * Sets retry count.
         *
         * @param retryCount the retry count
         */
        void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        /**
         * Gets retry after millis.
         *
         * @return the retry after millis
         */
        long getRetryAfterMillis() {
            return retryAfterMillis;
        }

        /**
         * Sets retry after millis.
         *
         * @param retryAfterMillis the retry after millis
         */
        void setRetryAfterMillis(long retryAfterMillis) {
            this.retryAfterMillis = retryAfterMillis;
        }
    }
    /**
     * Gets cert file stream.
     *
     * @return the cert file stream
     */
    public InputStream getCertFileStream() {
        return certFileStream;
    }

    /**
     * Sets cert file stream.
     *
     * @param certFileStream the cert file stream
     */
    public void setCertFileStream(InputStream certFileStream) {
        this.certFileStream = certFileStream;
    }

    /**
     * Sets non bezirk request.
     *
     * @param nonBezirkRequest the non bezirk request
     */
    public void setNonBezirkRequest(boolean nonBezirkRequest) {
        this.nonBezirkRequest = nonBezirkRequest;
    }

    /**
     * Is non bezirk request boolean.
     *
     * @return the boolean
     */
    public boolean isNonBezirkRequest() {
        return nonBezirkRequest;
    }

    /**
     * The enum Request type.
     */
    public enum RequestType {
        /**
         * Post request type.
         */
        POST, /**
         * Multipart post request type.
         */
        MULTIPART_POST, /**
         * Get request type.
         */
        GET, /**
         * Put request type.
         */
        PUT, /**
         * Delete request type.
         */
        DELETE
    }

    /**
     * Request object for {@link RequestType}
     *
     * @param requestType RequestType.POST/RequestType.PUT/RequestType.DELETE
     * @param baseURL     the base url
     * @param apiEndPoint API Endpoint
     */
    public RequestObject(@NotNull RequestType requestType, @NotNull String baseURL, @NotNull String apiEndPoint) {
        this.requestType = requestType;
        this.baseURL = baseURL;
        this.apiEndPoint = apiEndPoint;
        this.requestRetry = new RequestRetry();
    }

    /**
     * Sets headers.
     *
     * @param headers the headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Sets query params.
     *
     * @param queryParams the query params
     */
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Sets message body.
     *
     * @param messageBody the message body
     */
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * Sets multipart file.
     *
     * @param multipartFile the multipart file
     */
    public void setMultipartFile(File multipartFile) {
        this.multiPartFile = multipartFile;
    }

    /**
     * Gets request type.
     *
     * @return the request type
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Gets base url.
     *
     * @return the base url
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Gets api end point.
     *
     * @return the api end point
     */
    public String getApiEndPoint() {
        return apiEndPoint;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        if (headers == null)
            return new HashMap<>();
        return headers;
    }

    /**
     * Gets query params.
     *
     * @return the query params
     */
    public Map<String, String> getQueryParams() {
        if (queryParams == null)
            return new HashMap<>();
        return queryParams;
    }

    /**
     * Gets multi part file.
     *
     * @return the multi part file
     */
    public File getMultiPartFile() {
        return multiPartFile;
    }

    /**
     * Gets message body.
     *
     * @return the message body
     */
    public String getMessageBody() {
        return messageBody;
    }

    /**
     * Is retry boolean.
     *
     * @return the boolean
     */
    public boolean isRetry() {
        return this.requestRetry.isRetry();
    }

    /**
     * Sets retry.
     *
     * @param retry the retry
     */
    public void setRetry(boolean retry) {
        this.requestRetry.setRetry(retry);
    }

    /**
     * Gets retry count.
     *
     * @return the retry count
     */
    public int getRetryCount() {
        return this.requestRetry.getRetryCount();
    }

    /**
     * Sets retry count.
     *
     * @param retryCount the retry count
     */
    public void setRetryCount(int retryCount) {
        this.requestRetry.setRetryCount(retryCount);
    }

    /**
     * Gets retry after millis.
     *
     * @return the retry after millis
     */
    public long getRetryAfterMillis() {
        return this.requestRetry.getRetryAfterMillis();
    }

    /**
     * Sets retry after millis.
     *
     * @param retryAfterMillis the retry after millis
     */
    public void setRetryAfterMillis(long retryAfterMillis) {
        this.requestRetry.setRetryAfterMillis(retryAfterMillis);
    }
    /**
     * Sets url param encoded.
     *
     * @param urlParamEncoded the url param encoded
     */
    public void setUrlParamEncoded(boolean urlParamEncoded) {
        this.urlParamEncoded = urlParamEncoded;
    }

    /**
     * Is url param encoded boolean.
     *
     * @return the boolean
     */
    public boolean isUrlParamEncoded() {
        return urlParamEncoded;
    }
}
