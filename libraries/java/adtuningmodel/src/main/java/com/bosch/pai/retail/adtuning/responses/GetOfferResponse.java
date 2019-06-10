package com.bosch.pai.retail.adtuning.responses;

import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class GetOfferResponse {

    @SerializedName("promoDetailList")
    private Set<PromoDetail> promoDetailList = new HashSet<>();
    @SerializedName("statusMessage")
    private StatusMessage statusMessage;


    public GetOfferResponse() {
        //Required by jacksonmessageconverter
    }

    public Set<PromoDetail> getPromoDetailList() {
        return new HashSet<>(promoDetailList);
    }

    public void setPromoDetailList(Set<PromoDetail> promoDetailList) {
        this.promoDetailList = promoDetailList != null ? promoDetailList : new HashSet<PromoDetail>();
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "GetOfferResponse [promoDetailList=" + promoDetailList + ", statusMessage="
                + statusMessage + "]";
    }


}