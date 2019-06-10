package com.bosch.pai.ipsadmin.bearing.core.operation.training.location;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import java.util.UUID;

/**
 * The interface Train location listener.
 */
public interface TrainLocationListener {


    /**
     * On data record progress.
     *
     * @param transactionID the transaction id
     * @param approach      the approach
     * @param progress      the progress
     */
    void onDataRecordProgress(UUID transactionID, BearingConfiguration.Approach approach, int progress);


    /**
     * On locations trained.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     * @param siteName      the site name
     */
    void onLocationsTrained(UUID transactionId, BearingConfiguration.Approach approach, String siteName);

    /**
     * On location train error.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     * @param errMessage    the err message
     */
    void onLocationTrainError(UUID transactionId, BearingConfiguration.Approach approach, String errMessage);

    /**
     * On data recording completed.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     * @param siteName      the site name
     * @param locationName  the location name
     */
    void onDataRecordingCompleted(UUID transactionId, BearingConfiguration.Approach  approach, String siteName, String locationName);

    /**
     * On data record error.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     * @param errMessage    the err message
     */
    void onDataRecordError(UUID transactionId, BearingConfiguration.Approach  approach, String errMessage);


}
