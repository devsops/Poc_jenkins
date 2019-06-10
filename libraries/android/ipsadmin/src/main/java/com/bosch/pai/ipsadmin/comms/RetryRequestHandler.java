package com.bosch.pai.ipsadmin.comms;


import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.ipsadmin.comms.util.CommsUtil;

import java.util.Timer;
import java.util.TimerTask;


/**
 * The type Retry request handler.
 */
public class RetryRequestHandler implements CommsListener {
    private static final String TAG = RetryRequestHandler.class.getName();
    private int retryCount = 0;
    private final CommsManager commsManager;
    private final RequestObject requestObject;
    private CommsListener commsListener;
    private String certificateString;

    /**
     * Instantiates a new Http fall back and retry request handler.
     *
     * @param requestObject the request object
     */
    RetryRequestHandler(RequestObject requestObject) {
        this.requestObject = requestObject;
        if (this.requestObject.getCertFileStream() != null) {
            this.certificateString = CommsUtil.convertCrtStreamToString(this.requestObject.getCertFileStream());
            this.requestObject.setCertFileStream(CommsUtil.getStreamCrtString(this.certificateString));
        }
        this.commsManager = CommsManager.getInstance();
    }

    /**
     * Sets comms listener.
     *
     * @param commsListener the comms listener
     */
    void setCommsListener(CommsListener commsListener) {
        this.commsListener = commsListener;
    }

    @Override
    public void onResponse(ResponseObject responseObject) {
        this.commsListener.onResponse(responseObject);
    }

    @Override
    public void onFailure(int statusCode, String errMessage) {
        if (this.requestObject.isRetry() && this.retryCount < this.requestObject.getRetryCount()) {
            this.executeTimerTaskForRetry();
            this.retryCount++;
        } else {
            this.commsListener.onFailure(statusCode, errMessage);
        }
    }

    private void executeTimerTaskForRetry() {
        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "executeTimerTaskForRetry: " + requestObject + " Retry Count: " + retryCount);
        if (this.certificateString != null) {
            this.requestObject.setCertFileStream(CommsUtil.getStreamCrtString(this.certificateString));
        }
        final Timer timer = new Timer("RetryRequest :: " + requestObject.getBaseURL());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                commsManager.processRequest(requestObject, RetryRequestHandler.this);
                timer.cancel();
                timer.purge();
            }
        }, requestObject.getRetryAfterMillis());
    }
}