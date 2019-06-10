package com.bosch.pai.session;


import com.google.gson.annotations.SerializedName;

public class SessionInfo {

    @SerializedName("sessionId")
    private String sessionId;
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;
    @SerializedName("isValid")
    private Boolean isValid;

    public SessionInfo(String userId) {
        this.userId = userId;
        this.isValid = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public Boolean isValid() {
        return isValid;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
