package com.bosch.pai.retail.db.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sub_session_details_ios")
public class SubSessionDetailIos {

    @Id
    private String id = new ObjectId().toHexString();
    private String storeId;
    private String sessionId;
    private String userId;


    private String siteName;
    private String locationName;
    private Long startTime;
    private Long endTime;
    private Boolean isValid;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSubSessionId() {
        return id;
    }

    public void setSubSessionId(String subSessionId) {
        this.id = subSessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "SubSessionDetailIos{" +
                "subSessionId=" + id +
                ", sessionId=" + sessionId +
                ", userId=" + userId +
                ", site=" + siteName +
                ", location=" + locationName +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isValid=" + isValid +
                '}';
    }
}
