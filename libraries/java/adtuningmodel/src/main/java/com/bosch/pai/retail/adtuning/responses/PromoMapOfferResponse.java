package com.bosch.pai.retail.adtuning.responses;

import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.adtuning.model.offer.PromoLocation;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by hug5kor on 1/31/2018.
 */

public class PromoMapOfferResponse {
    @SerializedName("promoDetailMap")
    private Map<PromoLocation,List<PromoDetail>> promoDetailMap;
    @SerializedName("statusMessage")
    private StatusMessage statusMessage;


    public PromoMapOfferResponse() {
        //Required by jacksonmessageconverter
    }

    public Map<PromoLocation,List<PromoDetail>> getPromoDetailMap() {
        return promoDetailMap;
    }

    public void setPromoDetailMap(Map<PromoLocation,List<PromoDetail>> promoDetailMap) {
        this.promoDetailMap = promoDetailMap;
    }



    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "GetOfferResponse [promoDetailMap=" + promoDetailMap + ", statusMessage="
                + statusMessage + "]";
    }
}
