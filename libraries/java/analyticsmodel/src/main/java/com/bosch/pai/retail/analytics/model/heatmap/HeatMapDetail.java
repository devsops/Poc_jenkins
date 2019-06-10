package com.bosch.pai.retail.analytics.model.heatmap;


import com.google.gson.annotations.SerializedName;

public class HeatMapDetail {

    @SerializedName("companyName")
    private String companyName;
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("userCount")
    private Integer userCount;
    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;


    public HeatMapDetail() {
        //default constructor

    }

    public HeatMapDetail(String companyName, String storeId, String siteName, String locationName, int userCount, long startTime, long endTime) {
        this.companyName = companyName;
        this.storeId = storeId;
        this.siteName = siteName;
        this.locationName = locationName;
        this.userCount = userCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
        return "HeatMapDetail{" +
                "companyName=" + companyName +
                ", storeId=" + storeId +
                ", siteName=" + siteName +
                ", locationName=" + locationName +
                ", userCount=" + userCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
