package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationBayMap {

    @SerializedName("locationCode")
    private String locationCode;

    @SerializedName("bayList")
    private List<String> bayList= new ArrayList<>();

    public LocationBayMap() {
        //
    }

    public LocationBayMap(String locationCode, List<String> bayList) {
        this.locationCode = locationCode;
        this.bayList = bayList !=null ? bayList : new ArrayList<String>();
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public List<String> getBayList() {
        return new ArrayList<>(bayList);
    }

    public void setBayList(List<String> bayList) {
        this.bayList = bayList != null ? bayList : new ArrayList<String>();
    }

    @Override
    public String toString() {
        return "LocationBayMap{" +
                "locationCode='" + locationCode + '\'' +
                ", bayList=" + bayList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationBayMap that = (LocationBayMap) o;

        if (locationCode != null ? !locationCode.equals(that.locationCode) : that.locationCode != null)
            return false;
        return bayList != null ? bayList.equals(that.bayList) : that.bayList == null;

    }

    @Override
    public int hashCode() {
        int result = locationCode != null ? locationCode.hashCode() : 0;
        result = 31 * result + (bayList != null ? bayList.hashCode() : 0);
        return result;
    }
}