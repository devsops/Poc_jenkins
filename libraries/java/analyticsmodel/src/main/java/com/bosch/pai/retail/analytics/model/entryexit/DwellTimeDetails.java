package com.bosch.pai.retail.analytics.model.entryexit;

import com.google.gson.annotations.SerializedName;

public class DwellTimeDetails {

    @SerializedName("entries")
    private String entries;
    @SerializedName("userCount")
    private String userCount;
    @SerializedName("averageDuration")
    private String averageDuration;

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

    public String getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(String averageDuration) {
        this.averageDuration = averageDuration;
    }

    @Override
    public String toString() {
        return "DwellTimeDetails{" +
                "entries='" + entries + '\'' +
                ", userCount='" + userCount + '\'' +
                ", averageDuration='" + averageDuration + '\'' +
                '}';
    }
}
