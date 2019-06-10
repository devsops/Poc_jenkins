package com.bosch.pai.retail.productoffer.model;

/**
 * Created by hug5kor on 11/20/2017.
 */
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Document
public class ValidPromoDetail {

    @Id
    private String validPromoDetailId = new ObjectId().toHexString();
    @Field("SITE_NAME")
    private String siteName;
    @Field("LOCATION_NAME")
    private String locationName;
    @Field("LOCATION_CODE")
    private String locationCode;
    @Field("DISPLAY_MESSAGE")
    private String displayMessage;
    @Field("MESSAGE_CODE")
    private String messageCode;
    @Field("RANK")
    private Integer rank;
    @Field("PROMO_START_DATE")
    private Date promoStartDate;
    @Field("PROMO_END_DATE")
    private Date promoEndDate;


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

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Date getPromoStartDate() {
        return promoStartDate != null ? (Date) promoStartDate.clone() : null;
    }

    public void setPromoStartDate(Date promoStartDate) {
        this.promoStartDate = promoStartDate!= null ? (Date) promoStartDate.clone() : null;
    }

    public Date getPromoEndDate() {
        return promoEndDate != null ? (Date) promoEndDate.clone() : null;
    }

    public void setPromoEndDate(Date promoEndDate) {
        this.promoEndDate = promoEndDate != null ? (Date) promoEndDate.clone() : null;
    }

}

