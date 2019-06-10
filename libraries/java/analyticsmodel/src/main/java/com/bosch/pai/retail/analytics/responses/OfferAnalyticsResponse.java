package com.bosch.pai.retail.analytics.responses;


import com.google.gson.annotations.SerializedName;

public class OfferAnalyticsResponse {

    @SerializedName("companyId")
    private String companyId;
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("displayedOfferCount")
    private long displayedOfferCount;
    @SerializedName("acceptedOfferCount")
    private long acceptedOfferCount;
    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;

    public OfferAnalyticsResponse() {
        //sonar
    }

    public OfferAnalyticsResponse(String companyId, String storeId, String siteName, String locationName, long displayedOfferCount, long acceptedOfferCount, long startTime, long endTime) {
        this.companyId = companyId;
        this.storeId = storeId;
        this.siteName = siteName;
        this.locationName = locationName;
        this.displayedOfferCount = displayedOfferCount;
        this.acceptedOfferCount = acceptedOfferCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }


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

    public long getDisplayedOfferCount() {
        return displayedOfferCount;
    }

    public void setDisplayedOfferCount(long displayedOfferCount) {
        this.displayedOfferCount = displayedOfferCount;
    }

    public long getAcceptedOfferCount() {
        return acceptedOfferCount;
    }

    public void setAcceptedOfferCount(long acceptedOfferCount) {
        this.acceptedOfferCount = acceptedOfferCount;
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
        return "OfferAnalyticsResponse{" +
                "companyId=" + companyId +
                ", storeId=" + storeId +
                ", siteName=" + siteName +
                ", locationName=" + locationName +
                ", displayedOfferCount=" + displayedOfferCount +
                ", acceptedOfferCount=" + acceptedOfferCount +
                ", startTime=" + startTime +
                " endTime=" + endTime +
                '}';
    }

}
