package com.bosch.pai.ipsadminapp.models;

import java.io.Serializable;

/**
 * Created by sjn8kor on 5/30/2018.
 */

public class BleAdapterModel implements Serializable {

    private String sourceId;

    private double rssi;

    private boolean isSelected;

    public BleAdapterModel() {
    }

    public BleAdapterModel(String sourceId,double rssi, boolean isSelected) {
        this.sourceId = sourceId;
        this.rssi = rssi;
        this.isSelected = isSelected;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "BleAdapterModel{" +
                "sourceId='" + sourceId + '\'' +
                ", rssi=" + rssi +
                ", isSelected=" + isSelected +
                '}';
    }
}
