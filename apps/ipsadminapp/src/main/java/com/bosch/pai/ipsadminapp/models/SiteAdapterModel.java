package com.bosch.pai.ipsadminapp.models;

import java.io.Serializable;

public class SiteAdapterModel implements Serializable {

    private String siteName;

    private int locationCount;

    public SiteAdapterModel(String siteName, int locationCount) {
        this.siteName = siteName;
        this.locationCount = locationCount;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getLocationCount() {
        return locationCount;
    }

    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }

    @Override
    public String toString() {
        return "SiteAdapterModel{" +
                "siteName='" + siteName + '\'' +
                ", locationCount=" + locationCount +
                '}';
    }
}
