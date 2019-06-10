package com.bosch.pai.retail.analytics.requests;


import com.google.gson.annotations.SerializedName;

public class GetHeatMapRequest {

    @SerializedName("site")
    private String site;
    @SerializedName("location")
    private String location;
    @SerializedName("startTime")
    private Long startTime;
    @SerializedName("endTime")
    private Long endTime;

    public GetHeatMapRequest() {
        //sonar
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

    @Override
    public String toString() {
        return "GetHeatMapRequest{" +
                "site=" + site + 
                ", location=" + location + 
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
