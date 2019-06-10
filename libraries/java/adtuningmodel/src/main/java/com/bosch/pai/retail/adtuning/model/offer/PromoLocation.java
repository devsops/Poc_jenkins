package com.bosch.pai.retail.adtuning.model.offer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hug5kor on 1/19/2018.
 */

public class PromoLocation implements Serializable {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;


    public PromoLocation(String siteName, String locationName) {
        this.siteName = siteName;
        this.locationName = locationName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PromoLocation that = (PromoLocation) o;

        if (siteName != null ? !siteName.equals(that.siteName) : that.siteName != null)
            return false;
        return locationName != null ? locationName.equals(that.locationName) : that.locationName == null;
    }

    @Override
    public int hashCode() {
        int result = siteName != null ? siteName.hashCode() : 0;
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        return result;
    }
}