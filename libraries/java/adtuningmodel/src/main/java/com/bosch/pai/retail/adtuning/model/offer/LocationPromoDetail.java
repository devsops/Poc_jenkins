package com.bosch.pai.retail.adtuning.model.offer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hug5kor on 1/19/2018.
 */

public class LocationPromoDetail implements Serializable {

    @SerializedName("locationName")
    private String locationName;
    @SerializedName("siteName")
    private String siteName;
    @SerializedName("promoDetail")
    private List<PromoDetail> promoDetail= new ArrayList<>();

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }



    @Override
    public String toString() {
        return "LocationPromoDetail{" +
                "locationName='" + locationName + '\'' +
                ", promoDetail=" + promoDetail +
                '}';
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<PromoDetail> getPromoDetail() {
        return new ArrayList<>(promoDetail);
    }

    public void setPromoDetail(List<PromoDetail> promoDetail) {
        this.promoDetail = promoDetail !=null? promoDetail : new ArrayList<PromoDetail>();
    }
}
