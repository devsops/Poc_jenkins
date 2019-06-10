package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sjn8kor on 1/26/2018.
 */

public class SiteConfiguration implements Serializable {

    @SerializedName("minSitePredictCount")
    private int minSitePredictCount;
    @SerializedName("minLocationProbability")
    private Double minLocationProbability;

    public SiteConfiguration(SiteConfiguration siteConfiguration) {
        this.minSitePredictCount = siteConfiguration.getMinSitePredictCount();
        this.minLocationProbability = siteConfiguration.getMinLocationProbability();
    }


    public SiteConfiguration(int minSitePredictCount, Double minLocationProbability) {
        this.minSitePredictCount = minSitePredictCount;
        this.minLocationProbability = minLocationProbability;
    }

    public int getMinSitePredictCount() {
        return minSitePredictCount;
    }

    public void setMinSitePredictCount(int minSitePredictCount) {
        this.minSitePredictCount = minSitePredictCount;
    }

    public Double getMinLocationProbability() {
        return minLocationProbability;
    }

    public void setMinLocationProbability(Double minLocationProbability) {
        this.minLocationProbability = minLocationProbability;
    }

    @Override
    public String toString() {
        return "SiteConfiguration{" +
                "minSitePredictCount=" + minSitePredictCount +
                ", minLocationProbability=" + minLocationProbability +
                '}';
    }
}
