package com.bosch.pai.retail.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sjn8kor on 3/12/2018.
 */

public class SaveLocationBaymapRequest {
    
    @SerializedName("locationbaymapping")
    private String locationbaymapping;

    @SerializedName("overrideRequired")
    private boolean overrideRequired;

    public SaveLocationBaymapRequest() {
        //default
    }

    public SaveLocationBaymapRequest(String locationbaymapping, boolean overrideRequired) {
        this.locationbaymapping = locationbaymapping;
        this.overrideRequired = overrideRequired;
    }

    public String getLocationbaymapping() {
        return locationbaymapping;
    }

    public void setLocationbaymapping(String locationbaymapping) {
        this.locationbaymapping = locationbaymapping;
    }

    public boolean isOverrideRequired() {
        return overrideRequired;
    }

    public void setOverrideRequired(boolean overrideRequired) {
        this.overrideRequired = overrideRequired;
    }

    @Override
    public String toString() {
        return "SaveLocationBaymapRequest{" +
                "locationbaymapping='" + locationbaymapping + '\'' +
                ", overrideRequired=" + overrideRequired +
                '}';
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SaveLocationBaymapRequest that = (SaveLocationBaymapRequest) o;

        if (overrideRequired != that.overrideRequired) return false;
        return locationbaymapping != null ? locationbaymapping.equals(that.locationbaymapping) : that.locationbaymapping == null;
    }

    @Override
    public int hashCode() {
        int result = locationbaymapping != null ? locationbaymapping.hashCode() : 0;
        result = 31 * result + (overrideRequired ? 1 : 0);
        return result;
    }
}
