package com.bosch.pai.retail.analytics.model.offeranalytics;

import com.google.gson.annotations.SerializedName;

public class OfferDetails {
    @SerializedName("name")
    private String name;
    @SerializedName("displayedOfferCount")
    private long displayedOfferCount;
    @SerializedName("acceptedOfferCount")
    private long acceptedOfferCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDisplayedOfferCount() {
        return displayedOfferCount;
    }

    public void setDisplayedOfferCount(long displayedOfferCount) {
        this.displayedOfferCount = displayedOfferCount;
    }

    public long getAcceptedOfferCount() {
        return acceptedOfferCount;
    }

    public void setAcceptedOfferCount(long acceptedOfferCount) {
        this.acceptedOfferCount = acceptedOfferCount;
    }

    @Override
    public String toString() {
        return "OfferDetails{" +
                "name='" + name + '\'' +
                ", displayedOfferCount=" + displayedOfferCount +
                ", acceptedOfferCount=" + acceptedOfferCount +
                '}';
    }
}
