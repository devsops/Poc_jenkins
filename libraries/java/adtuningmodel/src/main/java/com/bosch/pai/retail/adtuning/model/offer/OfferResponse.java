package com.bosch.pai.retail.adtuning.model.offer;


import com.google.gson.annotations.SerializedName;

public class OfferResponse {
    @SerializedName("userId")
    private String userId;
    @SerializedName("promoCode")
    private String promoCode;

    @SerializedName("offerResponseStatus")
    private OfferResponseStatus offerResponseStatus;
    @SerializedName("offerActiveDuration")
    private Long offerActiveDuration;

    public OfferResponse() {
        //default constructor
    }

    public OfferResponse(String userId, String promoCode, OfferResponseStatus accepted, Long offerActiveDuration) {
        this.userId = userId;
        this.promoCode = promoCode;
        this.offerResponseStatus = accepted;
        this.offerActiveDuration = offerActiveDuration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }


    public OfferResponseStatus getOfferResponseStatus() {
        return offerResponseStatus;
    }

    public void setOfferResponseStatus(OfferResponseStatus offerResponseStatus) {
        this.offerResponseStatus = offerResponseStatus;
    }

    public Long getOfferActiveDuration() {
        return offerActiveDuration;
    }

    public void setOfferActiveDuration(Long offerActiveDuration) {
        this.offerActiveDuration = offerActiveDuration;
    }

    @Override
    public String toString() {
        return "OfferResponse{" +
                "userId=" + userId +
                ", promoCode=" + promoCode +
                ", offerResponseStatus=" + offerResponseStatus +
                ", offerActiveDuration=" + offerActiveDuration +
                '}';
    }
}
