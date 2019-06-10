package com.bosch.pai.retail.analytics.model.dwelltime;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class LocationDwellTime {
    @SerializedName("companyId")
    private String companyId;
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("averageDuration")
    private Float averageDuration;
    @SerializedName("userCount")
    private Integer userCount;
    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Float getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(Float averageDuration) {
        this.averageDuration = averageDuration;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "LocationDwellTime{" +
                "companyId=" + companyId +
                ", siteName=" + siteName +
                ", storeId=" + storeId +
                ", locationName=" + locationName +
                ", averageDuration=" + averageDuration +
                ", userCount=" + userCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}