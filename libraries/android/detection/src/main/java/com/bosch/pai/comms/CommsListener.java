package com.bosch.pai.comms;


import com.bosch.pai.comms.model.ResponseObject;

/**
 * The interface Comms listener.
 */
public interface CommsListener {

    /**
     * On response.
     *
     * @param responseObject the response object
     */
    void onResponse(ResponseObject responseObject);

    /**
     * On failure.
     *
     * @param statusCode the status code
     * @param errMessage the err message
     */
    void onFailure(int statusCode, String errMessage);
}