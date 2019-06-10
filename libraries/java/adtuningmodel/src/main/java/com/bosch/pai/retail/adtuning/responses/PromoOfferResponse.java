package com.bosch.pai.retail.adtuning.responses;

import com.bosch.pai.retail.adtuning.model.offer.LocationPromoDetail;
import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.adtuning.model.offer.PromoLocation;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hug5kor on 1/22/2018.
 */

public class PromoOfferResponse {
    @SerializedName("promoDetailList")
    private List<LocationPromoDetail> promoDetailList= new ArrayList<>();

    @Override
    public String toString() {
        return "PromoOfferResponse{" +
                "promoDetailList=" + promoDetailList +
                ", statusMessage=" + statusMessage +
                '}';
    }

    @SerializedName("statusMessage")
    private StatusMessage statusMessage;

    public List<LocationPromoDetail> getPromoDetailList() {
        return new ArrayList<>(promoDetailList);
    }

    public void setPromoDetailList(List<LocationPromoDetail> promoDetailList) {
        this.promoDetailList = promoDetailList != null ? promoDetailList : new ArrayList<LocationPromoDetail>();
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }


}
