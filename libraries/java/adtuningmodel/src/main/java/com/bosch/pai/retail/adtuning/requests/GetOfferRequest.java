package com.bosch.pai.retail.adtuning.requests;

import com.google.gson.annotations.SerializedName;

public class GetOfferRequest {

    @SerializedName("userId")
    private String userId;
    @SerializedName("site")
    private String site;
    @SerializedName("location")
    private String location;

    public GetOfferRequest() {
        //sonar
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "GetOfferRequest{" +
                "userId=" + userId +
                ", site=" + site +
                ", location=" + location +
                '}';
    }
}
