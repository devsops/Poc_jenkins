package com.bosch.pai.retail.configmodel;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SiteLocationHierarchyDetail implements Serializable {

    @SerializedName("companyId")
    private String companyId;
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;

    @SerializedName("hierarchies")
    private List<HierarchyDetail> hierarchies = new ArrayList<>();

    public SiteLocationHierarchyDetail() {
        //sonar
    }

    public SiteLocationHierarchyDetail(String companyId, String storeId, String siteName, String locationName, List<HierarchyDetail> locationCateDeptBrand) {
        this.companyId = companyId;
        this.storeId = storeId;
        this.siteName = siteName;
        this.locationName = locationName;
        this.hierarchies = locationCateDeptBrand != null ? locationCateDeptBrand : new ArrayList<HierarchyDetail>();
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

    public List<HierarchyDetail> getHierarchies() {
        return new ArrayList<>(hierarchies);
    }

    public void setHierarchies(List<HierarchyDetail> hierarchies) {
        this.hierarchies = hierarchies != null ? hierarchies : new ArrayList<HierarchyDetail>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiteLocationHierarchyDetail that = (SiteLocationHierarchyDetail) o;

        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null)
            return false;
        if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
        if (siteName != null ? !siteName.equals(that.siteName) : that.siteName != null)
            return false;
        if (locationName != null ? !locationName.equals(that.locationName) : that.locationName != null)
            return false;
        return hierarchies != null ? hierarchies.equals(that.hierarchies) : that.hierarchies == null;
    }

    @Override
    public int hashCode() {
        int result = companyId != null ? companyId.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (hierarchies != null ? hierarchies.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SiteLocationDetails{" +
                "companyId='" + companyId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", locationName='" + locationName + '\'' +
                ", hierarchies=" + hierarchies +
                '}';
    }
}
