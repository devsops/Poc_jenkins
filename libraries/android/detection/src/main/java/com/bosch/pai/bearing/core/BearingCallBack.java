package com.bosch.pai.bearing.core;

import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.event.EventSender;
import com.bosch.pai.bearing.event.Sender;

/**
 * The interface Bearing call back.
 */
public interface BearingCallBack extends Sender {

    /**
     * Generic callback for BearingTrain.Callback will have a response based on the CRUD operations of BearingTrain.
     * Bearing Output will have a different response based on Header and Body
     *
     * @param bearingOutput Generic output from bearing with Status code and body.
     * @return void
     * @see BearingOutput <p> BearingOutput for training will only contain status code to detect success or failure in training.
     */
    void onLocationResponse(BearingOutput bearingOutput);

}
