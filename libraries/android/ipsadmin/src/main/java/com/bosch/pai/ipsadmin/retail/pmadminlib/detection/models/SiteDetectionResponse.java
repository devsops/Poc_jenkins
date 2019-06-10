package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sjn8kor on 1/9/2018.
 */

public class SiteDetectionResponse implements Serializable {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("timestamp")
    private String timestamp;

    public SiteDetectionResponse() {
        //default constructor
    }

    public SiteDetectionResponse(SiteDetectionResponse siteDetectionResponse) {
        this.siteName = siteDetectionResponse.getSiteName();
        this.timestamp = siteDetectionResponse.getTimestamp();
    }


    public SiteDetectionResponse(String siteName, String timestamp) {
        this.siteName = siteName;
        this.timestamp = timestamp;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SiteDetectionResponse{" +
                "siteName='" + siteName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
