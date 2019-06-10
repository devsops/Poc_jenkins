package com.bosch.pai.ipsadmin.retail.pmadminlib.training.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sjn8kor on 6/14/2018.
 */

public class ScannedBleDetails implements Serializable {

    @SerializedName("bleId")
    private String bleId;

    @SerializedName("bleRssi")
    private Double bleRssi;

    public ScannedBleDetails() {
    }

    public ScannedBleDetails(String bleId, Double bleRssi) {
        this.bleId = bleId;
        this.bleRssi = bleRssi;
    }

    public String getBleId() {
        return bleId;
    }

    public void setBleId(String bleId) {
        this.bleId = bleId;
    }

    public Double getBleRssi() {
        return bleRssi;
    }

    public void setBleRssi(Double bleRssi) {
        this.bleRssi = bleRssi;
    }

    @Override
    public String toString() {
        return "ScannedBleDetails{" +
                "bleId='" + bleId + '\'' +
                ", bleRssi='" + bleRssi + '\'' +
                '}';
    }
}
