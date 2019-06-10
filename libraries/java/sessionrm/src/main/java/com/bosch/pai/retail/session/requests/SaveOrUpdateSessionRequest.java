package com.bosch.pai.retail.session.requests;

import com.google.gson.annotations.SerializedName;

public class SaveOrUpdateSessionRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("sessionId")
    private String sessionId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("timeZoneID")
    private String timeZoneID;

    public SaveOrUpdateSessionRequest() {
        //sonar
    }

    public SaveOrUpdateSessionRequest(String userId, String sessionId, String siteName, String locationName, String timeZoneID) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.siteName = siteName;
        this.locationName = locationName;
        this.timeZoneID = timeZoneID;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getTimeZoneID() {
        return timeZoneID;
    }

    @Override
    public String toString() {
        return "SaveOrUpdateSessionRequest{" +
                "UserId=" + userId +
                ", sessionId=" + sessionId +
                ", siteName=" + siteName +
                ", locationName=" + locationName +
                ", timeZoneID=" + timeZoneID +
                '}';
    }
}
