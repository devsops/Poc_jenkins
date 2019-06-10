package com.bosch.pai.retail.analytics.requests;

import com.google.gson.annotations.SerializedName;

public class GetDwellTimeDetailRequest {

    @SerializedName("startTime")
    private Long startTime;
    @SerializedName("endTime")
    private Long endTime;
    @SerializedName("site")
    private String site;
    @SerializedName("location")
    private String location;

    public GetDwellTimeDetailRequest() {
        //sonar
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "GetDwellTimeDetailRequest{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", site=" + site +
                ", location=" + location + 
                '}';
    }
}
