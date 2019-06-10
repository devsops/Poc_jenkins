package com.bosch.pai.retail.analytics.exception;


import com.bosch.pai.retail.common.responses.StatusMessage;

public class AnalyticsServiceException extends RuntimeException {

    private final StatusMessage statusMessage;

    public AnalyticsServiceException(StatusMessage statusMessage) {
        super(statusMessage.getStatusDescription());
        this.statusMessage = statusMessage;
    }

    public AnalyticsServiceException(StatusMessage statusMessage, Throwable e) {
        super(statusMessage.getStatusDescription(), e);
        this.statusMessage = statusMessage;
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

}
