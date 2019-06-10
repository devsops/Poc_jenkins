package com.bosch.pai.ipsadminapp.models;

import java.io.Serializable;

public class LocationAdapterModel implements Serializable {

    private SENSOR sensorType;
    private String locationName;

    public LocationAdapterModel() {
        //default
    }

    public LocationAdapterModel(String locationName) {
        this.locationName = locationName;
    }

    public LocationAdapterModel(SENSOR sensorType, String locationName) {
        this.sensorType = sensorType;
        this.locationName = locationName;
    }

    public SENSOR getSensorType() {
        return sensorType;
    }

    public void setSensorType(SENSOR sensorType) {
        this.sensorType = sensorType;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return "LocationAdapterModel{" +
                "locationName='" + locationName + '\'' +
                '}';
    }

    public enum SENSOR {

        WIFI,
        BLE

    }
}
