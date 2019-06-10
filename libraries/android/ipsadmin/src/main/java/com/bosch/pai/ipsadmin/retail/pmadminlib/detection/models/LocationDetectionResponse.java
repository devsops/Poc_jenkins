package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sjn8kor on 1/9/2018.
 */

public class LocationDetectionResponse implements Serializable {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("probability")
    private Double probability;
    @SerializedName("locationUpdateMap")
    private Map<String, Double> locationUpdateMap;

    public LocationDetectionResponse() {
        //default constructor
    }

    public LocationDetectionResponse(LocationDetectionResponse locationDetectionResponse){
        this.siteName = locationDetectionResponse.getSiteName();
        this.locationName = locationDetectionResponse.getLocationName();
        this.timestamp = locationDetectionResponse.getTimestamp();
        this.probability = locationDetectionResponse.getProbability();
        this.locationUpdateMap = locationDetectionResponse.getLocationUpdateMap();
    }

    public LocationDetectionResponse(String siteName, String locationName, String timestamp, Double probability, Map<String, Double> locationUpdateMap) {
        this.siteName = siteName;
        this.locationName = locationName;
        this.timestamp = timestamp;
        this.probability = probability;
        this.locationUpdateMap = locationUpdateMap;
    }

    public Map<String, Double> getLocationUpdateMap() {
        return locationUpdateMap;
    }

    public void setLocationUpdateMap(Map<String, Double> locationUpdateMap) {
        this.locationUpdateMap = locationUpdateMap;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "LocationDetectionResponse{" +
                "siteName='" + siteName + '\'' +
                ", locationName='" + locationName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", probability=" + probability +
                ", locationUpdateMap=" + locationUpdateMap +
                '}';
    }
}
