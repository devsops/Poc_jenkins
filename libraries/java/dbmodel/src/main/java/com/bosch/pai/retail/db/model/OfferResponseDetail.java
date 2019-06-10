package com.bosch.pai.retail.db.model;


import com.bosch.pai.retail.adtuning.model.offer.OfferResponseStatus;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;

/**
 * Created by SJN8KOR on 1/25/2017.
 */
@Document
public class OfferResponseDetail {

    @Id
    private String id = new ObjectId().toHexString();
    @Field("SITE_NAME")
    private String siteName;
    @Field("LOCATION_CODE")
    private String locationName;
    @Field("MESSAGE_CODE")
    private String messageCode;
    @Field("USER_ID")
    private String userId;
    @Field("IS_ACCEPTED")
    private OfferResponseStatus offerResponseStatus;
    @Field("OFFER_ACTIVE_DURATION")
    private Long offerActiveDuration;
    @Field("USER_RESPONSE_TIMESTAMP")
    private Timestamp userResponseTimeStamp;

    public String getOfferResponseId() {
        return id;
    }

    public void setOfferResponseId(String offerResponseId) {
        this.id = offerResponseId;
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

    public void setOfferResponseStatus(OfferResponseStatus offerResponseStatus) {
        this.offerResponseStatus = offerResponseStatus;
    }

    public OfferResponseStatus getOfferResponseStatus() {
        return offerResponseStatus;
    }

    public Long getOfferActiveDuration() {
        return offerActiveDuration;
    }

    public void setOfferActiveDuration(Long offerActiveDuration) {
        this.offerActiveDuration = offerActiveDuration;
    }

    public Timestamp getUserResponseTimeStamp() {
        return this.userResponseTimeStamp != null ? (Timestamp) this.userResponseTimeStamp.clone() : null;
    }

    public void setUserResponseTimeStamp(Timestamp userResponseTimeStamp) {
        this.userResponseTimeStamp = userResponseTimeStamp != null ? (Timestamp) userResponseTimeStamp.clone() : null;
    }

    @Override
    public String toString() {
        return "OfferResponseDetail{" +
                "OfferResponseId=" + id +
                ", siteName=" + siteName +
                ", locationName=" + locationName +
                ", messageCode=" + messageCode +
                ", userId=" + userId +
                ", offerResponseStatus=" + offerResponseStatus +
                ", offerActiveDuration=" + offerActiveDuration +
                ", userResponseTimeStamp=" + userResponseTimeStamp +
                '}';
    }
}
