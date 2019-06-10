package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sjn8kor on 8/17/2018.
 */

public class LocationFrequency {

    @SerializedName("locationName")
    private String locationName;
    @SerializedName("maxOfferCount")
    private long maxOfferCount;

    public LocationFrequency() {
    }

    public LocationFrequency(String locationName, long maxOfferCount) {
        this.locationName = locationName;
        this.maxOfferCount = maxOfferCount;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getMaxOfferCount() {
        return maxOfferCount;
    }

    public void setMaxOfferCount(long maxOfferCount) {
        this.maxOfferCount = maxOfferCount;
    }

    @Override
    public String toString() {
        return "LocationFrequency{" +
                "locationName='" + locationName + '\'' +
                ", maxOfferCount=" + maxOfferCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationFrequency that = (LocationFrequency) o;

        if (maxOfferCount != that.maxOfferCount) return false;
        return locationName != null ? locationName.equals(that.locationName) : that.locationName == null;
    }

    @Override
    public int hashCode() {
        int result = locationName != null ? locationName.hashCode() : 0;
        result = 31 * result + (int) (maxOfferCount ^ (maxOfferCount >>> 32));
        return result;
    }
}
