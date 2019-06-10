package com.bosch.pai.bearing.core.operation;

import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

/**
 * The type Bearing callback operation.
 */
public class BearingCallbackOperation {

    private final BearingConfiguration.OperationType operationType;
    private final BearingCallBack bearingCallback;
    private final BearingConfiguration.Approach approach;

    /**
     * Instantiates a new Bearing callback operation.
     *
     * @param operationType   the operation type
     * @param approach        the approach
     * @param bearingCallback the bearing callback
     */
    public BearingCallbackOperation(BearingConfiguration.OperationType operationType, BearingConfiguration.Approach approach, BearingCallBack bearingCallback) {
        this.operationType = operationType;
        this.bearingCallback = bearingCallback;
        this.approach= approach;
    }

    /**
     * Gets approach.
     *
     * @return the approach
     */
    public BearingConfiguration.Approach getApproach() {
        return approach;
    }

    /**
     * Gets operation type.
     *
     * @return the operation type
     */
    public BearingConfiguration.OperationType getOperationType() {
        return operationType;
    }


    /**
     * Gets bearing callback.
     *
     * @return the bearing callback
     */
    public BearingCallBack getBearingCallback() {
        return bearingCallback;
    }


}
