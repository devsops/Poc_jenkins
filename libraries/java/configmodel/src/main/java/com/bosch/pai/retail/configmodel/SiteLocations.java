package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sjn8kor on 3/9/2018.
 */

public class SiteLocations {

    @SerializedName("companyId")
    private String companyId;

    @SerializedName("storeId")
    private String storeId;

    @SerializedName("siteName")
    private String siteName;

    @SerializedName("locations")
    private Set<String> locations = new HashSet<>();

    public SiteLocations() {
        //defalut
    }

    public SiteLocations(String companyId, String storeId, String siteName, Set<String> locations) {
        this.companyId = companyId;
        this.storeId = storeId;
        this.siteName = siteName;
        this.locations = locations != null ? locations : new HashSet<String>();
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Set<String> getLocations() {
        return new HashSet<>(locations);
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations != null ? locations : new HashSet<String>();
    }

    @Override
    public String toString() {
        return "SiteLocations{" +
                "companyId='" + companyId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", locations=" + locations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiteLocations that = (SiteLocations) o;

        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null)
            return false;
        if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
        if (siteName != null ? !siteName.equals(that.siteName) : that.siteName != null)
            return false;
        return locations != null ? locations.equals(that.locations) : that.locations == null;
    }

    @Override
    public int hashCode() {
        int result = companyId != null ? companyId.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (locations != null ? locations.hashCode() : 0);
        return result;
    }
}
