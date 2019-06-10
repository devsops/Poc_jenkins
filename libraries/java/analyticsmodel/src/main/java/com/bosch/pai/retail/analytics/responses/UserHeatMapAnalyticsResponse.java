package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.entryexit.HeatMapDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserHeatMapAnalyticsResponse {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("hierarchyType")
    private String hierarchyType;
    @SerializedName("hierarchyHeatMapDetails")
    private List<HeatMapDetails> hierarchyHeatMapDetails = new ArrayList<>();

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

    public List<HeatMapDetails> getHierarchyHeatMapDetails() {
        return new ArrayList<>(hierarchyHeatMapDetails);
    }

    public void setHierarchyHeatMapDetails(List<HeatMapDetails> hierarchyHeatMapDetails) {
        this.hierarchyHeatMapDetails = hierarchyHeatMapDetails != null ? hierarchyHeatMapDetails : new ArrayList<HeatMapDetails>();
    }

    @Override
    public String toString() {
        return "UserHeatMapAnalyticsResponse{" +
                "siteName='" + siteName + '\'' +
                ", hierarchyType='" + hierarchyType + '\'' +
                ", hierarchyHeatMapDetails=" + hierarchyHeatMapDetails +
                '}';
    }
}
