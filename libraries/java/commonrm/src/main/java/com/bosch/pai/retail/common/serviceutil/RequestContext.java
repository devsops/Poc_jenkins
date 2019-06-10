package com.bosch.pai.retail.common.serviceutil;


import com.google.gson.annotations.SerializedName;

public class RequestContext {
    @SerializedName("userId")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
