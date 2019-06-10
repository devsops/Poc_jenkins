package com.bosch.pai.session;


import com.google.gson.annotations.SerializedName;

public class SubSessionInfo {

    @SerializedName("userId")
    private String userId;
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;
    @SerializedName("isValid")
    private Boolean isValid;

    public SubSessionInfo(String userId, String siteName, String locationName) {
        this.userId = userId;
        this.siteName = siteName;
        this.locationName = locationName;
        this.isValid = false;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getLocationName() {
        return locationName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Boolean getValid() {
        return isValid;
    }
}
