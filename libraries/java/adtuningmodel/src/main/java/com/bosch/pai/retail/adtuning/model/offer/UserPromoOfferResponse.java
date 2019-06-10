package com.bosch.pai.retail.adtuning.model.offer;

import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserPromoOfferResponse implements Serializable {

    @SerializedName("siteName")
    private String siteName;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("hierarchyDetails")
    private List<HierarchyDetail> hierarchyDetails= new ArrayList<>();
    @SerializedName("storeId")
    private String storeId;
    @SerializedName("messageCode")
    private String messageCode;
    @SerializedName("userId")
    private String userId;
    @SerializedName("offerResponseStatus")
    private OfferResponseStatus.STATUS offerResponseStatus;
    @SerializedName("offerActiveDuration")
    private Long offerActiveDuration;
    @SerializedName("userResponseTimeStamp")
    private Long userResponseTimeStamp;

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

    public List<HierarchyDetail> getHierarchyDetails() {
        return new ArrayList<>(hierarchyDetails);
    }

    public void setHierarchyDetails(List<HierarchyDetail> hierarchyDetails) {
        this.hierarchyDetails = hierarchyDetails !=null?hierarchyDetails:new ArrayList<HierarchyDetail>();
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OfferResponseStatus.STATUS getOfferResponseStatus() {
        return offerResponseStatus;
    }

    public void setOfferResponseStatus(OfferResponseStatus.STATUS offerResponseStatus) {
        this.offerResponseStatus = offerResponseStatus;
    }

    public Long getOfferActiveDuration() {
        return offerActiveDuration;
    }

    public void setOfferActiveDuration(Long offerActiveDuration) {
        this.offerActiveDuration = offerActiveDuration;
    }

    public Long getUserResponseTimeStamp() {
        return userResponseTimeStamp;
    }

    public void setUserResponseTimeStamp(Long userResponseTimeStamp) {
        this.userResponseTimeStamp = userResponseTimeStamp;
    }

    @Override
    public String toString() {
        return "UserPromoOfferResponse{" +
                "siteName='" + siteName + '\'' +
                ", locationName='" + locationName + '\'' +
                ", hierarchyDetails=" + hierarchyDetails +
                ", storeId='" + storeId + '\'' +
                ", messageCode='" + messageCode + '\'' +
                ", userId='" + userId + '\'' +
                ", offerResponseStatus=" + offerResponseStatus +
                ", offerActiveDuration=" + offerActiveDuration +
                ", userResponseTimeStamp=" + userResponseTimeStamp +
                '}';
    }
}
