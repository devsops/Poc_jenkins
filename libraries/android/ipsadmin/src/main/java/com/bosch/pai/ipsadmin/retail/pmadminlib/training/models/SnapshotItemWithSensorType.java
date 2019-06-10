package com.bosch.pai.ipsadmin.retail.pmadminlib.training.models;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SnapshotItemWithSensorType {
    @SerializedName("snapshotItem")
    private SnapshotItem snapshotItem;
    @SerializedName("sensorType")
    private BearingConfiguration.SensorType sensorType;

    public SnapshotItemWithSensorType(BearingConfiguration.SensorType sensorType, SnapshotItem snapshotItem) {
        this.sensorType = sensorType;
        this.snapshotItem = snapshotItem;

    }

    public SnapshotItem getSnapshotItem() {
        return snapshotItem;
    }


    public BearingConfiguration.SensorType getSensorType() {
        return sensorType;
    }
}