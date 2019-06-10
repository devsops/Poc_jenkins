package com.bosch.pai.retail.analytics.model.entryexit;

import com.google.gson.annotations.SerializedName;

public class HeatMapDetails {

    @SerializedName("entries")
    private String entries;
    @SerializedName("userCount")
    private String userCount;

    public String getEntries() {
        return entries;
    }

    public void setEntries(String entries) {
        this.entries = entries;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return "HeatMapDetails{" +
                "entries='" + entries + '\'' +
                ", userCount='" + userCount + '\'' +
                '}';
    }
}
