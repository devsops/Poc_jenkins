package com.bosch.pai.ipsadmin.bearing.core.operation.training.site;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;

import java.util.List;
import java.util.UUID;

/**
 * The interface Train site listener.
 */
public interface TrainSiteListener {

    /**
     * On train error.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     * @param siteName      the site name
     * @param errMessage    the err message
     */
    void onTrainError(UUID transactionId, BearingConfiguration.Approach approach, String siteName, String errMessage);

    /**
     * On train success.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     * @param siteName      the site name
     */
    void onTrainSuccess(UUID transactionId, BearingConfiguration.Approach approach, String siteName);

    /**
     * Callback introduced to return the snapshotObservation on scan success
     *
     * @param transactionId           the id mapping for the trancation of scanning
     * @param approach                the approach used for the data capture
     * @param siteName                the name of the site for with the scan was done
     * @param snapshotObservationList the list of scan responses returned.
     */


    void onSignalMerge(UUID transactionId, BearingConfiguration.Approach approach, String siteName, List<SnapshotObservation> snapshotObservationList);

}
