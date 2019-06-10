package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hug5kor on 2/14/2018.
 */


public class StoreConfig {
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("storeDescription")
    private String storeDescription;
    @SerializedName("snapshotThreshold")
    private double snapshotThreshold;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public double getSnapshotThreshold() {
        return snapshotThreshold;
    }


    public void setSnapshotThreshold(double snapshotThreshold) {
        this.snapshotThreshold = snapshotThreshold;
    }

    @Override
    public String toString() {
        return "StoreConfig{" +
                "storeId='" + storeId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", storeDescription='" + storeDescription + '\'' +
                ", snapshotThreshold=" + snapshotThreshold +
                '}';
    }


}
