package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.offeranalytics.OfferDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserOfferAnalyticsResponse {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("hierarchyType")
    private String hierarchyType;
    @SerializedName("details")
    private List<OfferDetails> details = new ArrayList<>();

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

    public List<OfferDetails> getDetails() {
        return new ArrayList<>(details);
    }

    public void setDetails(List<OfferDetails> details) {
        this.details = details != null ? details : new ArrayList<OfferDetails>();
    }

    @Override
    public String toString() {
        return "UserOfferAnalyticsResponse{" +
                "siteName='" + siteName + '\'' +
                ", hierarchyType='" + hierarchyType + '\'' +
                ", details=" + details +
                '}';
    }
}
