package com.bosch.pai.ipsadminapp.models;



import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.SnapshotItemWithSensorType;

import java.io.Serializable;

public class SignalAdapterModel {

    private SnapshotItemWithSensorType snapshotItemWithSensorType;
    private boolean isSelected;

    public SignalAdapterModel() {
        //default constructor
    }

    public SignalAdapterModel(SnapshotItemWithSensorType signal, boolean isSelected) {
        this.snapshotItemWithSensorType = signal;
        this.isSelected = isSelected;
    }

    public SnapshotItemWithSensorType getSnapshotItemWithSensorType() {
        return snapshotItemWithSensorType;
    }

    public void setSnapshotItemWithSensorType(SnapshotItemWithSensorType snapshotItemWithSensorType) {
        this.snapshotItemWithSensorType = snapshotItemWithSensorType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "SignalAdapterModel{" +
                "snapshotItemWithSensorType='" + snapshotItemWithSensorType + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
