package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.entryexit.DwellTimeDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserDwellTimeAnalyticsResponse {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("hierarchyType")
    private String hierarchyType;
    @SerializedName("hierarchyDwellTimeDetails")
    private List<DwellTimeDetails> hierarchyDwellTimeDetails = new ArrayList<>();

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(String hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public List<DwellTimeDetails> getHierarchyDwellTimeDetails() {
        return new ArrayList<>(hierarchyDwellTimeDetails);
    }

    public void setHierarchyDwellTimeDetails(List<DwellTimeDetails> hierarchyDwellTimeDetails) {
        this.hierarchyDwellTimeDetails = hierarchyDwellTimeDetails != null ? hierarchyDwellTimeDetails : new ArrayList<DwellTimeDetails>();
    }

    @Override
    public String toString() {
        return "UserDwellTimeAnalyticsResponse{" +
                "siteName='" + siteName + '\'' +
                ", hierarchyType='" + hierarchyType + '\'' +
                ", dwellTimeDetails=" + hierarchyDwellTimeDetails +
                '}';
    }
}
