package com.bosch.pai.retail.configmodel;


import com.google.gson.annotations.SerializedName;

public class DemoAdConfiguration {

    @SerializedName("companyName")
    private String companyName;
    @SerializedName("storeName")
    private String storeName;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("configuration")
    private String configuration;
    @SerializedName("startTime")
    private Long startTime;
    @SerializedName("endTime")
    private Long endTime;
    @SerializedName("isLatest")
    private Boolean isLatest;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getLatest() {
        return isLatest;
    }

    public void setLatest(Boolean latest) {
        isLatest = latest;
    }

    @Override
    public String toString() {
        return "DemoAdConfiguration{" +
                "companyName='" + companyName + '\'' +
                ", storeName='" + storeName + '\'' +
                ", siteName='" + siteName + '\'' +
                ", configuration='" + configuration + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isLatest=" + isLatest +
                '}';
    }
}
