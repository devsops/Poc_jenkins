package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BaymapDetail implements Serializable {

    @SerializedName("companyId")
    private String companyId;

    @SerializedName("storeId")
    private String storeId;

    @SerializedName("siteName")
    private String siteName;

    @SerializedName("locationName")
    private String locationName;

    @SerializedName("bays")
    private Set<String> bays = new HashSet<>();

    public BaymapDetail() {
        //sonar
    }


    public BaymapDetail(String companyId, String storeId, String siteName, String locationName, Set<String> bays) {
        this.companyId = companyId;
        this.storeId = storeId;
        this.siteName = siteName;
        this.locationName = locationName;
        this.bays = bays != null ? bays : new HashSet<String>();
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Set<String> getBays() {
        return new HashSet<>(bays);
    }

    public void setBays(Set<String> bays) {
        this.bays = bays !=null ? bays : new HashSet<String>();
    }

    @Override
    public String toString() {
        return "BaymapDetail{" +
                "companyId='" + companyId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", locationName='" + locationName + '\'' +
                ", bays=" + bays +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaymapDetail that = (BaymapDetail) o;

        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null)
            return false;
        if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
        if (siteName != null ? !siteName.equals(that.siteName) : that.siteName != null)
            return false;
        if (locationName != null ? !locationName.equals(that.locationName) : that.locationName != null)
            return false;
        return bays != null ? bays.equals(that.bays) : that.bays == null;
    }

    @Override
    public int hashCode() {
        int result = companyId != null ? companyId.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (bays != null ? bays.hashCode() : 0);
        return result;
    }
}