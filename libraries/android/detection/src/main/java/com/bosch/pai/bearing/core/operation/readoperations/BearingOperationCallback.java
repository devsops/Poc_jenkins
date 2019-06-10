package com.bosch.pai.bearing.core.operation.readoperations;

import java.util.List;
import java.util.UUID;

/**
 * The interface Bearing operation callback.
 */
public interface BearingOperationCallback {


    /**
     * The constant Error.
     */
    public String Error = "Error in data retrieval";


    /**
     * On data received.
     *
     * @param transactionId the transaction id
     * @param dataList      the data list
     */
    void onDataReceived(UUID transactionId, List<String> dataList);

    /**
     * On data received error.
     *
     * @param transactionId the transaction id
     * @param errorMessage  the error message
     */
    void onDataReceivedError(UUID transactionId, String errorMessage);

}
