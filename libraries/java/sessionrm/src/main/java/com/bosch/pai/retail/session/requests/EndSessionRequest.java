package com.bosch.pai.retail.session.requests;

import com.google.gson.annotations.SerializedName;

public class EndSessionRequest {
    @SerializedName("userId")
    private String userId;
    @SerializedName("sessionId")
    private String sessionId;

    public EndSessionRequest() {
        //sonar
    }

    public EndSessionRequest(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "SaveOrUpdateSessionRequest{" +
                "UserId=" + userId +
                ", sessionId=" + sessionId +
                '}';
    }
}
